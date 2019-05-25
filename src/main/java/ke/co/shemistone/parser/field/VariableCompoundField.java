/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.co.shemistone.parser.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ke.co.shemistone.parser.Converters;
import ke.co.shemistone.parser.Strings;

/**
 *
 * @author shemistone
 */
public class VariableCompoundField implements CompoundField {

    private int id;
    private int length;
    private int maxLength;
    private int lengthOfLength;
    private Encoding lengthEncoding;
    private Encoding valueEncoding;
    private String value = "";
    private String encodedValue = "";
    private final IsoField isoField;
    private boolean secBitmapSet = false;
    private final int[] bitmap = new int[1 + 128];
    private final Map<Integer, Field> fields = new HashMap<>();

    public VariableCompoundField(IsoField isoField) {
        this.id = isoField.getId();
        this.length = isoField.getLength();
        this.maxLength = isoField.getMaxLength();
        this.lengthOfLength = String.valueOf(this.maxLength).length();
        this.lengthEncoding = isoField.getLengthEncoding();
        this.valueEncoding = isoField.getValueEncoding();
        this.isoField = isoField;
        List<IsoField> isoSubFields = this.isoField.getIsoFields();
        for (int fieldId = 0; fieldId < isoSubFields.size(); fieldId++) {
            IsoField isoSubField = isoSubFields.get(fieldId);
            Field field = this.createField(isoSubField);
            this.fields.put(fieldId, field);
        }
    }

    @Override
    public void setValue(int fieldId, String value) {
        if (fieldId > 1) {
            this.bitmap[fieldId] = 1;
        }
        if (fieldId > 64 && !this.secBitmapSet) {
            this.bitmap[1] = 1;
            this.secBitmapSet = true;
        }
        Field field = this.getField(fieldId);
        field.setValue(value);
    }

    @Override
    public String getValue(int fieldId) {
        return this.getField(fieldId).getValue();
    }

    @Override
    public void setValue(int fieldId, int minorFieldId, String value) {
        this.setValue(fieldId, "");
        CompoundField field = (CompoundField) this.getField(fieldId);
        field.setValue(minorFieldId, value);
    }

    @Override
    public String getValue(int fieldId, int subFieldId) {
        CompoundField field = (CompoundField) this.getField(fieldId);
        return field.getValue(subFieldId);
    }

    @Override
    public void setField(int fieldId, Field field) {
        this.fields.put(fieldId, field);
    }

    @Override
    public Field getField(int fieldId) {
        return this.fields.get(fieldId);
    }

    @Override
    public void setField(int fieldId, int minorFieldId, Field subField) {
        this.setValue(fieldId, "");
        CompoundField field = (CompoundField) this.getField(fieldId);
        field.setField(minorFieldId, subField);
    }

    @Override
    public Field getField(int fieldId, int subFieldId) {
        CompoundField field = (CompoundField) this.fields.get(fieldId);
        return field.getField(subFieldId);
    }

    @Override
    public String encode() {
        this.value = "";
        int fieldId = 0;
        try {
            for (fieldId = 0; fieldId < this.fields.size(); fieldId++) {
                Field field = this.getField(fieldId);
                String fieldValue = "";
                String fieldEncodedValue;
                switch (fieldId) {
                    case 1:
                        for (int bitmapIndex = 1; bitmapIndex < this.bitmap.length; bitmapIndex++) {
                            fieldValue += String.valueOf(this.bitmap[bitmapIndex]);
                        }
                        field.setValue(fieldValue.substring(0, 64));
                        fieldEncodedValue = field.encode();
                        if (this.secBitmapSet) {
                            field.setValue(fieldValue.substring(64));
                            fieldEncodedValue += field.encode();
                        } else {
                            fieldValue = fieldValue.substring(0, 64);
                        }
                        break;
                    default:
                        fieldValue = field.getValue();
                        fieldEncodedValue = field.encode();
                        break;
                }
                this.value += fieldValue;
                this.encodedValue += fieldEncodedValue;
                if (field.getEncodedValue().length() > 0) {
                    System.out.printf("Field %d.%d => %s (%s)\n", this.id, fieldId, field.getEncodedValue(), field.getValue());
                }
            }

            if (this.value.length() == 0) {
                return this.encodedValue;
            }
            if (this.encodedValue.length() > this.maxLength) {
                throw new PackException(String.format("Length of field %d (%d) is more than %d", this.id, this.value.length(), this.maxLength));
            }
            String encodedLength;
            this.length = this.value.length();
            encodedLength = String.valueOf(this.length);
            encodedLength = Strings.prepend(encodedLength, "0", this.lengthOfLength);
            switch (this.lengthEncoding) {
                case BCD:
                    encodedLength = Converters.hexToAscii(encodedLength);
                    break;
                case ASC:
                default:
                    break;
            }
            this.encodedValue = encodedLength + this.encodedValue;
        } catch (RuntimeException ex) {
            throw new PackException(String.format("Error packing field %d.%d", this.id, fieldId), ex);
        }
        return this.encodedValue;
    }

