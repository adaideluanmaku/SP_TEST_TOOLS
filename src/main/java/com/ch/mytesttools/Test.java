package com.ch.mytesttools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
	public static void main(String args[]) throws IOException {
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
		
//		// 读取HTML-B源码
//		File file1 = new File("C:\\b.txt");
//		
//		byte[] filecontent1 = null;
//		try {
//			FileInputStream in = new FileInputStream(file1);
//			filecontent1 = new byte[in.available()];
//			in.read(filecontent1);
//			in.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		String conntent1 = new String(filecontent1, "gbk");
		
		
		Document doc = Jsoup.parse(conntent);
		Elements allele=doc.body().children().select("*");
		for(Element el:allele){
			System.out.println(el.ownText());
		}
		
	}
}
