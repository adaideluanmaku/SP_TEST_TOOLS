package com.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试  
@ContextConfiguration(locations={"classpath*:/Spring-mvc-servlet.xml"}) //加载配置文件   
public class MoreThread{
	@Autowired
//	private MyService myService;
	
	@Test  
	public void test1() {  
		System.out.println("xxxxxxxxxxxx");
		int threadNum=1;
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNum, 5, 5, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(threadNum));

		for (int i = 0; i < 1; i++) {
//			executor.execute(onethread());
			System.out.println("hzsz"+1+"  "+i);
//			executor.execute(new onethread11());
		}
		executor.shutdown();
		try {
			while (!executor.awaitTermination(5, TimeUnit.SECONDS));
			
			System.out.println("11111111111");
		} catch (InterruptedException e) {
			// 执行柱塞是否报错
//			logger.error("导入线程池报错（InterruptedException）：" + e.getMessage());
			System.out.println("导入线程池报错（InterruptedException）：" + e.getMessage());
		}
    }

	class onethread11 implements Runnable{

		public void run() {
			// TODO Auto-generated method stub
			int b = 0;
			while(b<10){
				System.out.println(b);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b = b+1;
			}
		}
		
	}
}
