package com.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试  
@ContextConfiguration(locations={"classpath*:/Spring-mvc-servlet.xml"}) //加载配置文件   
public class Spring_test{
	@Autowired
//	private MyService myService;
	
	@Test  
//	@Transactional   //标明此方法需使用事务  
//	@Rollback(false)  //标明使用完此方法后事务不回滚,true时为回滚  
	public void test3() {  
		System.out.println("bbbbbbb");
//		myService.aaa();
    }
}
