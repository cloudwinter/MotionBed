package com.sn.blackdianqi.util;

import android.text.TextUtils;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.blue.BluetoothLeService;

import java.util.HashMap;
import java.util.Map;


public class BlueUtils {


    /**
     * 判断蓝牙是否已连接
     *
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
     * 二进制转16进制
     *
     * @param str2
     * @return
     */
    public static String str2To16(String str2) {
        if (str2 == null || str2.equals("")) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int len = str2.length();
        System.out.println("原数据长度：" + (len / 8) + "字节");

        for (int i = 0; i < len / 4; i++) {
            //每4个二进制位转换为1个十六进制位
            String temp = str2.substring(i * 4, (i + 1) * 4);
            int tempInt = Integer.parseInt(temp, 2);
            String tempHex = Integer.toHexString(tempInt).toUpperCase();
            sb.append(tempHex);
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
     *
     * @param content
     * @return
     */
    public static int covert16TO10(String content) {
        content = content.toUpperCase();
        int number = 0;
        String[] HighLetter = {"A", "B", "C", "D", "E", "F"};
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i <= 9; i++) {
            map.put(i + "", i);
        }
        for (int j = 10; j < HighLetter.length + 10; j++) {
            map.put(HighLetter[j - 10], j);
        }
        String[] str = new String[content.length()];
        for (int i = 0; i < str.length; i++) {
            str[i] = content.substring(i, i + 1);
        }
        for (int i = 0; i < str.length; i++) {
            number += map.get(str[i]) * Math.pow(16, str.length - 1 - i);
        }
        return number;
    }

    public static String covert10TO16(int number) {
        if (number == 0) {
            return "00";
        }
        int i = 0;
        char[] S = new char[100];
        while (number != 0) {
            int t = number % 16;
            if (t >= 0 && t < 10) {
                S[i] = (char) (t + '0');
                i++;
            } else {
                S[i] = (char) (t + 'A' - 10);
                i++;
            }
            number = number / 16;
        }
        StringBuilder sb = new StringBuilder();
        for (int j = i - 1; j >= 0; j--) {
            sb.append(S[j]);
        }
        String result = sb.toString();
        if (result.length() == 1) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * 转义蓝牙名称
     *
     * @param oriBlueName
     * @return
     */
    public static String transferBlueName(String oriBlueName) {
        if (TextUtils.isEmpty(oriBlueName)) {
            return null;
        }
        oriBlueName = oriBlueName.replace("<", "C");
        oriBlueName = oriBlueName.replace(":", "A");
        oriBlueName = oriBlueName.replace(";", "B");
        oriBlueName = oriBlueName.replace("=", "D");
        oriBlueName = oriBlueName.replace(">", "E");
        oriBlueName = oriBlueName.replace("?", "F");
        return oriBlueName;
    }

    /**
     * 计算校验和
     *
     * @param data
     * @return
     */
    public static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        data = data.replaceAll(" ", "");
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
//        covert10TO16(total);
//        System.out.println(decToHex(total));
//        int mod = total % 256;
//        System.out.println(mod);
//        String hex = Integer.toHexString(mod);
//        len = hex.length();
//        // 如果不够校验位的长度，补0,这里用的是两位校验
//        if (len < 2) {
//            hex = "0" + hex;
//        }
        return decToHex(total).toUpperCase();
    }


    /**
     * int 10进制转16进制
     * 并高位在后，低位在前
     *
     * @param dec
     * @return
     */
    private static String decToHex(int dec) {
        String hex = "";
        while (dec != 0) {
            String h = Integer.toString(dec & 0xff, 16);
            if ((h.length() & 0x01) == 1)
                h = '0' + h;
            hex = hex + h;
            dec = dec >> 8;
        }
        return hex;
    }


    /**
     * 二进制字符串转16进制字符串
     *
     * @param bString
     * @return
     */
    public static String hexString2To16hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 16进制字符转二进制字符串
     *
     * @param hexString
     * @return
     */
    public static String hexString16To2hexString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    public static void main(String[] args) {
        System.out.println(covert10TO16(20));
        System.out.println(covert10TO16(70));
//        System.out.println(hexString16To2hexString("06"));
//
//
//        System.out.println(makeChecksum("FF FF FF FF 01 00 02 19 01 08 30 50 00 01 00 00 01 00 00 01 03 01 01"));

//        String cmd = "FF FF FF FF 01 00 02 19 A1 08 30 50 00 01 00 00 01 00 00 01 03 01 01".replace(" ", "");
//        System.out.println(calculateChecksum(cmd.getBytes()));
    }


}
