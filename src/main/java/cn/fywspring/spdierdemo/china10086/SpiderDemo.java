package cn.fywspring.spdierdemo.china10086;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.fywspring.spdierdemo.china10086.imageutils.ImageUtils;
import cn.fywspring.spdierdemo.china10086.yzmutils.YzmUtils;


public class SpiderDemo {
	public static void main(String[] args) throws InterruptedException {
		// 使用本地浏览器
		System.setProperty("webdriver.firefox.bin", "D:\\Program Files\\Mozilla Firefox\\firefox.exe");
		//使用本地浏览器的默认设置
		ProfilesIni profilesIni = new ProfilesIni();
		FirefoxProfile profile = profilesIni.getProfile("default");
		FirefoxOptions option = new FirefoxOptions().setProfile(profile);
		// 初始化一个火狐浏览器
		FirefoxDriver browser = new FirefoxDriver(option);
		// 连接url地址
		String url = "https://bj.ac.10086.cn/login";
		browser.get(url);
		// 新建等待对象
		WebDriverWait wait = new WebDriverWait(browser, 300000);
		// 获取登陆名输入框对象
		WebElement loginName = wait.until(ExpectedConditions.visibilityOf(browser.switchTo().frame("logonboxiframe").findElement(By.xpath(".//*[@id='loginName']"))));
		// 点击登录名输入框
		loginName.click();
		Thread.sleep(3000);
		// 发送登录名
		loginName.sendKeys("18810166614");
		// 获取密码输入框对象
		WebElement password = browser.findElement(By.xpath(".//*[@id='password']"));
		// 点击密码输入框
		password.click();
		Thread.sleep(3000);
		// 发送密码
		password.sendKeys("FYW_19910525");
		// 点击验证码输入框，触发点击事件
		WebElement rnum = browser.findElement(By.xpath(".//*[@id='rnum']"));
		rnum.click();
		Thread.sleep(3000);
		while (true){
			// 获取验证码图片链接
			WebElement numImageEle = browser.findElement(By.xpath(".//*[@id='NumImage']"));
			byte[] image_bytes = browser.getScreenshotAs(OutputType.BYTES);
			ImageUtils.saveNumImage(numImageEle,image_bytes);
			// 将验证码图片发送至打码平台接口
			String data= YzmUtils.getRnum();
			if (!"error".equals(data)) {
				JsonNode tree = null;
				try {
					tree = new ObjectMapper().readTree(data);
				
					// 发送验证码
					rnum.sendKeys(tree.get("val").asText());
					WebElement login = browser.findElement(By.xpath(".//*[@id='loginBut']"));
					login.click();
					Thread.sleep(2000);
					//如果验证码识别不正确，上报错误
					if (url.equals(browser.getCurrentUrl())) {
						String style = browser.findElement(By.xpath(".//*[@id='errorinfornum']")).getAttribute("style");
						if ("".equals(style)) {//如果这个div的隐藏效果（display:none）没有了，则验证码不正确，重新获取
							YzmUtils.sendError(tree.get("id").asText());
							continue;
						} else break;	// 如果验证码正确则退出循环
	
					} else break;
					
				} catch (Exception e) {
					e.printStackTrace();
				} 
				
			}
		}
		searchDetails(browser, wait);
		
		
		
	}

	private static void searchDetails(FirefoxDriver browser, WebDriverWait wait) throws InterruptedException {
		//移动鼠标到“我的移动”
		WebElement mycm_btn_point = wait.until(ExpectedConditions.visibilityOf(browser.findElement(By.xpath(".//*[@id='topA1']"))));
		if (mycm_btn_point != null){
			JavascriptExecutor js = (JavascriptExecutor)browser;
			String js_str = "$('#topA1').mouseover()";
			js.executeScript(js_str);
			Thread.sleep(3000);
		}
		// 查询详单
		WebElement detailEle = browser.findElement(By.xpath(".//*[@id='secMenu1']/p[3]/a"));
		detailEle.click();
		// 客服密码登陆
		WebElement passEle = wait.until(ExpectedConditions.visibilityOf(browser.findElement(By.xpath(".//*[@id='password']"))));
		passEle.click();
		Thread.sleep(2000);
		passEle.sendKeys("421609");
		// 找到登陆按钮，然后点击
		browser.findElement(By.xpath("")).click();
		Thread.sleep(2000);
		// 获得所有可查月份
		List<WebElement> monthEles = wait.until(ExpectedConditions.visibilityOfAllElements(browser.findElements(By.xpath(".//*[@id='yf']/td"))));
		List<WebElement> detailTypeDivs = new ArrayList<WebElement>();// 详单类型行数
		//遍历所有的可查月份.//*[@id='yf']/td[2]
		for (WebElement monthEle : monthEles) {
			// 点击月份
			monthEle.click();
			Thread.sleep(2000);
			// 获取语音通信详单、上网详单和短信/彩信详单类型单选框的标签
			detailTypeDivs.addAll(wait.until(ExpectedConditions.visibilityOfAllElements(browser.findElements(By.xpath(".//*[@id='myTab_Content2']/table/tbody/tr/td[2]/div[1]/ul/li[position()>1 and position()<5]/label")))));
		}
	}
	
	
	
}
