package com.zyyhbd.api.util;

import org.apache.commons.codec.digest.DigestUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class HttpRequestUtil {
	public static final String CODE = "code";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String AUTHORIZATION_CODE = "authorization_code";
	public static final String APP_SECRET_KEY = "56eea6c8e76fc4262a4a2816dfd79c7fdb4781a9433da5509d3ee649125447d8"; // 密钥，向IAM申请


	static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}


		@Override
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}


		@Override
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}



	/**
	 * 根据请求的URL是https还是http请求数据
	 *
	 * @param sendUrl
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String getResult(String sendUrl, String param)
			throws Exception {
		if (sendUrl.startsWith("https")) {
			return getResultByHttps(sendUrl, param);
		}
		return getResultByHttp(sendUrl, param);
	}

	private static String getResultByHttps(String sendUrl, String param)
			throws NoSuchAlgorithmException, KeyManagementException,
			IOException {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		// javax.net.ssl.SSLContext sc =
		// javax.net.ssl.SSLContext.getInstance("SSL");
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSLv3");

		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());


		HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);

		OutputStream out = null;
		BufferedReader reader = null;
		String result = "";
		URL url = null;
		HttpsURLConnection conn = null;
		try {
			url = new URL(sendUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");
			// 必须设置false，否则会自动redirect到重定向后的地址
			conn.setInstanceFollowRedirects(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.connect();
			out = conn.getOutputStream();
			out.write(param.getBytes());
			InputStream input = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line = "";
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			if (out != null) {
				out.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}

	private static String getResultByHttp(String sendUrl, String param)
			throws NoSuchAlgorithmException, KeyManagementException,
			IOException {

		HttpURLConnection conn = null;
		OutputStream out = null;
		BufferedReader reader = null;
		String result = "";
		try {

			URL url = new URL(sendUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(150000);
			conn.connect();
			out = conn.getOutputStream();
			out.write(param.getBytes());
			out.flush();
			out.close();
			InputStream input = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			out.close();
			conn.disconnect();
		}

		return result;
	}

	public static String getSign(Map<String, String> params,String secret)
	{
		String sign="";
		StringBuilder sb = new StringBuilder();
		//排序
		Set<String> keyset=params.keySet();
		TreeSet<String> sortSet=new TreeSet<String>();
		sortSet.addAll(keyset);
		Iterator<String> it=sortSet.iterator();
		//加密字符串
		while(it.hasNext())
		{
			String key=it.next();
			String value=params.get(key);
			sb.append(key).append(value);
		}
		sb.append("appkey").append(secret);
		try {

			sign=DigestUtils.md5Hex(sb.toString()).toUpperCase();
//           sign=  MD5Util.md5s(sb.toString()).toUpperCase();
		} catch (Exception e) {
		}
		return sign;
	}

	/**
	 * 组装获取用户api参数，含签名
	 * @param client_ID
	 * @param client_secret
	 * @param token
	 * @return
	 */
	public static String getUserParam(String client_ID, String client_secret,String token) {

		String nonce_str = radomString();
		String appkey=HttpRequestUtil.APP_SECRET_KEY;
		long timestamp= System.currentTimeMillis();

		Map<String, String> params = new HashMap<String, String>();

		params.put("client_id", client_ID);
		params.put("client_secret", client_secret);
		params.put("nonce_str", nonce_str);
		params.put("oauth_timestamp", String.valueOf(timestamp));

		if(token.contains("access_token=")){
			int strStartIndex = token.indexOf("access_token=");
			int strEndIndex = token.indexOf("&expires");
			String access_token = token.substring(strStartIndex, strEndIndex).substring("access_token=".length());
			params.put("access_token", access_token);

		}else{
			params.put("access_token", token);
		}
		String sign = getSign(params, appkey+client_secret);
		StringBuffer tokenParam = new StringBuffer();
		for (String key : params.keySet()) {
			if(tokenParam.length()==0){
				tokenParam.append(key).append("=").append(params.get(key));
			}else{
				tokenParam.append("&").append(key).append("=").append(params.get(key));
			}

		}
		tokenParam.append("&sign=").append(sign);
		return tokenParam.toString();
	}

	/**
	 * 组装检查心跳API参数含签名
	 * @param client_ID
	 * @param client_secret
	 * @return
	 */
	public static String getIAMServiceParam(String client_ID, String client_secret) {
		String nonce_str = radomString();
		String appkey=HttpRequestUtil.APP_SECRET_KEY;
		long timestamp= System.currentTimeMillis();

		Map<String, String> params = new HashMap<String, String>();

		params.put("client_id", client_ID);
		params.put("client_secret", client_secret);
		params.put("nonce_str", nonce_str);
		params.put("oauth_timestamp", String.valueOf(timestamp));
		String sign = getSign(params, appkey+client_secret);
		StringBuffer tokenParam = new StringBuffer();
		for (String key : params.keySet()) {
			if(tokenParam.length()==0){
				tokenParam.append(key).append("=").append(params.get(key));
			}else{
				tokenParam.append("&").append(key).append("=").append(params.get(key));
			}

		}
		tokenParam.append("&sign=").append(sign);
		return tokenParam.toString();
	}

	private static String radomString(){
		String result="";
		for(int i=0;i<10;i++){
			int intVal=(int)(Math.random()*26+97);
			result=result+(char)intVal;
		}
		return result;
	}

	/**
	 * 组装获取token api参数 含签名
	 * @param client_ID
	 * @param client_secret
	 * @param redirect_uri
	 * @param code
	 * @return
	 */
	public static String getAccessTokenParam(String client_ID, String client_secret, String redirect_uri, String code) {
		String nonce_str = radomString();
		String appkey=HttpRequestUtil.APP_SECRET_KEY;
		long timestamp= System.currentTimeMillis();

		Map<String, String> params = new HashMap<String, String>();

		params.put("client_id", client_ID);
		params.put("client_secret", client_secret);
		params.put("nonce_str", nonce_str);
		params.put("oauth_timestamp", String.valueOf(timestamp));

		params.put("code", code);
		params.put("redirect_uri",redirect_uri);
		params.put("grant_type", "authorization_code");
		String sign = getSign(params, appkey+client_secret);
		StringBuffer tokenParam = new StringBuffer();
		for (String key : params.keySet()) {
			if(tokenParam.length()==0){
				tokenParam.append(key).append("=").append(params.get(key));
			}else{
				tokenParam.append("&").append(key).append("=").append(params.get(key));
			}

		}
		tokenParam.append("&sign=").append(sign);
		return tokenParam.toString();
	}



}
