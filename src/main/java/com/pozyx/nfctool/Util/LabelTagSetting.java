package com.pozyx.nfctool.Util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class LabelTagSetting extends NumberTagSetting implements TagSetting {

    public LabelTagSetting(int offset, SettingType type, String name, boolean display){
        super(offset, type, name, display);
        assert(type == SettingType.UINT8);    //make sure the type is boolean
    }

    @Override
    public View getView(Context ctx){
        view = new TextView(ctx);
        ((TextView)view).setText(Long.toString(this.value));
        return view;
    }

    @Override
    void setViewValue(long val){
        ((TextView)view).setText(Long.toString(val));
    }

    @Override
    Long getViewValue(){ return (long) this.value; }
}
