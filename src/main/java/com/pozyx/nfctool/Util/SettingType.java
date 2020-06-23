package com.pozyx.nfctool.Util;

enum SettingType {
    UINT8(1), UINT16(2), UINT32(4), BOOLEAN(1);
    private int size = 0;
    private SettingType(final int size) {this.size = size;}
    public int getSize(){
        return this.size;
    }
}