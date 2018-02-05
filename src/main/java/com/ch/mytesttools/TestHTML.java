package com.ch.mytesttools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

//读取文档内容，文件内容
public class TestHTML {
	public static void main(String args[]) throws IOException {
		WebDriver driver = new FirefoxDriver();

		// 全局设置延迟，如果操作无响应，则等待最多10S
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// 浏览器输入地址
		driver.get("http://www.w3school.com.cn/");

		// 浏览器最大化
		driver.manage().window().maximize();

		// 读取HTML-A源码
		File file = new File("C:\\a.txt");
		
		byte[] filecontent = null;
		try {
			FileInputStream in = new FileInputStream(file);
			filecontent = new byte[in.available()];
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String conntent = new String(filecontent, "gbk");
		// System.out.println(conntent);
		Document doc = Jsoup.parse(conntent);
		String[] docstr = doc.toString().split("\n");
		
		// 读取HTML-B源码
		Document doc1 = Jsoup.connect(driver.getCurrentUrl()).get();
		String[] docstr1 = doc1.toString().split("\n");
		
		boolean status = true;
		if(docstr.length==docstr1.length){
			for(int i=0;i<docstr.length;i++){
				String str = docstr[i].trim();
				String str1 = docstr1[i].trim();
				
				if(!str.equals(str1)){
					System.out.println("第"+(i+1)+"行出现问题：");
					System.out.println(str);
					System.out.println(str1);
					status = false;
					break;
				} 
			}
		}

		if(status){
			System.out.println("源码相同");
		}else{
			System.out.println("源码不同");
		}
		
		driver.close();
	}
}
