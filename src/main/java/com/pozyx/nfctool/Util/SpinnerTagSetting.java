package com.pozyx.nfctool.Util;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

public class SpinnerTagSetting extends NumberTagSetting implements TagSetting {

    HashMap<String, Long> possible_values;
    HashMap<Long, String> rev_possible_values;
    ArrayAdapter<String> dataAdapter;

    public SpinnerTagSetting(int offset, SettingType type, String name, boolean display, HashMap<String, Long> possible_values){
        super(offset, type, name, display);
        this.possible_values = possible_values;

        //We know the values are unique
        this.rev_possible_values = new HashMap<Long, String>();
        for (String key : possible_values.keySet()){
            this.rev_possible_values.put(possible_values.get(key), key);
        }
    }

    @Override
    public View getView(Context ctx){
        view = new Spinner(ctx);
        dataAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, new ArrayList<String>(possible_values.keySet()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)view).setAdapter(dataAdapter);
        view.setPadding(0, 20, 0, 20);
        setViewValue(this.value);
        return view;
    }

    @Override
    void setViewValue(long val){
        ((Spinner)view).setSelection(dataAdapter.getPosition(this.rev_possible_values.get(val)));
    }

    @Override
    Long getViewValue(){
        return possible_values.get(((Spinner)view).getSelectedItem().toString());
    }
}
