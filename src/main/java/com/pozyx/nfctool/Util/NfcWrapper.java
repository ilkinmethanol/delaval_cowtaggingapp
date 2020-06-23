package com.pozyx.nfctool.Util;

import android.nfc.tech.NfcV;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

//https://www.st.com/content/ccc/resource/technical/document/application_note/group0/5e/83/22/10/07/50/45/ab/DM00347279/files/DM00347279.pdf/jcr:content/translations/en.DM00347279.pdf
//We can see here we can write/read single blocks and (fast) read multiple blocks
public class NfcWrapper {

    private byte[] getWriteCmd(int address, byte[] data) {
        //prepared apdu command
        return new byte[]{
                (byte) 0x02, //flags
                (byte) 0x21, //write single block
                (byte) address, //address 1
                data[0], data[1], data[2], data[3]}; //4 bytes for the 1 block to be written
    }

    private byte[] getFastReadCmd(int address, int length){
        return new byte[]{
                (byte) 0x02,    //flags
                (byte) 0x23,//0x23    //read multiple blocks
                (byte) address, //address 1
                (byte) length   //amount of blocks to read
        };
    }

    public void sendInterrupt(NfcV tag){
        try {
            tag.transceive(new byte[] {(byte)0x00, (byte)0xA9, (byte)0x02, (byte)0x80});
        } catch (IOException e){
            //throw new NullPointerException("Failed triggering interrupt");
        }
    }

    public void writeBlocks(NfcV tag, HashMap<Integer, byte[]> blocks) {
        try {
            for (int block_nr : blocks.keySet()) {
                byte[] command = getWriteCmd(block_nr, blocks.get(block_nr));
                tag.transceive(command);
            }
        } catch (IOException e){
            throw new NullPointerException("Failed writing blocks");
        }
    }

    public byte[] readStatusBlocks(NfcV tag){
        try {
            tag.connect();
            byte[] command = getFastReadCmd(0, 0);
            byte[] userdata = tag.transceive(command);
            userdata = Arrays.copyOfRange(userdata, 1, userdata.length);    //filter out first 00
            tag.close();
            return userdata;
        } catch (IOException e) {
            throw new NullPointerException("Reading failed");
        }
    }

    public byte[] readSettingsBlocks(NfcV tag) {
        try {
            tag.connect();
            byte[] command = getFastReadCmd(0, 5);
            byte[] userdata = tag.transceive(command);
            userdata = Arrays.copyOfRange(userdata, 1, userdata.length);    //filter out first 00
            command = getFastReadCmd(6, 6);
            byte[] userdata2 = tag.transceive(command);
            tag.close();
            userdata2 = Arrays.copyOfRange(userdata2, 1, userdata.length);    //filter out first 00
            byte[] result = new byte[userdata.length + userdata2.length];
            System.arraycopy(userdata, 0, result, 0, userdata.length);
            System.arraycopy(userdata2, 0, result, userdata.length, userdata2.length);
            return result;
        } catch (IOException e) {
            return null;
        }
    }
}