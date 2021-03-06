package org.cczzrs.core.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class HTTPUtil {
	
    public static void main_(String[] args) throws Exception {
        Map<String, String> dataMap = new HashMap<>();
        Map<String, String> headerMap = new HashMap<>();
        // header: {"Content-Type":"application/json"}
        //
        headerMap.put("Accept","application/json");
        headerMap.put("Content-Type","application/json");
        // data: {"username":"zhangwei","password":"3D186804534370C3C817DB0563F0E461"}
        String sendData = "{\"username\":\"zhangwei\",\"password\":\"3D186804534370C3C817DB0563F0E461\"}";
        dataMap.put("username","zhangwei");
        dataMap.put("password","3D186804534370C3C817DB0563F0E461");
        String url = "http://127.0.0.1:8087/FlowGDSZBackend/user/login";
        System.out.println("---------------");
        System.out.println(post(url, sendData));
        System.out.println("---------------");
    }
	/**
	 * http???get??????
	 * @param url
	 */
	public static String get(String url) {
		return get(url, "UTF-8");
	}

	/**
	 * http???get??????
	 * @param url
	 */
	public static String get(String url, String charset) {
		HttpGet httpGet = new HttpGet(url);
		return executeRequest(httpGet, charset);
	}

	/**
	 * http???get????????????????????????????????????
	 * @param url
	 */
	public static String ajaxGet(String url) {
		return ajaxGet(url, "UTF-8");
	}

	/**
	 * http???get????????????????????????????????????
	 * @param url
	 */
	public static String ajaxGet(String url, String charset) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
		return executeRequest(httpGet, charset);
	}


	/**
	 * http???post???????????????map????????????
	 */
	public static String post(String url, Map<String, String> dataMap) {
		return post(url, dataMap, "UTF-8");
	}


    /**
     * http???post???????????????map????????????
     */
    public static String post(String url, Map<String, String> dataMap, String charset) {
		return execute(null, dataMap, charset, new HttpPost(url));
    }
    /**
     * CCZZRS
     * 2019-12-25 15:27:41
     * http???post???????????????map???????????? - ??????header
     */
    public static String post(String url, Map<String, String> headers, Map<String, String> dataMap, String charset) {
        return execute(headers, dataMap, charset, new HttpPost(url));
    }
    public static String put(String url, Map<String, String> headers, Map<String, String> dataMap, String charset) {
        return execute(headers, dataMap, charset, new HttpPut(url));
    }
    public static String put(String url, Map<String, String> headers, Map<String, String> dataMap) {
        return execute(headers, dataMap, "UTF-8", new HttpPut(url));
    }
	public static String execute(Map<String, String> headers, Map<String, String> dataMap, String charset, HttpEntityEnclosingRequestBase http) {
		try {
            if (dataMap != null){
                List<NameValuePair> nvps = new ArrayList<>();
                for (String key : dataMap.keySet()) {
                    nvps.add(new BasicNameValuePair(key, dataMap.get(key)));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, charset);
                formEntity.setContentEncoding(charset);
                http.setEntity(formEntity);
            }
            // headers
            if(headers != null){
                for (String key:headers.keySet()) {
                    http.setHeader(key, headers.get(key));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return executeRequest(http, charset);
	}


	/**
	 * http???post???????????????map????????????
	 */
	public static String postNew(String url, Map<String, String> dataMap, String charset, String header) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("signature", header);
		httpPost.setHeader("content-type", "application/json");
		try {
			if (dataMap != null){
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : dataMap.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, charset);
				formEntity.setContentEncoding(charset);
				httpPost.setEntity(formEntity);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return executeRequest(httpPost, charset);
	}

	/**
	 * http???post?????????????????????????????????????????????map????????????
	 */
	public static String ajaxPost(String url, Map<String, String> dataMap) {
		return ajaxPost(url, dataMap, "UTF-8");
	}

	/**
	 * http???post?????????????????????????????????????????????map????????????
	 */
	public static String ajaxPost(String url, Map<String, String> dataMap, String charset) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
		try {
			if (dataMap != null){
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : dataMap.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, charset);
				formEntity.setContentEncoding(charset);
				httpPost.setEntity(formEntity);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return executeRequest(httpPost, charset);
	}

	/**
	 * http???post?????????????????????????????????????????????json????????????
	 */
	public static String ajaxPostJson(String url, String jsonString) {
		return ajaxPostJson(url, jsonString, "UTF-8");
	}

	/**
	 * http???post?????????????????????????????????????????????json????????????
	 */
	public static String ajaxPostJson(String url, String jsonString, String charset) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
//		try {
			StringEntity stringEntity = new StringEntity(jsonString, charset);// ????????????????????????
			stringEntity.setContentEncoding(charset);
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return executeRequest(httpPost, charset);
	}

	/**
	 * ????????????http???????????????HttpGet???HttpPost??????
	 */
	public static String executeRequest(HttpUriRequest httpRequest) {
		return executeRequest(httpRequest, "UTF-8");
	}

	/**
	 * ????????????http???????????????HttpGet???HttpPost??????
	 */
	public static String executeRequest(HttpUriRequest httpRequest, String charset) {
		CloseableHttpClient httpclient;
		if ("https".equals(httpRequest.getURI().getScheme())){
			httpclient = createSSLInsecureClient();
		}else{
			httpclient = HttpClients.createDefault();
		}
		String result = "";
		try {
			try {
				CloseableHttpResponse response = httpclient.execute(httpRequest);
				HttpEntity entity = null;
				try {
					entity = response.getEntity();
					result = EntityUtils.toString(entity, charset);
				} finally {
					EntityUtils.consume(entity);
					response.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			} finally {
				httpclient.close();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return result;
	}

	public static JSONObject executeAll(HttpUriRequest httpRequest) {
		return executeAll(httpRequest, "UTF-8");
	}
	public static JSONObject executeAll(HttpUriRequest httpRequest, String charset) {
		CloseableHttpClient httpclient;
		if ("https".equals(httpRequest.getURI().getScheme())){
			httpclient = createSSLInsecureClient();
		}else{
			httpclient = HttpClients.createDefault();
		}
		JSONObject result = new JSONObject();
		try {
			try {
				CloseableHttpResponse response = httpclient.execute(httpRequest);
				HttpEntity entity = null;
				try {
					entity = response.getEntity();
					Arrays.stream(response.getAllHeaders()).forEach(h -> result.put(h.getName(), h.getValue()));
					result.put("body", EntityUtils.toString(entity, charset));
				} finally {
					EntityUtils.consume(entity);
					response.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			} finally {
				httpclient.close();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return result;
	}
	/**
	 * ?????? SSL??????
	 */
	public static CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (GeneralSecurityException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String post(String url, String params) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		String charSet = "UTF-8";
		StringEntity entity = new StringEntity(params, charSet);
		httpPost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
			StatusLine status = response.getStatusLine();
			int state = status.getStatusCode();
			if (state != HttpStatus.SC_OK) {
				log.error("????????????:" + state + "(" + url + ")");
			}
            return EntityUtils.toString(response.getEntity());
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ??????http?????????post??????
	 * @return
	 */
	public static String api4Http(String url,String requestData){
        log.info("api4Http----------------------------");
		//?????????httpclient
		CloseableHttpClient httpclient = HttpClients.createDefault();
		//?????????post??????
		HttpPost httpPost = new HttpPost(url);
		//????????????
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("requestData",requestData));
		log.info("????????????????????????={}");
		//????????????
		CloseableHttpResponse response = null;
		String content ="";
		try {
			//???????????????
			UrlEncodedFormEntity uefEntity  = new UrlEncodedFormEntity(nvps, "UTF-8");
			//????????????post??????
			httpPost.setEntity(uefEntity);
			//??????post??????
			response = httpclient.execute(httpPost);
			if(response != null && response.getStatusLine().getStatusCode()==200){
				content = EntityUtils.toString(response.getEntity(),"utf-8");

			}
			log.info("??????========================="+content);
		} catch (ClientProtocolException e) {
		    log.error("??????????????????");
		} catch (IOException e) {
            log.error("??????????????????");
		}finally{
			if(httpclient != null){
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}


	/**
	 * ws???????????? get??????
	 * @param url
	 * @return
	 */
	public static String api4Get(String url){

		//?????????httpclient
		CloseableHttpClient httpclient = HttpClients.createDefault();
		//?????????get??????
		HttpGet httpget = new HttpGet(url);
		//????????????
		CloseableHttpResponse response = null;
		String content ="";
		try {
			//??????get??????
			response = httpclient.execute(httpget);
			if(response !=null && response.getStatusLine().getStatusCode()==200){
				content = EntityUtils.toString(response.getEntity(),"utf-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(httpclient != null){
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}
}