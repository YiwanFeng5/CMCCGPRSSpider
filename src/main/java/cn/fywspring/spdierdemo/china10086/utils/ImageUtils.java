package cn.fywspring.spdierdemo.china10086.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class ImageUtils {
	/**
	 *  保存验证码图片
	 * @param imgEle 验证码元素
	 * @param img_bytes	二进制图片数据
	 */
	public static void saveNumImage(WebElement imgEle, byte[] img_bytes) {
		try {
			// 获得验证码图片的位置和大小
			Point location = imgEle.getLocation();
			Dimension size = imgEle.getSize();
			// 创建全屏截屏
			BufferedImage img_buff = ImageIO.read(new ByteArrayInputStream(img_bytes));
			// 从全屏截图中切除验证码图片,并返回
			BufferedImage numImage =  img_buff.getSubimage(location.getX(), location.getY(), size.width, size.getHeight());
			ImageIO.write(numImage, "jpg", new File(System.getProperty("user.dir")+"/img/ValidateNum.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
