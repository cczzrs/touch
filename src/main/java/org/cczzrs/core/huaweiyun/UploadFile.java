package org.cczzrs.core.huaweiyun;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSONObject;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.cczzrs.core.http.HTTPUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * @fileName UploadFile.java
 * @author CCZZRS
 * @date 2021-09-17 14:17:25
 * @description 华为云 OBS API 对接
 */ 
public class UploadFile {
    // 桶
    public static final String OBS_FZPX = "obs-fzpx";
    //服务地址
    public static final String OBS_ADDR = "obs-fzpx.obs.cn-north-4.myhuaweicloud.com";
    //秘钥
    public static final String ak="NSMOZTV7UBWAM9CIHOS2";
    public static final String sk="0TlM4jMIFqaIMdgARlOQKfHZSvQGnOlAzgNazNan";
    
    /**
     * @Description: 上传文件
     * @param: mFile：文件     name:文件夹名称
     * @return:
     * @authoer: wangyuan
     *date:2021/3/12 11:37
     **/
    public static JSONObject multipartFile(MultipartFile mFile, String path){
        JSONObject data = new JSONObject();
        try {
            //获取文件名称
            JSONObject ret = inputStream(path, mFile.getOriginalFilename(), mFile.getInputStream());
            //响应结果
            if(ret.getInteger("code")!=200){
                data.put("msg","上传失败");
                return data;
            }
            // private String bucketName;
            // private String objectKey;
            // private String objectUrl;
            data.putAll(ret.getJSONObject("buf"));
        } catch (IOException e) {
            e.printStackTrace();
            data.put("msg","上传错误");
            return data;
        }
        return data;
    }
    public static JSONObject file(File file, String path, String name){
        try {
            return inputStream(path, name, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JSONObject retJO = new JSONObject();
            retJO.put("code", 500);
            retJO.put("buf","上传错误");
            return retJO;
        }
    }
    
    /**
            PUT /ObjectName HTTP/1.1 
            Host: bucketname.obs.cn-north-4.myhuaweicloud.com 
            Content-Type: application/xml 
            Content-Length: length
            Authorization: authorization
            Date: date
            <Optional Additional Header> 
            <object Content>
    */
    /**
     * @fileName UploadFile.java
     * @author CCZZRS
     * @date 2021-09-17 14:17:59
     * @description 上传文件 
     * */
    public static JSONObject putObject(String obsFzpx, String data_key, InputStream in) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        data_key = URLEncoder.encode(data_key, "UTF-8");
        String url = "http://"+OBS_ADDR+"/"+data_key;
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Host", OBS_ADDR);
        // httpPut.setHeader("Content-Type", contentType);
        // httpPut.setHeader("Content-Type", "application/json");
        // httpPut.setHeader("Content-Length", in.available()+"");
        String fdDate = fdDate();
        httpPut.setHeader("Date", fdDate);
        String signature = enCode("PUT\n\n\n"+fdDate+"\n/"+OBS_FZPX+"/"+data_key);
        httpPut.setHeader("Authorization", "OBS "+ak+":"+signature);
        httpPut.setEntity(new InputStreamEntity(in));
        JSONObject response = HTTPUtil.executeAll(httpPut); 
        response.put("objectUrl", url);
        return response;
    }

    public static String enCode(String signature)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(sk.getBytes("UTF-8"), mac.getAlgorithm()));
        return Base64.getEncoder().encodeToString(mac.doFinal(signature.getBytes("UTF-8")));
    }
    public static SimpleDateFormat df_EEE = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    public static String fdDate(){
        return fdDate(System.currentTimeMillis());
    }
    public static String fdDate(Object date){
        if(date == null){
            return fdDate();
        }
        df_EEE.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df_EEE.format(date);
    }

    static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * @author CCZZRS
     * @date 2021-3-13 14:13:26
     * @description 上传到 OBS ，统一调用
     */
    public static JSONObject inputStream(String path, String name, InputStream in){
        JSONObject retJO = new JSONObject();
        try {
            //上传字节内容
            // ByteArrayInputStream input = new ByteArrayInputStream(buf);
            String data_key = path + "/" + df.format(new Date()) + "/" + UUID.randomUUID().toString().replace("-", "") + name;
            //响应结果
            retJO.put("code", 200);
            retJO.put("buf",putObject(OBS_FZPX, data_key, in));
        } catch (Exception e) {
            e.printStackTrace();
            retJO.put("code", 500);
            retJO.put("buf","上传错误");
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retJO;
    }
    public static JSONObject buf(byte[] buf, String path, String name){
        return inputStream(path, name, new ByteArrayInputStream(buf));
    }

// public static void main(String[] args) throws IOException {
//     FileInputStream in = new FileInputStream("ccc.png");
//     JSONObject inputStream = inputStream("test","法制培训合格证.jpg",in);
//     in.close();
//     System.out.println(inputStream.toJSONString());
// }

}
