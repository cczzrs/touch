package org.cczzrs.core.utils;

import org.apache.logging.log4j.util.PropertiesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @fileName PropertyUtil.java
 * @author CCZZRS
 * @date 2019-9-18 15:59:40
 * @description Property文件类型操作
 * */
public class PropertyUtil {

    /**
     * 读取配置文件
     * @param fileName
     */
    public static Properties readProperties(String fileName){
        Properties prop = null;
        try {
            InputStream in = PropertiesUtil.class.getResourceAsStream("/"+fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            prop = new Properties();
            prop.load(bf);
            in.close();
            bf.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return prop;
    }

    /**
     * 根据key读取对应的value
     * @param key
     * @return
     */
    public static String getProperty(String fileName, String key){
        return readProperties(fileName).getProperty(key);
    }
}