package com.sn.blackdianqi.util;

import android.text.TextUtils;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.blue.BluetoothLeService;

import java.util.HashMap;
import java.util.Map;


public class BlueUtils {


    /**
     * 判断蓝牙是否已连接
     * @return
     */
    public static boolean isConnected() {
        BluetoothLeService bluetoothLeService = MyApplication.getInstance().mBluetoothLeService;
        if (bluetoothLeService != null && MyApplication.getInstance().gattCharacteristic != null
                && Prefer.getInstance().isBleConnected()) {
            return true;
        }
        return false;
    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return String
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:将16进制的数据转为数组
     *
     * @param data
     * @return byte[]
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch3;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch3 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch3 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch4;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch4 = (hex_char2 - 48); //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch4 = hex_char2 - 55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch3 + int_ch4;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }

    /**
     * 16进制转10进制
     * @param content
     * @return
     */
    public static int covert16TO10(String content){
        content = content.toUpperCase();
        int number=0;
        String [] HighLetter = {"A","B","C","D","E","F"};
        Map<String,Integer> map = new HashMap<>();
        for(int i = 0;i <= 9;i++){
            map.put(i+"",i);
        }
        for(int j= 10;j<HighLetter.length+10;j++){
            map.put(HighLetter[j-10],j);
        }
        String[]str = new String[content.length()];
        for(int i = 0; i < str.length; i++){
            str[i] = content.substring(i,i+1);
        }
        for(int i = 0; i < str.length; i++){
            number += map.get(str[i])*Math.pow(16,str.length-1-i);
        }
        return number;
    }

    /**
     * 转义蓝牙名称
     * @param oriBlueName
     * @return
     */
    public static String transferBlueName(String oriBlueName){
        if (TextUtils.isEmpty(oriBlueName)) {
            return null;
        }
        oriBlueName = oriBlueName.replace("<","C");
        oriBlueName = oriBlueName.replace(":","A");
        oriBlueName = oriBlueName.replace(";","B");
        oriBlueName = oriBlueName.replace("=","D");
        oriBlueName = oriBlueName.replace(">","E");
        oriBlueName = oriBlueName.replace("?","F");
        return oriBlueName;
    }


    public static void main(String[] args) {
        System.out.println(covert16TO10("0a"));
    }
}
