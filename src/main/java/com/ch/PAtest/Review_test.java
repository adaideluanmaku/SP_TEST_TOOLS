package com.ch.PAtest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author 陈辉
 *	PA审查挂接表测试
 * 将mysql的PA审查挂接结果表和win库结果表对比，查找出不同的数据
 * 这个功能主要测试PA处方点评审查挂接用
 */
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试  
@ContextConfiguration(locations={"classpath:Spring-mvc-servlet.xml"}) //加载配置文件   
public class Review_test{
	
	@Autowired
	JdbcTemplate jdbcTemplate_sqlserver;
	
	@Autowired
	JdbcTemplate jdbcTemplate_mysql;
	
	public void review() throws ClassNotFoundException, SQLException, IOException{
		String sql=null;
		List loglist=new ArrayList();
		List mysql_review=null;
		List sqlserver_review=null;
		List caseidsqllist=null;
		
//		sql="select distinct caseid from mc_review_main where mhiscode='0' and (caseid='Mz1609PA201710090_118_mz_1609PA_门诊_MZZYH_29_4' or caseid='Mz1609PA201710090_119_mz_1609PA_门诊_MZZYH_29_5')"; // 
		sql="select distinct caseid from mc_review_main where mhiscode='0'";
		caseidsqllist=jdbcTemplate_sqlserver.queryForList(sql);
		
		for(int i=0;i<caseidsqllist.size();i++){
			Map caseidmap=(Map)caseidsqllist.get(i);
			
			sql="select distinct a.caseid,a.templateid,a.prescno,b.groupname,b.reviewid,b.reviewname,b.orderid,"
					+ "b.ordermsg,CAST(b.contentmsg AS varchar(8000)) as contentmsg from mc_review_main a inner join "
					+ "mc_review_detail b on a.id=b.mid where a.mhiscode='0' and a.caseid=? ";
			sqlserver_review=jdbcTemplate_sqlserver.queryForList(sql,new Object[]{caseidmap.get("caseid")});
			
			sql="select distinct a.caseid,a.templateid,a.prescno,b.groupname,b.reviewid,b.reviewname,b.orderid,"
					+ "b.ordermsg,b.contentmsg as contentmsg from mc_review_main a inner join mc_review_detail b "
					+ "on a.id=b.mid where a.mhiscode='199006' and a.caseid=? ";
			mysql_review=jdbcTemplate_mysql.queryForList(sql,new Object[]{caseidmap.get("caseid")});
			
			int a=0;
			for(int i1=0;i1<sqlserver_review.size();i1++){
				Map sqlserver_reviewmap=(Map)sqlserver_review.get(i1);
				
				a=0;
				
				for(int i2=0;i2<mysql_review.size();i2++){
					Map mysql_reviewmap=(Map)mysql_review.get(i2);
					
					String sqlserver_orderid="";
					if(sqlserver_reviewmap.get("orderid")!=null){
						sqlserver_orderid=sqlserver_reviewmap.get("orderid").toString().replace("、", ",");
					}
					String sqlserver_ordermsg="";
					if(sqlserver_reviewmap.get("ordermsg")!=null){
						sqlserver_ordermsg=sqlserver_reviewmap.get("ordermsg").toString().replace("、", ",");
					}
					
					
					if(sqlserver_reviewmap.get("templateid").equals(mysql_reviewmap.get("templateid")) &&
							sqlserver_reviewmap.get("prescno").equals(mysql_reviewmap.get("prescno")) && 
							sqlserver_reviewmap.get("contentmsg").equals(mysql_reviewmap.get("contentmsg")) &&
							sqlserver_reviewmap.get("reviewid").equals(mysql_reviewmap.get("reviewid")) && 
							sqlserver_orderid.equals(mysql_reviewmap.get("orderid"))){
						a=a+1;
//						if(sqlserver_reviewmap.get("reviewid").equals(mysql_reviewmap.get("reviewid")) ){
//							a=a+1;
							if(sqlserver_reviewmap.get("groupname").equals(mysql_reviewmap.get("groupname"))){
								a=a+1;
							}
							if(sqlserver_reviewmap.get("reviewname").equals(mysql_reviewmap.get("reviewname"))){
								a=a+1;
							}
							if(sqlserver_reviewmap.get("contentmsg").equals(mysql_reviewmap.get("contentmsg"))){
								a=a+1;
							}
//							if(sqlserver_reviewmap.get("orderid")!=null && mysql_reviewmap.get("orderid")!=null){
//								String sqlserver_orderids[]=sqlserver_reviewmap.get("orderid").toString().split("、");
//								String mysql_orderids[]=mysql_reviewmap.get("orderid").toString().split(",");
//								if(sqlserver_orderids.length==mysql_orderids.length){
//									Arrays.sort(sqlserver_orderids);
//									Arrays.sort(mysql_orderids);
//									int orderidsnum=0;
//									for(int i5=0;i5<sqlserver_orderids.length;i5++){
//										if(sqlserver_orderids[i5].equals(mysql_orderids[i5])){
//											orderidsnum=orderidsnum+1;
//										}
//									}
//									if(orderidsnum==sqlserver_orderids.length){
//										a=a+1;
//									}
//								}
//							}else{
//								if(sqlserver_reviewmap.get("orderid")==null && mysql_reviewmap.get("orderid")==null){
//									a=a+1;
//								}
//							}
							
							if(sqlserver_ordermsg.equals(mysql_reviewmap.get("ordermsg"))){
								a=a+1;
							}
//							if(sqlserver_reviewmap.get("ordermsg")!=null && mysql_reviewmap.get("ordermsg")!=null){
//								String sqlserver_ordermsgs[]=sqlserver_reviewmap.get("ordermsg").toString().split("、");
//								String mysql_ordermsgs[]=mysql_reviewmap.get("ordermsg").toString().split(",");
//								if(sqlserver_ordermsgs.length==mysql_ordermsgs.length){
//									Arrays.sort(sqlserver_ordermsgs);
//									Arrays.sort(mysql_ordermsgs);
//									int ordermsgsnum=0;
//									for(int i5=0;i5<sqlserver_ordermsgs.length;i5++){
//										if(sqlserver_ordermsgs[i5].equals(mysql_ordermsgs[i5])){
//											ordermsgsnum=ordermsgsnum+1;
//										}
//									}
//									if(ordermsgsnum==sqlserver_ordermsgs.length){
//										a=a+1;
//									}
//								}
//							}else{
//								if(sqlserver_reviewmap.get("ordermsg")==null && mysql_reviewmap.get("ordermsg")==null){
//									a=a+1;
//								}
//							}
//						}
					}
					
					if(a>0){
						sqlserver_review.remove(i1);
						mysql_review.remove(i2);
						i1=i1-1;
						i2=i2-1;
						break;
					}
				}
				
				//==5是对的
				if(a<5){
					loglist=listadd(loglist,sqlserver_reviewmap);//
				} 
			}
		}
		
		List listout=new ArrayList();
		String caseidstr=null;
		int aa=0;
		for(int i=0;i<loglist.size();i++){
			Map logmap=(Map)loglist.get(i);
			if(!logmap.get("caseid").equals(caseidstr)){
				aa=aa+1;
				System.out.println("========================="+aa+"=========================");
				listout.add("========================="+aa+"=========================");
				System.out.println("存在问题的病人caseid："+logmap.get("caseid"));
				listout.add("存在问题的病人caseid："+logmap.get("caseid"));
				caseidstr=logmap.get("caseid").toString();
			}
			System.out.println("错误情况：病人姓名="+logmap.get("patientname")+",处方号="+logmap.get("prescno")+",模块id="+logmap.get("templateid")+",模块名称="+logmap.get("modulename"));
			listout.add("错误情况：病人姓名="+logmap.get("patientname")+",处方号="+logmap.get("prescno")+",模块id="+logmap.get("templateid")+",模块名称="+logmap.get("modulename"));
		}
		
		outfile(listout);
		System.out.println("测试结束");
	}
	
