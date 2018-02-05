package com.ch.mytesttools;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//Jsoup解析HTML，读取HTML
public class JsoupHTML {
	final static String url=  "http://www.w3school.com.cn/";
	
	public static void main(String args[]) throws IOException{
		//1.直接读页面
		Document doc = Jsoup.connect(url).get();
		System.out.println(doc);
		
		//2.直接读字符串
		String aa= "<html><body><a>12312321</a></body></html>";
		Document doc1 = Jsoup.parse(aa);
		System.out.println(doc1);

	}
}
