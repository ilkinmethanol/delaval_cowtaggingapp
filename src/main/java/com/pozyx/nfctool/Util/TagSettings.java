package com.pozyx.nfctool.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TagSettings {

    byte[] read_memory;
    TagSetting[] settings = new NumberTagSetting[] {
            new LabelTagSetting(0, SettingType.BOOLEAN, "Changed", true),
            new HardwareVersionSetting(1, SettingType.UINT8, "Hardware version", true),
            new FirmwareVersionSetting(2, SettingType.UINT8, "Firmware version", true),
            new StatusSetting(3, SettingType.BOOLEAN, "State", true),
            new LabelTagSetting(4, SettingType.UINT32, "Id", true),
            new LabelTagSetting(8, SettingType.UINT32, "Blink index", true),
            new LabelTagSetting(12, SettingType.UINT16, "Sleep Time (ms)", false),
            new LabelTagSetting(14, SettingType.UINT16, "Variation (ms)", false),

            new LabelTagSetting(16, SettingType.UINT16, "Settings updaterate", false),
            new LabelTagSetting(18, SettingType.UINT8, "Accelerometer", false),
            new LabelTagSetting(19, SettingType.UINT8, "Channel", false),
            new LabelTagSetting(20, SettingType.UINT8, "Preamble", false),
            new LabelTagSetting(21, SettingType.UINT8, "Datarate", false),
            new LabelTagSetting(22, SettingType.UINT8, "PRF", false),

            //Delaval specific settings
            new LabelTagSetting(23, SettingType.UINT16, "Threshold", true),
            new LabelTagSetting(25, SettingType.UINT8, "Minimum Trigger Count", true),
            new LabelTagSetting(26, SettingType.UINT8, "Samples Per Interval", true),

            new LabelTagSetting(27, SettingType.UINT8, "Power", true),
            new LabelTagSetting(28, SettingType.UINT16, "0", false),
            new LabelTagSetting(30, SettingType.UINT8, "0", false),
            new LabelTagSetting(31, SettingType.UINT8, "pgdly", true),
            new LabelTagSetting(32, SettingType.UINT8, "0", false),

            //Delaval specific settings
            new SliderTagSetting(33, SettingType.BOOLEAN, "0", false),
            new LabelTagSetting(34, SettingType.BOOLEAN, "Enable temperature adjustments", true),
            new LabelTagSetting(35, SettingType.BOOLEAN, "Aggregation algorithm", true),
            new LabelTagSetting(36, SettingType.UINT8, "Minimum active blinks", true),
            new LabelTagSetting(37, SettingType.UINT8, "Minimum level active blinks", true),
    };

    public HashMap<String, TagSetting> settingsmap = new HashMap<String, TagSetting>() {{
        put("Changed", settings[0]);
        put("Hardware version", settings[1]);
        put("Firmware version", settings[2]);
        put("State", settings[3]);
        put("Id", settings[4]);
        put("Blink", settings[5]);
        put("Threshold", settings[14]);
        put("Minimum trigger count", settings[15]);
        put("Samples per interval", settings[16]);
        put("Power", settings[17]);
        put("Pgdly", settings[20]);
        put("Enable temperature adjustments", settings[23]);
        put("Aggregation algorithm", settings[24]);
        put("Minimum active blinks", settings[25]);
        put("Minimum level active blinks", settings[26]);
    }};

    public String[] settings_order = {"State", "Id", "Hardware version", "Firmware version", "Blink", "Threshold", "Minimum trigger count", "Samples per interval", "Power", "Pgdly", "Enable temperature adjustments", "Aggregation algorithm", "Minimum active blinks", "Minimum level active blinks","Changed"};

    ArrayList<TagSetting> displayed_settings = new ArrayList<TagSetting>();
    public TagSettings(){
        for (TagSetting setting : settings){
            if (setting.isDisplayed()){
                displayed_settings.add(setting);
            }
        }
    }

    public ArrayList<TagSetting> get_settings(){    // Don't use the settingsmap.keys(), we need to be able to edit the keys array to sort the keys and remove access to certain values.
        return displayed_settings;
    }

    public long get_setting(String setting){
        return settingsmap.get(setting).getValue();
    }

    public void set_setting(String setting, long value) throws ParseException{
        settingsmap.get(setting).setValue(value);
    }

    public HashMap<Integer, byte[]> getChangedBlocks(){
        byte[] changed_memory = this.serialize();
        HashMap<Integer, byte[]> changed_blocks = new HashMap<Integer, byte[]>();
        int i =0;
        while (i < read_memory.length){
            if (read_memory[i] != changed_memory[i]){
                changed_memory[0] = 1;
                int block_nr = (i/4);
                byte[] block = Arrays.copyOfRange(changed_memory, block_nr*4, (block_nr+1)*4);
                changed_blocks.put((int)Math.floor(i/4), block);
                i+= 4-(i%4);    //skip checking the rest of the block
            }
            i++;
        }
        //If the data has changed we need to update the first block
        if (changed_memory[0] == 1){
            if (!changed_blocks.containsKey(0)){
                byte[] block = Arrays.copyOfRange(changed_memory, 0, 4);
                changed_blocks.put(0, block);
            }
        }
        return changed_blocks;
    }

    byte[] serialize(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            for (TagSetting setting : settings) {
                stream.write(setting.serialize());
            }
            return stream.toByteArray();
        } catch (IOException e){
            throw new NullPointerException("Could not serialize settings");
        }
    }

    public void updateSetting(TagSetting setting){
        byte[] setting_serialized = setting.serialize();
        for (int i=0; i<setting.getLength(); i++){
            read_memory[setting.getOffset()+i] = setting_serialized[i];
        }
    }

    public void deserialize(byte[] nfc_memory){
        for (TagSetting setting : settings){
            byte[] setting_memory = Arrays.copyOfRange(nfc_memory, setting.getOffset(), setting.getOffset() + setting.getLength());
            setting.deserialize(setting_memory);
        }
        read_memory = serialize();
    }

    public void deserializeStatus(byte[] statusblock){
        settingsmap.get("Changed").deserialize(new byte[]{statusblock[0]});
        settingsmap.get("Hardware version").deserialize(new byte[]{statusblock[1]});
        settingsmap.get("Firmware version").deserialize(new byte[]{statusblock[2]});
        settingsmap.get("State").deserialize(new byte[]{statusblock[3]});
        read_memory = serialize();
    }
}