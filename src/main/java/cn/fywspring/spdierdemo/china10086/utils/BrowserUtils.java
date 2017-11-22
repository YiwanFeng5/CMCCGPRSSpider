package cn.fywspring.spdierdemo.china10086.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.fywspring.spdierdemo.china10086.module.GPRS;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

public class BrowserUtils {
	static FirefoxDriver browser = null;
	
	/**
	 * 登录的主要方法
	 * @throws InterruptedException
	 */
	public  static void toLogin() throws InterruptedException {
		browser = getBrowser();
		// 连接url地址
		String url = PropertiesUtil.getProp("url");
		browser.get(url);
		// 新建等待对象
		WebDriverWait wait = new WebDriverWait(browser, 300000);
		// 获取登陆名输入框对象
		WebElement loginName = wait.until(ExpectedConditions.visibilityOf(browser.switchTo().frame("logonboxiframe").findElement(By.xpath(".//*[@id='loginName']"))));
		// 点击登录名输入框
		if (loginName != null) { 
			loginName.click();
			// 发送一个Ctrl+a，然后和退格键
			loginName.sendKeys(Keys.CONTROL+"a");
			Thread.sleep(1000);
			loginName.sendKeys(Keys.DELETE);
		}
		Thread.sleep(2000);
		// 发送登录名
		loginName.sendKeys(PropertiesUtil.getProp("loginName"));
		// 获取密码输入框对象
		WebElement password = wait.until(ExpectedConditions.visibilityOf(browser.findElement(By.xpath(".//*[@id='password']"))));
		// 点击密码输入框
		password.click();
		Thread.sleep(3000);
		// 发送密码
		password.sendKeys(PropertiesUtil.getProp("password"));
		// 点击验证码输入框，触发点击事件
		WebElement rnum = wait.until(ExpectedConditions.visibilityOf(browser.findElement(By.xpath(".//*[@id='rnum']"))));
		if (rnum != null) {
			rnum.click();
			Thread.sleep(3000);
			String mycm_url = "";
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
						rnum.click();
						rnum.sendKeys(tree.get("val").asText());
						mycm_url = login();
						//如果验证码识别不正确，上报错误
						if (url.equals(mycm_url)) {		// 如果网页对上了，则页面以及跳转了，可以获取新页面的元素了
							String style = browser.findElement(By.xpath(".//*[@id='errorinfornum']")).getAttribute("style");
							if ("".equals(style)) {//如果这个div的隐藏效果（display:none）没有了，则验证码不正确，重新获取
								System.out.println("错误验证码id"+tree.get("id"));
								YzmUtils.sendError(tree.get("id").asText());
								continue;
							} else break;	// 如果验证码正确则退出循环
							
						} else break;
						
					} catch (Exception e) {
						e.printStackTrace();
					} 
					
				}
			}
		} else {
			login();
		}
	}

	/**
	 * 在没有出现验证码的情况下，一定是上次登录过了，并且记录了登录名
	 * @return 返回登录之后的页面url
	 * @throws InterruptedException
	 */
	public  static String login() throws InterruptedException {
		String mycm_url;
		WebElement login = browser.findElement(By.xpath(".//*[@id='loginBut']"));
		login.click();
		Thread.sleep(3000);
		mycm_url = browser.getCurrentUrl();
		return mycm_url;
	}

	/**
	 * 获取一个火狐浏览器对象
	 * @return 返回该浏览器对象供后续模拟浏览器操作
	 */
	public  static FirefoxDriver getBrowser() {
		// 使用本地浏览器
		System.setProperty("webdriver.firefox.bin", PropertiesUtil.getProp("firefoxpath"));
		//使用本地浏览器的默认设置
		ProfilesIni profilesIni = new ProfilesIni();
		FirefoxProfile profile = profilesIni.getProfile("default");
		FirefoxOptions option = new FirefoxOptions().setProfile(profile);
		// 初始化一个火狐浏览器
		FirefoxDriver browser = new FirefoxDriver(option);
		return browser;
	}

	public  static void searchDetails() throws InterruptedException {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		System.out.println("现在的地址是："+browser.getCurrentUrl());
		// 从登录页面的那个Frame中调到当前页面的默认容器
		browser = (FirefoxDriver) browser.switchTo().defaultContent();
		//移动鼠标到“我的移动”
		WebElement mycm_btn_point = browser.findElement(By.xpath(".//*[@id='topA1']"));
		if (mycm_btn_point != null){
			JavascriptExecutor js = (JavascriptExecutor)browser;
			String js_str = "$('#topA1').mouseover()";
			js.executeScript(js_str);
			Thread.sleep(3000);
		}
		// 查询详单
		WebElement detailEle = browser.findElement(By.xpath(".//*[@id='secMenu1']/p[3]/a"));
		detailEle.click();
		Thread.sleep(3000);
		// 客服密码登陆
		browser = (FirefoxDriver) browser.switchTo().defaultContent();
		WebDriverWait wait = new WebDriverWait(browser, 30000);
		Thread.sleep(30000);
		WebElement passEle = wait.until(ExpectedConditions.visibilityOf(browser.findElement(By.xpath("//input[@id='password']"))));
		passEle.click();
		Thread.sleep(2000);
		passEle.sendKeys(PropertiesUtil.getProp("kefupass"));
		// 找到登陆按钮，然后点击
		browser.findElement(By.xpath("//img[@src='style/xdcx/iFrame_images/login_second_button.jpg']")).click();
		Thread.sleep(3000);
		browser.switchTo().defaultContent();
		// 获得所有可查月份
//		List<WebElement> monthEles = wait.until(ExpectedConditions.visibilityOfAllElements(browser.findElements(By.xpath(".//*[@id='yf']/td"))));
		String[] months = {"201710","201709","201708","201707","201706","201705","201704","201703","201702","201701","201612","201611","201610","201609","201608","201607"};
		//遍历所有的可查月份.//*[@id='yf']/td[2]
		for (int i = 0; i < months.length; i++) {
			// 点击月份
//			browser.findElement(By.xpath(".//*[@id='yf']/td["+i+"]")).click();
			browser.get("https://service.bj.10086.cn/poffice/package/xdcx/xdcxShow.action?month="+months[i]+"&yfFlag=5&PACKAGECODE=XD");
			Thread.sleep(2000);
			// 获取语音通信详单、上网详单和短信/彩信详单类型单选框的标签
			browser.switchTo().defaultContent();
			WebElement detailType = wait.until(ExpectedConditions.visibilityOf(browser.findElement(By.xpath(".//*[@id='myTab_Content2']/table/tbody/tr/td[2]/div[1]/ul/li[3]/label"))));
			detailType.click();
			Thread.sleep(3000);
			browser = (FirefoxDriver) browser.switchTo().defaultContent();
			// 获取提交按钮,并点击
			browser.findElement(By.xpath("//img[@src='style/xdcx/images/chaxun.jpg']")).click();
			Thread.sleep(30000);
			
			// 多线程去遍历所有tr下的所有td
			final String html = browser.getPageSource();
			// 用jsoup离线去解析
			threadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					parseHtml(html);
				}
			});
		}
			
