package com.pozyx.nfctool.Util;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

public class NumberTagSetting implements TagSetting {
    int offset;
    SettingType type;
    long value;
    String name;
    boolean display;
    View view;

    public NumberTagSetting(int offset, SettingType type, String name, boolean display) {
        this.offset = offset;
        this.type = type;
        this.value = 0;
        this.name = name;
        this.display = display;
    }

    Long getViewValue(){
        return Long.parseLong(((EditText) view).getText().toString());
    }

    void setViewValue(long val){
        ((EditText) view).setText(Long.toString(val));
    }

    public View getView(Context ctx){
        view = new EditText(ctx);
        ((EditText)view).setText(Long.toString(this.value));
        ((EditText)view).setInputType(InputType.TYPE_CLASS_NUMBER);
        ((EditText)view).setIncludeFontPadding(false);

        return view;
    }

    public String getName(){
        return name;
    }

    public int getOffset(){
        return this.offset;
    }

    public int getLength() {
        return this.type.getSize();
    }

    public long getValue(){
        return getViewValue();
    }

    public boolean isDisplayed(){
        return display;
    }

    public void setValue(long value) throws ParseException {
        switch (this.type){
            case BOOLEAN:
                if(value != 1 && value != 0) {
                    throw new NumberFormatException(this.name + " is not a boolean");
                }
            case UINT8:
                if (value < 0 && value > 0xFF) {
                    throw new NumberFormatException(this.name + " is not a uint8");
                }
                break;
            case UINT16:
                if(value < 0 && value > 0xFFFF) {
                    throw new NumberFormatException(this.name + " is not a uint16");
                }
                break;
            case UINT32:
                if(value < 0 && value > 0xFFFFFFFF) {
                    throw new NumberFormatException(this.name + " is not a uint32");
                }
                break;
        }
        setViewValue(value);
        this.value = value;
    }

    public byte[] serialize() {
        if (view != null) {
            this.value = getViewValue();
        }
        byte[] data;
        switch(this.type){
            case BOOLEAN:
            case UINT8:
                data = new byte[1];
                data[0] = (byte) (this.value & 0xFF);
                return data;
            case UINT16:
                data = new byte[2];
                data[1] = (byte) (this.value & 0xff);
                data[0] = (byte) (this.value>>8 & 0xff);
                return data;
            case UINT32:
                data = new byte[4];
                data[3] = (byte) (this.value & 0xff);
                data[2] = (byte) (this.value>>8 & 0xff);
                data[1] = (byte) (this.value>>16 & 0xff);
                data[0] = (byte) (this.value>>24 & 0xff);
                return data;
            default:
                return null;
        }
    }

    public long deserialize(byte[] value) throws InvalidParameterException{
        if (this.type.getSize() != value.length){
            throw new InvalidParameterException("Invalid length");
        }
        switch(this.type){
            case UINT8:
                this.value = (long) (value[0] & 0xff);
                break;
            case UINT16:
                this.value = (long) (value[1] & 0xFF) | (value[0] & 0xFF) <<8 ;
                break;
            case UINT32:
                this.value = (long) 0x00000000ffffffffL & ((value[0] & 0xff) << 24) | (value[1] & 0xFF) << 16| (value[2] & 0xFF) << 8| (value[3] & 0xFF);
                break;
            case BOOLEAN:
                this.value = (long) (value[0] & 0xFF);
                break;
            default:
                break;
        }
        if (this.view != null) {
            setViewValue(this.value);
        }
        return this.value;
    }
}
