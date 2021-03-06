# 使用selenium来授权爬取中国移动网上营业厅的个人上网流量详单 #

## 主要使用到技术有： ##

Selenium，Redis，MySQL，Jsoup，HttpClient，Json，C3P0

## 主要的配置是： ##

    JDK1.8
    Selenium3.70
    MySQL5.5.27
    Redis3.2.8
    Jsoup1.7.2
    HttpClient4.5.2
    C3P00.9.1.2
    Json使用的是net.fs.json2.4
    firefox56.0（需要设置PATH系统环境变量）
    geckodriver-v0.19-win64（需要设置PATH系统环境变量，或者将其放置firefox目录下）
    Maven3.3.1
    Eclipse Mars Release (4.5.0)
	firebug2.0
	firepath0.9.7
注意：filefox的版本和geckodriver的版本匹配非常重要，主要参考geckodriver在github上的指导

[http://github.com/mozila/geckodriver/releases](http://github.com/mozila/geckodriver/releases "火狐浏览器geckodriver的gihub地址")

### 1. 首先就是使用Selenium插件来模拟登陆 ###

> 设置好必要的参数settings.properties
	
	#	火狐浏览器的位置
	firefoxpath=
	
	#	中国移动相关参数
	#	url（因为不懂省份地址不一样，以后优化的话可以自动识别）
	url=
	#	登录名（默认就好，不要选随机码登陆）
	loginName=
	#	密码（是中国移动的网上营业厅的登录密码，不是客服密码，客服密码也会用到）
	password=
	#	客服密码（一般是6位纯数字）
	kefupass=
	# 联众打码平台账号和密码
	user_name=
	user_pw=

> 获取浏览器对象

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

注意：本地火狐浏览器浏览器的设置可以在windows中cmd中输入命令：

	firefox -ProfileManager -no-remote

然后双击default，就可以启动浏览器，进行设置。也可以自定义profile，在代码中需要指定，参考上面的代码

> 接下来就是使用上面获得到的浏览器对象模拟登陆操作

	1. 首先是使用浏览器对象去连接登录网址,使用浏览器对象的get()方法
	2. 新建一个等待对象来等待上一步的链接完成（selenium提供了三种等待机制，强制等待--Thread.sleep(毫秒数)；隐式等待：浏览器对象下的manage()方法下的timeouts()方法下的implicitlyWait(等待时间，时间单位TimeUnit的的常数)如果等待超时会抛出异常org.openqa.selenium.NoSuchElementException；显式等待：也就是这里使用到的，新建一个WebDriverWait(浏览器对象, 等待时间单位是秒)对象，然后使用这个对象去调用它下面的util(ExpectedConditions.方法())方法详情请翻阅源码，这种等待超时也会抛出上面的异常，所以需要对每一次页面跳转都需要进行catch然后超时重试操作，本代码还没实现）
	3. 然后通过浏览器对象的findElement等各种方法可以找一个或者多个元素对象，可以By对象可以是XPath也可以是CSS和re（值得注意的是这里有一个坑，那就是iframe中的元素，对于嵌套框架的需要使用浏览器对象进行跳转，然后再能找到该框架中的元素，该方法就是switchTo().frame(框架的id属性，这是一个字符串参数)）
	4. 通过上面的方法找到登陆名输入框和密码输入框，就可以selenium提供的sendKeys()方法进行填表，亦可以模拟的更加细致，模拟点击输入框后，再sendKeys,这里要注意的就是，如果之前登陆过，并记住登录名了，那么这里就要做一个处理了，提供几个思路：1.给登录名输入框发送组合按键Ctrl+a和Delete或者退格键（，发送按键的方法还是sendKeys，Keys类提供按键常数），不管之前登陆名是啥都是删了重新输入；2.获取输入框中的登录名然后做判断，是要输入的登录名就跳过登录名输入，直接输入密码，这样的话就不用输入验证码就可以登陆的，不过还是要要进行验证码元素的查找判断，保险起见，如果真找不到，那就直接可以登陆
	5. 在点击密码输入框后出现验证码输入框，如果之前没有记住登录名的话。然后找到验证码输入框再点击，右边会出现验证码图片，接下来就是验证码的图片处理了，怎么把图片上的字符转换为字符串？我的思路是：截全屏，然后再全屏图中把验证码那一块切出来，再将其不保存在本地，然后封装成PPOST请求再发送至联众打码平台，具体操作请查看联众打码平台，当然验证码的处理还有很多种方法，在这里我就不提供了，大家自行百度
	6. 最后当然时登陆操作啦，有两种方案，要么就是直接像页面发送回车按键，要么就是找到登陆按钮再点击，值得注意的是还是登陆异常重试的处理，还有一个问题就是，如果上一次登陆没有退出或者没有正确退出，会弹出一个div框提示已经处于登陆状态，问是否继续，这就要进行处理，本代码页面有进行处理优化

> 上网流浪详单爬取

	1. 链接到我的详单页面，两种方案，一种是直接通过浏览器对象去get()，还有一种就是模拟用户就是操作用户真实操作：先将鼠标移动到“我的移动”，然后再移动到“我的详单”，然后再点击（值得注意的是，由于页面刚从登陆页面跳转过来，原来的浏览器对象需要更新：浏览器对象.switchTo().defaultContent()，也可以不完全模拟用户操作，可以利用Selenium来执行JQuery来实现对页面指定元素进行鼠标悬停，从而显示被隐藏的div，如果div被隐藏通过findElement是找不到的）
	2. 进入详单页面会要求使用客服密码来登陆，才能进行后续的查询操作（值得注意的是，这里有一个BUG就是如果通过回车来登陆，会直接跳回回首页然后显示未登陆，再点击登录居然没有跳去登录页面，再去详单页面，还是提示客服密码登录。所以这里必须模拟点击登录按钮来登陆）
	3. 接下来就是查询每个月的详单，一开始我以为只能查询他显示地6个月份除了当月，后来发现可以直接进行地址访问16个月，只许改变每次的年份和月份参数即可https://service.bj.10086.cn/poffice/package/xdcx/xdcxShow.action?month="+months[i]+"&yfFlag=5&PACKAGECODE=XD于是我将所有可以查的月份存到了数组里面，当然这里是写死的，可以尝试写活
	4. 然后就是选择上网详单，点击查询，这里会发送一个post请求，可以尝试自己封装POST去访问，将访问后的响应页面交给Jsoup去离线处理，这里可以加入多线程，这样不等jsoup解析完同时去访问下一个月的详单
	5. 解析离线的html页面使用的是Jsoup.parse(String html),定位到元素id为DETAIL的tbody，遍历所有的子元素tr节点，注意这里多一个总计节点，还有每一天记录占了两个td节点，所以记录条数是tr数-1再除以2，拿到每个td进行取值然后封装到GPRS这个bean对象中，在使用net.fs.json提供的JSONObject.fromObject(gprs)将bean对象装换为json对象，在tiString()成字符串缓存到Redis中
	6. 最后将Redis中的所有数据持久化到MySQL数据库中