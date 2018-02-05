package com.ch.mytesttools;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Selenium_image {
	@Test
	public void testImage() {
		WebDriver driver = new FirefoxDriver();
		
		// 全局设置延迟，如果操作无响应，则等待最多10S
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// 浏览器输入地址
		driver.get("http://www.w3school.com.cn/");

		// 浏览器最大化
		driver.manage().window().maximize();
		
		//
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			Thread.sleep(3000);
			FileUtils.copyFile(screenshot, new File("E:\\test.jpg"));//selenium截取的页面图片
			File fileInput = new File("E:\\ch.jpg");//断言，预期图片
			File fileOutput = new File("E:\\test.jpg");//selenium截取的页面图片

			BufferedImage bufileInput = ImageIO.read(fileInput);
			DataBuffer dafileInput = bufileInput.getData().getDataBuffer();
			int sizefileInput = dafileInput.getSize();//获取断言图片的属性总数
			BufferedImage bufileOutput = ImageIO.read(fileOutput);
			DataBuffer dafileOutput = bufileOutput.getData().getDataBuffer();
			int sizefileOutput = dafileOutput.getSize();//selenium截取的页面图片的属性总数

			//特别说明：selenium截取的图片是整个页面，所以页面属性总数应该是最多的。
			//而断言所截取的图片，应该是整个页面的一部分作为测试重点，页面属性总数不可能大于整个页面。
			//所以，我们做测试的时候，断言图片只要截取页面中的主要部分就可以了，无需把没用的东西也截取进来
			boolean matchFlag = true;//默认图片相同，这个标记可以返回给其他地方使用
			int sizeinit=0;
			if (sizefileInput <= sizefileOutput) {
				for (int j = 0; j < sizefileInput; j++) {
					if (dafileInput.getElem(j) != dafileOutput.getElem(j)) {
						matchFlag = false;
						System.out.println("图片属性不同，两张图片存在差异");
						System.out.println(dafileInput.getElem(j-1)+":"+dafileOutput.getElem(j-1));
						System.out.println(dafileInput.getElem(j)+":"+dafileOutput.getElem(j));
						break;
					}
				}
				if(matchFlag){
					System.out.println("图片相同");
				}
				
			} else {
				matchFlag = false;
				System.out.println("断言属性总数大于预期属性总数，两张图片存在差异");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
//		driver.close();
	}
}
