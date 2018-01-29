package com.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试  
@ContextConfiguration(locations={"classpath*:/Spring-mvc-servlet.xml"}) //加载配置文件   
public class Spring_test2{
	@Autowired
//	private MyService myService;
	
	@Test  
//	@Transactional   //标明此方法需使用事务  
//	@Rollback(false)  //标明使用完此方法后事务不回滚,true时为回滚  
	public void test1() {  
		System.out.println("bbbbbbb");
		System.out.println(-new Random().nextInt(10));
//		System.out.println(differentDaysByMillisecond_1("","2017-07-07 00:00:00"));
    }
	
	/**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
	public int differentDaysByMillisecond(Date date1, Date date2) {
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		return days;
	}

	public int differentDaysByMillisecond_1(String date1, String date2) {
		SimpleDateFormat sdf=null;
		Date time1=null;
		Date time2=null;
		
		sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			time1 = sdf.parse(date1);
			time2 = sdf.parse(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int days = (int) ((time1.getTime() - time2.getTime()) / (1000 * 3600 * 24));
		return days;
	}
}
