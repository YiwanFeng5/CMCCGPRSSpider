package cn.fywspring.spdierdemo.china10086.yzmutils;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
			final String user_name = "yiwanfeng";
			final String user_pw = "FYW19910525ily$";
			final String yzm_minlen = "4";
			final String yzm_maxlen = "5";
			final String yzmtype_mark = "1013";
			final String zztool_token = "yiwanfeng";
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
			HttpPost httpPost = new HttpPost("http://v1-http-api.jsdama.com/api.php?mod=php&act=upload");
			httpPost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
			httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			httpPost.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.7");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Host", "v1-http-api.jsdama.com");

			MultipartEntityBuilder mutiEntityBuilder = MultipartEntityBuilder.create();
			final String user_name = "yiwanfeng";
			final String user_pw = "FYW19910525ily$";
			final ContentType ct = ContentType.create("text/html");
			mutiEntityBuilder.addPart("user_name", new StringBody(user_name, ct));
			mutiEntityBuilder.addPart("user_pw", new StringBody(user_pw, ct));
			mutiEntityBuilder.addPart("yzm_id", new StringBody(yzm_id, ct));

			httpPost.setEntity(mutiEntityBuilder.build());
			httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String content = EntityUtils.toString(httpEntity);
			ObjectMapper om = new ObjectMapper();
			JsonNode tree = om.readTree(content);
			JsonNode num = tree.get("data").get("val");

			System.out.println(num);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
