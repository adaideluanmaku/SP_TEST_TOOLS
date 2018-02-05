package com.ch.mytesttools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;

import com.ch.Imagetest.ImageTestDemo;

public class Selenium_image1 {
	@Autowired
//	ImageTestDemo imageTestDemo;
	@Test
	public void testHTML() {
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

			String image1;
			String image2;
			try {
				ImageTestDemo imageTestDemo = new ImageTestDemo();
				image1 = imageTestDemo.getHash(new FileInputStream(fileOutput));
				image2 = imageTestDemo.getHash(new FileInputStream(fileOutput));
				System.out.println("1:1 Score is " + imageTestDemo.distance(image1, image2)+" 。说明：汉明距离越大表明图片差异越大，0<x<5不同但相似，>5明显不同");

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		driver.close();
	}
}
