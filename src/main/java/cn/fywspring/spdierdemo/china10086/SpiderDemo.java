package cn.fywspring.spdierdemo.china10086;



import cn.fywspring.spdierdemo.china10086.utils.BrowserUtils;


public class SpiderDemo {
	
	public static void main(String[] args) throws InterruptedException {
		BrowserUtils.toLogin();
		BrowserUtils.searchDetails();
	}

}
