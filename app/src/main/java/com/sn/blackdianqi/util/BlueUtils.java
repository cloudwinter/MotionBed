package com.sn.blackdianqi.util;

import android.text.TextUtils;
import android.util.Log;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.blue.BluetoothLeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /**
     * 10进制转16进制
     *
     * @param number
     * @return
     */
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

    // CRC-16-MODBUS (初始值0xFFFF, 多项式0x8005, 结果取反)
    public static String crc16Modbus(String cmd) {
        byte[] data = hexStringToByteArray(cmd);
        int crc = 0xFFFF;

        for (byte b : data) {
            crc ^= (b & 0xFF);       // 与当前字节异或
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) { // 检查最低位
                    crc = (crc >> 1) ^ 0xA001; // 右移并异或多项式反转值
                } else {
                    crc >>= 1;
                }
            }
        }
//        LogUtils.e("CRC==result", (crc & 0xFFFF) + "");
        String result = decToHex(crc & 0xFFFF).toUpperCase();
        StringBuilder sb = new StringBuilder();
        sb.append(result);
        if (result.length() < 4) {
            for (int i = 0; i < (4 - result.length()); i++) {
                sb.append("0");
            }
        }
//        LogUtils.e("CRC==result", sb.toString());
        return sb.toString();
    }

    /**
     * 将16进制字符串转换为字节数组
     *
     * @param hexString 16进制字符串(如: "01020304")
     * @return 对应的字节数组
     */
    public static byte[] hexStringToByteArray(String hexString) {
        // 移除所有空白字符
        hexString = hexString.replaceAll("\\s", "");

        // 检查长度是否为偶数
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even length");
        }

        byte[] result = new byte[hexString.length() / 2];

        for (int i = 0; i < result.length; i++) {
            int index = i * 2;
            // 提取每两个字符作为一个16进制数
            result[i] = (byte) Integer.parseInt(hexString.substring(index, index + 2), 16);
        }

        return result;
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

    /**
     * 一个字节转8位bit 从低位到高位
     *
     * @param {} cmd
     */
    public static String byteToBit(byte b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            // 依次取出每一位，判断是 0 还是 1
            int bit = (b >> i) & 1;
            sb.append(bit);
        }
        // 由于是从低位到高位，这里将字符串反转一下，更符合一般的二进制展示习惯
        return sb.reverse().toString();
    }

    /**
     * 将字符串每隔一定长度切分放入一个数组中
     *
     * @param {*} str 原始字符
     * @param {*} length 每隔几个字符
     *            return 返回数组
     */
    public static List<String> strToArray(String str, int itemLength) {
        List<String> array = new ArrayList<>();
        if (TextUtils.isEmpty(str) || str.length() % itemLength != 0) {
            Log.e("strToArray 字符为空或长度异常", str + "  length:" + itemLength);
            return array;
        }
        for (int i = 0; i < str.length(); i += itemLength) {
            array.add(str.substring(i, i + itemLength));
        }
        return array;
    }

    /**
     * 将16进制字符串转换为byte
     * @param hexString 16进制字符串（如"FF"、"f"、"0A"）
     * @return 转换后的byte值，转换失败返回0
     */
    public static byte hexStringToByte(String hexString) {
        if (hexString == null || hexString.trim().isEmpty()) {
            return 0;
        }

        // 去除空格并统一转为大写
        String hex = hexString.trim().toUpperCase();

        // 检查长度是否合法（1或2个字符）
        if (hex.length() > 2) {
            hex = hex.substring(0, 2); // 截取前两位
        } else if (hex.length() == 1) {
            hex = "0" + hex; // 补前导0，如"F"转为"0F"
        }

        try {
            // 解析16进制为整数（0-255）
            int intValue = Integer.parseInt(hex, 16);
            // 转换为byte（处理符号：超过127的数转为负数）
            return (byte) intValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0; // 转换失败返回0
        }
    }

    public static void main(String[] args) {
        System.out.println(covert16TO10("0003"));
        System.out.println(covert16TO10("5003"));
//        System.out.println(hexString16To2hexString("06"));
//
//
//        System.out.println(makeChecksum("FF FF FF FF 01 00 02 19 01 08 30 50 00 01 00 00 01 00 00 01 03 01 01"));

//        String cmd = "FF FF FF FF 01 00 02 19 A1 08 30 50 00 01 00 00 01 00 00 01 03 01 01".replace(" ", "");
//        System.out.println(calculateChecksum(cmd.getBytes()));
    }


}
