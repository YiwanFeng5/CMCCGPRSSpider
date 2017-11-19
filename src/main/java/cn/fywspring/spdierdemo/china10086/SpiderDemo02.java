package cn.fywspring.spdierdemo.china10086;



import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SpiderDemo02 {
	public static void main(String[] args) throws InterruptedException {

		// 使用本地浏览器
		System.setProperty("webdriver.firefox.bin", "D:\\Program Files\\Mozilla Firefox\\firefox.exe");
		//使用本地浏览器的默认设置
		ProfilesIni profilesIni = new ProfilesIni();
		FirefoxProfile profile = profilesIni.getProfile("default");
		FirefoxOptions option = new FirefoxOptions().setProfile(profile);
		// 初始化一个火狐浏览器
		FirefoxDriver driver = new FirefoxDriver(option);
		WebDriverWait wait = new WebDriverWait(driver, 30000);
		driver.get("http://10086.cn/bj/index_100_100.html");
		WebElement ele = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(".//*[@id='topA1']"))));
		if (ele != null){
			JavascriptExecutor js = (JavascriptExecutor)driver;
			String js_str = "$('#topA1').mouseover()";
			js.executeScript(js_str);
			Thread.sleep(3000);
		}
		
	}
}
