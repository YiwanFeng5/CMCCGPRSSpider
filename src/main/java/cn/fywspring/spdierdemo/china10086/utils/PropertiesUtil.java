package cn.fywspring.spdierdemo.china10086.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
	public static void main(String[] args) {
		getProp("user_pw");
	}
	static Properties prop = null;
	static {
		prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(System.getProperty("user.dir")+"/src/main/java/settings.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getProp(String key){
		String value = prop.getProperty(key);
//		System.out.println(value);
		return value;
	}
}