//		List<WebElement> details_trs = wait.until(ExpectedConditions.visibilityOfAllElements(browser.findElements(By.xpath(".//*[@id='DETAIL']/tr"))));
//		for (WebElement details_tr : details_trs) {
//			String time = wait.until(ExpectedConditions.visibilityOf(details_tr.findElement(By.xpath("td[1]")))).getText();
//			String addr = details_tr.findElement(By.xpath("td[2]")).getText();
//			String way = details_tr.findElement(By.xpath("td[3]")).getText();
//			String time_long = details_tr.findElement(By.xpath("td[4]")).getText();
//			String gprs = details_tr.findElement(By.xpath("td[5]")).getText();
//			String tcyh = details_tr.findElement(By.xpath("td[6]")).getText();
//			String fee = details_tr.findElement(By.xpath("td[7]")).getText();
//			System.out.println(time+"---"+addr+"---"+way+"---"+way+"---"+time_long+"---"+gprs+"---"+tcyh+"---"+fee);
//			SaveDB.insertIntoDB(time, addr, way, time_long, gprs, tcyh, fee);
//		}
		System.out.println("本次一共爬取出"+resultsum+"条记录！");
		browser.switchTo().defaultContent();
		String oldUrl = browser.getCurrentUrl();
		while (true) {
			browser.switchTo().defaultContent();
			WebElement logout = wait.until(ExpectedConditions.visibilityOf(browser.findElement(By.xpath(".//*[@id='logout']"))));
			if (logout == null) {
				break;
			} else {
				logout.click();
			}
			Thread.sleep(60000);
			browser.switchTo().defaultContent();
			if (oldUrl.equals(browser.getCurrentUrl())) {
				browser.navigate().refresh();
				continue;
			} else {
				break;
			}
		}
		browser.quit();
	}
	
	static long resultsum = 0L;
	public static void parseHtml(String html) {
		Document doc = Jsoup.parse(html);
		Jedis jedis = JedisUtils.getJedis();
		synchronized(doc){
			Element detail = doc.getElementById("DETAIL");	//详情tbody
			int trsNum = (detail.childNodeSize()-1)/2;
			resultsum += trsNum;
			for (int i = 0; i < trsNum-1; i++) {
				String gprs_time = detail.child(i).child(0).text();
				String gprs_addr = detail.child(i).child(1).text();
				String gprs_way = detail.child(i).child(2).text();
				String gprs_long = detail.child(i).child(3).text();
				String gprs_sum = detail.child(i).child(4).text();
				String gprs_tcyh = detail.child(i).child(5).text();
				String gprs_fee = detail.child(i).child(6).text();
				
				GPRS gprs = new GPRS(gprs_time,gprs_addr,gprs_way,gprs_long,gprs_sum,gprs_tcyh,gprs_fee);
				String json = JSONObject.fromObject(gprs).toString();
				System.out.println("[ "+(i+1)+" / "+trsNum+" ]---"+Thread.currentThread().getName()+"---"+json);
				jedis.lpush("cmcc_gprs",json);
			}
		}
		
		
		JedisUtils.returnResource(jedis);
		
		
	}
}
