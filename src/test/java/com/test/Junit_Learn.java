package com.test;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.aspectj.lang.annotation.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


import jdk.nashorn.internal.ir.annotations.Ignore;

public class Junit_Learn{
	
	@Test  
	@Transactional   //标明此方法需使用事务  
	@Rollback(false)  //标明使用完此方法后事务不回滚,true时为回滚  
	public void test1() {  
		System.out.println("11111");


		String obj1 = "junit";
		String obj2 = "junit";
		String obj3 = "test";
		String obj4 = "test";
		String obj5 = null;
		int var1 = 1;
		int var2 = 2;
		int[] arithmetic1 = { 1, 2, 3 };
		int[] arithmetic2 = { 1, 2, 3 };

		assertEquals(obj1, obj2);//断言两个值相等

		assertSame(obj3, obj4);//断言，两个对象引用相同的对象

		assertNotSame(obj2, obj4);//断言，两个对象不是引用同一个对象

		assertNotNull(obj1);//断言一个对象不为空(null)

		assertNull(obj5);//	断言一个对象为空(null)

		assertTrue(obj1, true);//断言一个条件为真

		assertArrayEquals(arithmetic1, arithmetic2);//断言预期数组和结果数组相等
		
    }
	
	@Before
	public void test2(){
		System.out.println("2222");
	}
	
	@BeforeClass
	public static  void test3(){
		System.out.println("33333");
	}
	
	@AfterClass
	public static void test4(){
		System.out.println("44444");
	}
	
	//没执行到这个代码，不知道为什么
	@After(value = "aaa")
	public void test5(){
		System.out.println("55555");
	}
	
	//这个方法不执行
	@Ignore
	@Test
	public void test6(){
		System.out.println("666666");
	}
	
	@Test
	public void testall(){
		System.out.println("======================");
		test1();
		test2();
		test3();
		System.out.println("================");
	}
}
