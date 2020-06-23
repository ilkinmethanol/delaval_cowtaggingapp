package com.pozyx.nfctool.Util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Switch;
import android.widget.TableRow;

public class SliderTagSetting extends NumberTagSetting implements TagSetting {
    public SliderTagSetting(int offset, SettingType type, String name, boolean display){
        super(offset, type, name, display);
        assert(type == SettingType.BOOLEAN);    //make sure the type is boolean
    }

    @Override
    public View getView(Context ctx){
        view = new Switch(ctx);
        //TableRow.LayoutParams ly = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
        //ly.gravity = Gravity.CENTER;
        //view.setLayoutParams(ly);
        ((Switch)view).setChecked(this.value == 1);
        return view;
    }

    @Override
    void setViewValue(long val){
        ((Switch)view).setChecked(val == 1);
    }

    @Override
    Long getViewValue(){
        return (long) (((Switch)view).isChecked() ? 1:0);
    }
}
