package org.cczzrs.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

public class MyUtil {
    
    /**
     * SHA加密公共方法
     * @param string 目标字符串
     * @param type   加密类型 
                    SHA224("sha-224"),
                    SHA256("sha-256"),
                    SHA384("sha-384"),
                    SHA512("sha-512"),
     */
    public static String encryptSHA(String str) {return encryptSHA(str, null);}
    public static String encryptSHA(String str,String type) {
        if (str==null || "".equals(str.trim())) return "";
        if (type==null) type = "sha-256";
        try {
            MessageDigest md5 = MessageDigest.getInstance(type);
            byte[] bytes = md5.digest((str).getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5加密-32位小写
     */
    public static String encryptMD5(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = ((int) md5Byte) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }

    /**
     * 获取客户端的ip地址
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        try {
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteHost();
            }
            return ip;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String returnPhone(String phone){
        if(null == phone || "".equals(phone)){
            return phone;
        }
        return phone.substring(phone.length()-4);
    }
}
