package com.xcg.aitripassistant.utils;

public class Md5Util {
    public static String md5(String str) {
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(str);
    }
    public static void main(String[] args) {
        System.out.println(md5("123456"));
    }
}
