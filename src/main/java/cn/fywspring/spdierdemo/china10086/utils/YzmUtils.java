package cn.fywspring.spdierdemo.china10086.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class YzmUtils {
	// 识别验证码
	public static String getRnum() {

		HttpResponse httpResponse;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("http://v1-http-api.jsdama.com/api.php?mod=php&act=upload");
			httpPost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
			httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			httpPost.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.7");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Host", "v1-http-api.jsdama.com");

			MultipartEntityBuilder mutiEntityBuilder = MultipartEntityBuilder.create();
			File file = new File(System.getProperty("user.dir") + "/img/ValidateNum.jpg");
			final String user_name = PropertiesUtil.getProp("user_name");
			final String user_pw = PropertiesUtil.getProp("user_pw");
			final String yzm_minlen = "4";
			final String yzm_maxlen = "5";
			final String yzmtype_mark = "1013";
			final String zztool_token = PropertiesUtil.getProp("user_name");
			final ContentType ct = ContentType.create("text/html");
			final ContentType img_ct = ContentType.create("image/jpeg");
			final String filename = "ValidateNum.jpg";
			mutiEntityBuilder.addPart("user_name", new StringBody(user_name, ct));
			mutiEntityBuilder.addPart("user_pw", new StringBody(user_pw, ct));
			mutiEntityBuilder.addPart("yzm_minlen", new StringBody(yzm_minlen, ct));
			mutiEntityBuilder.addPart("yzm_maxlen", new StringBody(yzm_maxlen, ct));
			mutiEntityBuilder.addPart("yzmtype_mark", new StringBody(yzmtype_mark, ct));
			mutiEntityBuilder.addPart("zztool_token", new StringBody(zztool_token, ct));
			mutiEntityBuilder.addPart("upload", new FileBody(file, img_ct, filename));

			httpPost.setEntity(mutiEntityBuilder.build());
			httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String content = EntityUtils.toString(httpEntity);
			ObjectMapper om = new ObjectMapper();
			JsonNode tree = om.readTree(content);
			Boolean result = tree.get("result").asBoolean();
			if (result) {// 正确获取验证码
				String data = tree.get("data").toString();
				System.out.println(data);
				return data;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";

	}

	// 验证码错误上传
	public static void sendError(String yzm_id) {
		HttpResponse httpResponse;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("http://v1-http-api.jsdama.com/api.php?mod=php&act=error");
			httpPost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Host", "v1-http-api.jsdama.com");
			httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//			EntityBuilder entityBuilder = EntityBuilder.create();
			String user_name = PropertiesUtil.getProp("user_name");
			String user_pw = PropertiesUtil.getProp("user_pw");
			System.out.println("user_name: "+user_name+", user_pw: "+user_pw+", yzm_id: "+yzm_id);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("user_name", user_name));
			list.add(new BasicNameValuePair("user_pw", user_pw));
			list.add(new BasicNameValuePair("yzm_id", yzm_id));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"utf-8");
			
//			entityBuilder.setParameters(new BasicNameValuePair("user_name", user_name));
//			entityBuilder.setParameters(new BasicNameValuePair("user_pw", user_pw));
//			entityBuilder.setParameters(new BasicNameValuePair("yzm_id", yzm_id));
			httpPost.setEntity(entity);
			httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String content = EntityUtils.toString(httpEntity);
			ObjectMapper om = new ObjectMapper();
			JsonNode tree = om.readTree(content);

			System.out.println(tree.get("result")+"---验证码错误上报异常---"+tree.get("data"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		sendError("11092692321");
	}

}