    @Override
    public int decode(String head) {
        this.value = "";
        int valueIndex;
        int headIndex = 0;
        int fieldId = 0;
        try {
            switch (this.lengthEncoding) {
                case BCD:
                    if (this.lengthOfLength % 2 != 0) {
                        valueIndex = (this.lengthOfLength + 1) / 2;
                    } else {
                        valueIndex = this.lengthOfLength / 2;
                    }
                    this.length = Integer.parseInt(Converters.asciiToHex(head.substring(0, valueIndex)));
                    break;
                case ASC:
                default:
                    valueIndex = this.lengthOfLength;
                    this.length = Integer.parseInt(head.substring(0, valueIndex));
                    break;
            }
            if (this.length > this.maxLength) {
                throw new UnpackException(String.format("Length of field %d (%d) is more than %d", this.id, this.length, this.maxLength));
            }

            headIndex = valueIndex;
            for (fieldId = 0; fieldId < this.fields.size(); fieldId++) {
                Field field = this.fields.get(fieldId);
                String fieldValue = "";
                int nextHeadIndex;
                switch (fieldId) {
                    case 0:
                        nextHeadIndex = field.decode(head.substring(headIndex));
                        headIndex += nextHeadIndex;
                        fieldValue = field.getValue();
                        break;
                    case 1:
                        nextHeadIndex = field.decode(head.substring(headIndex));
                        headIndex += nextHeadIndex;
                        fieldValue = field.getValue();
                        if (fieldValue.startsWith("1")) {
                            nextHeadIndex = field.decode(head.substring(headIndex));
                            headIndex += nextHeadIndex;
                            fieldValue += field.getValue();
                            field.setValue(fieldValue);
                        }
                        for (int bitmapIndex = 0; bitmapIndex < fieldValue.length(); bitmapIndex++) {
                            this.bitmap[bitmapIndex + 1] = Integer.parseInt(fieldValue.substring(bitmapIndex, bitmapIndex + 1));
                        }
                        break;
                    default:
                        if (this.bitmap[fieldId] == 1) {
                            nextHeadIndex = field.decode(head.substring(headIndex));
                            headIndex += nextHeadIndex;
                            fieldValue = field.getValue();
                        }
                        break;
                }
                this.value += fieldValue;
                if (field.getValue().length() > 0) {
                    System.out.printf("Field %d.%d => %s (%s)\n", this.id, fieldId, field.getEncodedValue(), field.getValue());
                }
            }
        } catch (RuntimeException ex) {
            throw new UnpackException(String.format("Error unpacking field %d.%d", this.id, fieldId), ex);
        }
        return headIndex;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Field createField(IsoField isoField) {
        try {
            Class<?> cls = Class.forName(isoField.getCls());
            Constructor<?> cons = cls.getConstructor(IsoField.class);
            return (Field) cons.newInstance(isoField);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getEncodedValue() {
        return encodedValue;
    }

    @Override
    public void setEncodedValue(String encodedValue) {
        this.encodedValue = encodedValue;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getLengthOfLength() {
        return lengthOfLength;
    }

    public void setLengthOfLength(int lengthOfLength) {
        this.lengthOfLength = lengthOfLength;
    }

    public Encoding getLengthEncoding() {
        return lengthEncoding;
    }

    public void setLengthEncoding(Encoding lengthEncoding) {
        this.lengthEncoding = lengthEncoding;
    }

    public Encoding getValueEncoding() {
        return valueEncoding;
    }

    public void setValueEncoding(Encoding valueEncoding) {
        this.valueEncoding = valueEncoding;
    }

}