	public List listadd(List loglist,Map sqlmap) throws ClassNotFoundException, SQLException, IOException{
		String sql=null;
		
		if(loglist.size()>0){
			for(int i=0;i<loglist.size();i++){
				Map logmap=(Map)loglist.get(i);
				String contentmsg1="";
				if(sqlmap.get("contentmsg")!=null && sqlmap.get("ordermsg")!=null){
					contentmsg1=sqlmap.get("contentmsg").toString().replace(sqlmap.get("ordermsg").toString(), "");
				}
				
				String contentmsg2="";
				if(logmap.get("contentmsg")!=null && logmap.get("ordermsg")!=null){
					contentmsg2=logmap.get("contentmsg").toString().replace(logmap.get("ordermsg").toString(), "");
				}
				
				if(logmap.get("templateid").equals(sqlmap.get("templateid")) &&
						logmap.get("reviewid").equals(sqlmap.get("reviewid")) && 
//						logmap.get("reviewname").equals(sqlmap.get("reviewname")) && 
						contentmsg1.equals(contentmsg2)
//						logmap.get("groupname").equals(sqlmap.get("groupname")) &&
//						logmap.get("prescno").equals(sqlmap.get("prescno"))
						){
					return loglist;
				}
			}
		}
		
//		Map passmysqlmap=(Map)passmysqllist.get(i2);
		if(Integer.parseInt(sqlmap.get("templateid").toString())==2 ||
				Integer.parseInt(sqlmap.get("templateid").toString())==5 ||
				Integer.parseInt(sqlmap.get("templateid").toString())==12 ||
				Integer.parseInt(sqlmap.get("templateid").toString())==13 ||
				Integer.parseInt(sqlmap.get("templateid").toString())==14 ||
				Integer.parseInt(sqlmap.get("templateid").toString())==15){
			return loglist;
		}
		
//		System.out.println("存在问题的病人caseid："+sqlmap.get("caseid"));
//		loglist.add("存在问题的病人caseid："+sqlmap.get("caseid"));
		//获取模板名称
		sql="select name from mc_dict_review_template where templateid=?";
		String modulename=jdbcTemplate_sqlserver.queryForObject(sql, String.class,new Object[]{
				Integer.parseInt(sqlmap.get("templateid").toString())});
		
		String caseidaa=sqlmap.get("caseid").toString();
		caseidaa=caseidaa.split("_")[2];
		String patientname=null;
		if("mz".equals(caseidaa)){
			//获取病人名称
			sql="select patientname from mc_clinic_patient_medinfo where caseid=?";
			patientname=jdbcTemplate_sqlserver.queryForObject(sql, String.class,new Object[]{
					sqlmap.get("caseid").toString()});
			
		}
		if("zy".equals(caseidaa)){
			//获取病人名称
			sql="select patientname from mc_inhosp_patient_medinfo where caseid=?";
			patientname=jdbcTemplate_sqlserver.queryForObject(sql, String.class,new Object[]{
					sqlmap.get("caseid").toString()});
			
		}
		if("cy".equals(caseidaa)){
			//获取病人名称
			sql="select patientname from mc_outhosp_patient_medinfo where caseid=?";
			patientname=jdbcTemplate_sqlserver.queryForObject(sql, String.class,new Object[]{
					sqlmap.get("caseid").toString()});
			
		}
		
		sqlmap.put("patientname", patientname);
		sqlmap.put("modulename", modulename);
//		System.out.println("错误情况：病人姓名="+patientname+",模块id="+sqlmap.get("templateid")+",模块名称="+modulename);
//		loglist.add("错误情况：病人姓名="+patientname+",模块id="+sqlmap.get("templateid")+",模块名称="+modulename);
		loglist.add(sqlmap);
		return loglist;
		
	}
	
	public void outfile(List listout) throws IOException{
		System.out.println("开始写入文件总数"+listout.size());
		String outpath="C:/Users/陈辉/Desktop/测试结果.txt";
		FileOutputStream fos = new FileOutputStream(new File(outpath));
		for(int i=0; i<listout.size();i++){
	        fos.write(listout.get(i).toString().getBytes());
	        fos.write("\r\n".getBytes());
		}
		fos.close();
	}
	
	@Test  
	public void test() throws ClassNotFoundException, SQLException, IOException {  
		System.out.println("开始测试");
		review();
    }  
}
