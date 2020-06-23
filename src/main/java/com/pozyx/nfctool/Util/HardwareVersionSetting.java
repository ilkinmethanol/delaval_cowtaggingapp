package com.pozyx.nfctool.Util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class HardwareVersionSetting extends NumberTagSetting implements TagSetting {

    public HardwareVersionSetting(int offset, SettingType type, String name, boolean display){
        super(offset, type, name, display);
        assert(type == SettingType.UINT8);    //make sure the type is boolean
    }

    @Override
    public View getView(Context ctx){
        view = new TextView(ctx);
        this.setViewValue(this.value);
        return view;
    }

    @Override
    void setViewValue(long val){
        if (this.value == 0) {
            ((TextView)view).setText("v1.2");
        } else if (this.value == 1) {
            ((TextView) view).setText("v1.3");
        } else {
            ((TextView) view).setText("Unknown (" + Long.toString(this.value) + ")");
        }
    }

    @Override
    Long getViewValue(){ return (long) this.value; }
}
