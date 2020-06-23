package com.pozyx.nfctool.Util;

import android.content.Context;
import android.view.View;

import java.security.InvalidParameterException;
import java.text.ParseException;

public interface TagSetting {
    String getName();

    int getOffset();

    int getLength();

    long getValue();

    boolean isDisplayed();

    View getView(Context ctx);

    void setValue(long value) throws ParseException;

    byte[] serialize();

    long deserialize(byte[] value) throws InvalidParameterException;
}
