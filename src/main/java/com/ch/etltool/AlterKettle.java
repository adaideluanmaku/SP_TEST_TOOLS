package com.ch.etltool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 脚本批量维护
*java 修改kettle 文件
*/

@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试  
@ContextConfiguration(locations={"classpath:Spring-mvc-servlet.xml"}) //加载配置文件   
public class AlterKettle {
	private static final int RADIX = 16;
	private static final String SEED = "0933910847463829827159347601486730416058";
	public static final String PASSWORD_ENCRYPTED_PREFIX = "Encrypted ";

	//加密
	public static final String encryptPassword(String password) {
		if (password == null)
			return "";
		if (password.length() == 0)
			return "";

		BigInteger bi_passwd = new BigInteger(password.getBytes());

		BigInteger bi_r0 = new BigInteger(SEED);
		BigInteger bi_r1 = bi_r0.xor(bi_passwd);

		return bi_r1.toString(RADIX);
	}

	//解密
	public static final String decryptPassword(String encrypted) {
		if (encrypted == null)
			return "";
		if (encrypted.length() == 0)
			return "";

		BigInteger bi_confuse = new BigInteger(SEED);

		try {
			BigInteger bi_r1 = new BigInteger(encrypted, RADIX);
			BigInteger bi_r0 = bi_r1.xor(bi_confuse);

			return new String(bi_r0.toByteArray());
		} catch (Exception e) {
			return "";
		}
	}
	
	public void kettlexml(String path,String outpath){
		String mserver="172.18.7.160";
		String mtype="MYSQL";
		String maccess="Native";
		String mdatabase="passpa2db_1609_rh_v5";
		String mport="3306";
		String musername="root";
		String mpassword=PASSWORD_ENCRYPTED_PREFIX+encryptPassword("zfxmz");//zfxmz
		
		String oserver="172.18.7.154";
		String otype="ORACLE";
		String oaccess="Native";
		String odatabase="orcl";
		String oport="1521";
		String ousername="passpa";
		String opassword=PASSWORD_ENCRYPTED_PREFIX+encryptPassword("123456");//123456
		
		Crypt_Kettyle password=new Crypt_Kettyle();
		
		SAXReader reader=new SAXReader();
		Document document = null;
		try {
			document = reader.read(new File(path));
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		System.out.println(document);
		
		//读取xml文件
		Element root = document.getRootElement();
//		System.out.println(root.asXML());
		
		int a=0;
		int b=0;
		for ( Iterator i = root.elementIterator("connection"); i.hasNext();) {
			Element connection = (Element) i.next();//�߳�������
//			System.out.println(connection.asXML());
			
			Element name =connection.element("name");
//			System.out.println(name.getText());
			if("mysql".equals(name.getText())){
				a=a+1;
				connection.element("server").setText(mserver);
				connection.element("type").setText(mtype);
				connection.element("access").setText(maccess);
				connection.element("database").setText(mdatabase);
				connection.element("port").setText(mport);
				connection.element("username").setText(musername);
				connection.element("password").setText(mpassword);
			}
			if("oracle".equals(name.getText())){
				b=b+1;
				connection.element("server").setText(oserver);
				connection.element("type").setText(otype);
				connection.element("access").setText(oaccess);
				connection.element("database").setText(odatabase);
				connection.element("port").setText(oport);
				connection.element("username").setText(ousername);
				connection.element("password").setText(opassword);
			}
	    }
		
		//如果缺少数据库连接
		if(a==0){
			root.addElement("connection").addElement("name").setText("mysql");
			for ( Iterator i = root.elementIterator("connection"); i.hasNext();) {
				Element connection = (Element) i.next();//�߳�������
//				System.out.println(connection.asXML());
				
				Element name =connection.element("name");
//				System.out.println(name.getText());
				if("mysql".equals(name.getText())){
					connection.addElement("server").setText(mserver);
					connection.addElement("type").setText(mtype);
					connection.addElement("access").setText(maccess);
					connection.addElement("database").setText(mdatabase);
					connection.addElement("port").setText(mport);
					connection.addElement("username").setText(musername);
					connection.addElement("password").setText(mpassword);
				}
		    }
		}
		if(b==0){
			root.addElement("connection").addElement("name").setText("oracle");
			for ( Iterator i = root.elementIterator("connection"); i.hasNext();) {
				Element connection = (Element) i.next();//�߳�������
//				System.out.println(connection.asXML());
				
				Element name =connection.element("name");
//				System.out.println(name.getText());
				if("oracle".equals(name.getText())){
					connection.addElement("server").setText(oserver);
					connection.addElement("type").setText(otype);
					connection.addElement("access").setText(oaccess);
					connection.addElement("database").setText(odatabase);
					connection.addElement("port").setText(oport);
					connection.addElement("username").setText(ousername);
					connection.addElement("password").setText(opassword);
				}
		    }
		}
		
		OutputFormat format = new OutputFormat();
        format.setEncoding("UTF-8");
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outpath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        XMLWriter output = null;
		try {
			output = new XMLWriter(fos, format);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			output.write(document);
			output.flush();
			output.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}
	
	@Test
	public void testrun(){
		AlterKettle alterKettle=new AlterKettle();
		String path="E:\\chenhui\\工作文档和资料\\PASS-JAVA\\工程包版本备案\\kettle\\kettle_standard\\";
		String outpath="E:\\chenhui\\工作文档和资料\\PASS-JAVA\\工程包版本备案\\kettle\\kettle_standard-copy\\";
		
		//read all kettle files
		File file = new File(path);
		File[] tempList = file.listFiles();
		// System.out.println("files num:" + tempList.length);
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				System.out.println("filepath is:" + tempList[i]);
				
				//alter kettle file
				alterKettle.kettlexml(tempList[i].toString(),outpath+tempList[i].getName());
			}
		}
	}
}
