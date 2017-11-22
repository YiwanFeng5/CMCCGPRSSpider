package cn.fywspring.spdierdemo.china10086;



import cn.fywspring.spdierdemo.china10086.dao.SaveDB;
import cn.fywspring.spdierdemo.china10086.utils.BrowserUtils;


public class SpiderDemo {
	
	public static void main(String[] args) throws InterruptedException {
		BrowserUtils.toLogin();
		System.out.println("--------------------登录完成，开始遍历每个月的上网流量数据--------------");
		BrowserUtils.searchDetails();
		System.out.println("--------------------开始插入MySQL数据库----------------------------");
		SaveDB.saveFromRedis2DB();
	}

}
