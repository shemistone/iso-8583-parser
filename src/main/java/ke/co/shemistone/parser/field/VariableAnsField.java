/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.co.shemistone.parser.field;

import ke.co.shemistone.parser.Converters;
import ke.co.shemistone.parser.Strings;

/**
 *
 * @author bmg
 */
public class VariableAnsField implements Field {

    private int id;
    private int length;
    private int maxLength;
    private int lengthOfLength;
    private Encoding lengthEncoding;
    private Encoding valueEncoding;
    private String value = "";
    private String encodedValue = "";

    public VariableAnsField(IsoField isoField) {
        this.id = isoField.getId();
        this.length = isoField.getLength();
        this.maxLength = isoField.getMaxLength();
        this.lengthOfLength = String.valueOf(this.maxLength).length();
        this.lengthEncoding = isoField.getLengthEncoding();
        this.valueEncoding = isoField.getValueEncoding();
    }

    @Override
    public String encode() {
        if (this.value.length() == 0) {
            return this.encodedValue;
        }
        if (this.value.length() > this.maxLength) {
            throw new PackException(String.format("Length of field %d (%s) is more than %d", this.id, this.value.length(), this.maxLength));
        }
        this.length = this.value.length();
        String encodedLength;
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
        switch (this.valueEncoding) {
            case ASC:
            default:
                this.encodedValue = this.value;
                break;
        }
        this.encodedValue = encodedLength + this.encodedValue;
        return this.encodedValue;
    }

    @Override
    public int decode(String head) {
        switch (this.lengthEncoding) {
            case BCD:
                if (this.lengthOfLength % 2 != 0) {
                    this.lengthOfLength = this.lengthOfLength + 1;
                }
                this.lengthOfLength = this.lengthOfLength / 2;
                this.length = Integer.parseInt(Converters.asciiToHex(head.substring(0, this.lengthOfLength)));
                break;
            case ASC:
            default:
                this.length = Integer.parseInt(head.substring(0, this.lengthOfLength));
                break;
        }
        if (this.length > this.maxLength) {
            throw new UnpackException(String.format("Length of field %d (%d) is more than %d", this.id, this.length, this.maxLength));
        }
        int nextHeadIndex;
        switch (this.valueEncoding) {
            case ASC:
            default:
                nextHeadIndex = this.lengthOfLength + this.length;
                this.encodedValue = head.substring(this.lengthOfLength, nextHeadIndex);
                break;
        }
        this.value = head.substring(this.lengthOfLength, this.lengthOfLength + this.length);
        return nextHeadIndex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getEncodedValue() {
        return this.encodedValue;
    }

    @Override
    public void setEncodedValue(String encodedValue) {
        this.encodedValue = encodedValue;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
