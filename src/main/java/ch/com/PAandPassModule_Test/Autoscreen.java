package ch.com.PAandPassModule_Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
/**
 * 1.调用PASS审查，结果数据PASS写入redis
 * 2.通过统计分析临时表的数据制作分表数据
 * @author 陈辉
 *
 */

@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试  
@ContextConfiguration(locations={"classpath:Spring-mvc-servlet.xml"}) //加载配置文件   
public class Autoscreen {
	
	@Autowired
	public static Jedis jedis;
	
	@Autowired
	private JdbcTemplate jdbcTemplate_anli;
	
	@Autowired
	JdbcTemplate jdbcTemplate_sqlserver;
	
	@Autowired
	JdbcTemplate jdbcTemplate_mysql;
	
	public static final String PA_SCREENRESULTS = "PA_SCREENRESULT_LIST";
	
	public void PASS(){
		
		int count=1 ;//循环总数
		
		//缺少超多日用量 "超多日用量025"
		String[] anliname={"不良反应015","体外配伍068","儿童用药027","剂量范围014","哺乳用药046","围手术期020",
				"妊娠用药012","性别用药031","成人用药018","相互作用022","细菌耐药率015","给药途径036","老人用药021",
				"肝损害剂量027","肾损害剂量066","药物禁忌症032","药物过敏028","超适应症023",
				"越权用药019","配伍浓度020","重复用药031","钾离子浓度069","超多日用量025"};
//		String[] anliname={"不良反应015"};
		
		
		List anlilist=new ArrayList();
		
		String sql=null;
		for(int i=0;i<anliname.length;i++){
			sql="select gatherbaseinfo from sa_gather_log where anliname=? and version='1609'";
			String gatherbaseinfo=jdbcTemplate_anli.queryForObject(sql, String.class,new Object[]{anliname[i]});
			anlilist.add(gatherbaseinfo);
		}
		
		System.out.println(anlilist.size());
		
		//请求passcore
		final Passservice passservice=new Passservice();
		
		for(int i=0;i<count;i++){
			for(int j=0;j<anlilist.size();j++){
				final JSONObject json=JSONObject.fromObject(anlilist.get(j));
				final JSONObject Patient=json.getJSONObject("Patient");
				//修改案例唯一码
				Patient.put("PatCode", Patient.getString("PatCode"));
				Patient.put("InHospNo", Patient.getString("InHospNo"));
				Patient.put("Name", Patient.getString("Name")+"_"+i+"_"+j);
				json.put("Patient", Patient);
//				System.out.println(Patient.get("Name"));
				Thread t=new Thread(new Runnable(){
	    			public void run(){
	    				try {
							String a=passservice.getPassResult(json.toString(), "http://172.18.7.160:8081/pass/ws/PASSwebService.asmx/Mc_DoScreen");
//	    					String a = passservice.getPassResult(json.toString(), "http://172.18.2.179:9099/pass/ws/PASSwebService.asmx/Mc_DoScreen");
							System.out.println("-->"+a);
	    				} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("请求发生异常");
						}
	    			}
	    		});
				t.start();
				
				try {
					if((i*anlilist.size()+(j+1))%(anlilist.size()*2)==0){
						System.out.println("循环次数："+anliname.length*count+"-->"+(i*anlilist.size()+(j+1)));
						t.join();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void PA() throws ClassNotFoundException, SQLException, IOException{
		int count=1;//一批案例的循环次数
		
		//mc_review_detail 108条数据， mc_review_main45条数据
		
		//缺少超多日用量 "超多日用量025"
//		String[] anliname={"不良反应015","体外配伍068","儿童用药027","剂量范围014","哺乳用药046","围手术期020",
//				"妊娠用药012","性别用药031","成人用药018","相互作用022","细菌耐药率015","给药途径036","老人用药021",
//				"肝损害剂量027","肾损害剂量066","药物禁忌症032","药物过敏028","超适应症023",
//				"越权用药019","配伍浓度020","重复用药031","钾离子浓度069","超多日用量025"};
		String[] anliname={"不良反应015"};
		
		List anlilist=new ArrayList();
		
		String sql=null;
		
		for(int i=0;i<anliname.length;i++){
			sql="select gatherbaseinfo from sa_gather_log where anliname=? and version='1609'";
			String gatherbaseinfo=jdbcTemplate_anli.queryForObject(sql, String.class,new Object[]{anliname[i]});
			
			anlilist.add(gatherbaseinfo);
		}
		
		System.out.println(anlilist.size());
		
		//请求passcore
		final Paservice paservice2=new Paservice();
		
		for(int i=0;i<count;i++){
			for(int j=0;j<anlilist.size();j++){
				final JSONObject json=JSONObject.fromObject(anlilist.get(j));
				final JSONObject Patient=json.getJSONObject("Patient");
				//修改案例唯一码
				Patient.put("PatCode", Patient.getString("PatCode"));
				Patient.put("InHospNo", Patient.getString("InHospNo"));
				Patient.put("Name", Patient.getString("Name")+"_"+i+"_"+j);
				Patient.put("PatStatus", 2);
				//模拟PA组织caseid，PA调用审查时，将caseid存入JSON中的patcode节点中
				String caseid="Mz"+Patient.getString("PatCode")+"_"+Patient.getString("InHospNo");
				
				Patient.put("PatCode", caseid);
				json.put("Patient", Patient);
				
				JSONObject ScreenDrugList=json.getJSONObject("ScreenDrugList");
				JSONArray ScreenDrugs=ScreenDrugList.getJSONArray("ScreenDrugs");
				for (int z=0;z<ScreenDrugs.size();z++) {  
					JSONObject ScreenDrug=ScreenDrugs.getJSONObject(z);
					ScreenDrug.put("RecipNo", i*anlilist.size()+(j+1));
				} 
				ScreenDrugList.put("ScreenDrugs", ScreenDrugs);
				json.put("ScreenDrugList", ScreenDrugList);
				
				JSONObject ScreenMedCondList=json.getJSONObject("ScreenMedCondList");
				JSONArray ScreenMedConds=ScreenMedCondList.getJSONArray("ScreenMedConds");
				for (int z=0;z<ScreenMedConds.size();z++) {  
					JSONObject ScreenMedCond=ScreenMedConds.getJSONObject(z);
					ScreenMedCond.put("RecipNo", i*anlilist.size()+(j+1));
				} 
				ScreenMedCondList.put("ScreenMedConds", ScreenMedConds);
				json.put("ScreenMedCondList", ScreenMedCondList);
				
//				System.out.println(Patient.get("Name"));
				Thread t=new Thread(new Runnable(){
	    			public void run(){
	    				try {
							paservice2.getPassResult(json.toString(), "http://172.18.7.160:8081/pass/ws/paScreen");
//							String a=paservice2.getPassResult(json.toString(), "http://172.18.2.179:9099/pass/ws/paScreen");
//	    					String a = paservice2.restPost( "http://172.18.3.152:7777/pass/ws/paScreen", json.toString());
//							System.out.println("-->"+a);
//							System.out.println("-->"+json.toString());
							
	    				} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("请求发生异常");
						}
	    			}
	    		});
				t.start();
				
				try {
					if((i*anlilist.size()+(j+1))%(anlilist.size()*2)==0){
						System.out.println("循环次数："+anliname.length*count+"-->"+(i*anlilist.size()+(j+1)));
						t.join();
					}
					if((i+1==count) && (j+1==anlilist.size())){
						System.out.println("循环次数："+anliname.length*count+"-->"+(i*anlilist.size()+(j+1)));
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void Checkdata() throws ClassNotFoundException, SQLException, IOException{
		
		//缺少案例 "超多日用量025"
		String[] anliname={"不良反应015","体外配伍068","儿童用药027","剂量范围014","哺乳用药046","围手术期020",
				"妊娠用药012","性别用药031","成人用药018","相互作用022","细菌耐药率015","给药途径036","老人用药021",
				"肝损害剂量027","肾损害剂量066","药物禁忌症032","药物过敏028","超适应症023",
				"越权用药019","配伍浓度020","重复用药031","钾离子浓度069","超多日用量025"};
		
		String sql=null;
		for(int i=0;i<anliname.length;i++){
			sql="select gatherbaseinfo from sa_gather_log where anliname=? and version='1609' ";
			String gatherbaseinfo=jdbcTemplate_anli.queryForObject(sql, String.class,new Object[]{anliname[i]});
			
			JSONObject json=JSONObject.fromObject(gatherbaseinfo);
			
			JSONObject ScreenMedCondList=json.getJSONObject("ScreenMedCondList");
			JSONArray ScreenMedConds=ScreenMedCondList.getJSONArray("ScreenMedConds");
			//判断疾病里面的数据是否存在问题
			for(int j=0;j<ScreenMedConds.size();j++){
				JSONObject ScreenMedCond=(JSONObject)ScreenMedConds.get(j);
				
				if(StringUtils.isNotBlank(ScreenMedCond.get("DiseaseCode").toString())){
					sql="select count(*) from mc_dict_disease where discode=?";
					int resultsum=jdbcTemplate_mysql.queryForObject(sql, int.class,new Object[]{ScreenMedCond.get("DiseaseCode").toString()});
					
					if(resultsum==0){
						System.out.println(anliname[i]+"-->DiseaseCode:"+ScreenMedCond.getString("DiseaseCode")+" 找不到配对数据");
					}
				}
			}
			
			JSONObject ScreenDrugList=json.getJSONObject("ScreenDrugList");
			JSONArray ScreenDrugs=ScreenDrugList.getJSONArray("ScreenDrugs");
			//判断药品里面的数据是否存在问题
			for(int j=0;j<ScreenDrugs.size();j++){
				JSONObject ScreenDrug=(JSONObject)ScreenDrugs.get(j);
				
				if(StringUtils.isNotBlank(ScreenDrug.get("RouteCode").toString())){
					sql="select count(*) from mc_dict_route where routecode=?";
					int resultsum=jdbcTemplate_mysql.queryForObject(sql, int.class,new Object[]{ScreenDrug.getString("RouteCode")});
					if(resultsum==0){
						System.out.println(anliname[i]+"-->RouteCode:"+ScreenDrug.getString("RouteCode")+" 找不到配对数据");
					}
				}
				
				if(StringUtils.isNotBlank(ScreenDrug.get("DoctorCode").toString())){
					sql="select count(*) from mc_dict_doctor where doctorcode=?";
					int resultsum=jdbcTemplate_mysql.queryForObject(sql, int.class,new Object[]{ScreenDrug.getString("DoctorCode")});
					if(resultsum==0){
						System.out.println(anliname[i]+"-->DoctorCode:"+ScreenDrug.getString("DoctorCode")+" 找不到配对数据");
					}
				}
				
				if(StringUtils.isNotBlank(ScreenDrug.get("DeptCode").toString())){
					sql="select count(*) from mc_dict_dept where deptcode=?";
					int resultsum=jdbcTemplate_mysql.queryForObject(sql, int.class,new Object[]{ScreenDrug.getString("DeptCode")});
					if(resultsum==0){
						System.out.println(anliname[i]+"-->DeptCode:"+ScreenDrug.getString("DeptCode")+" 找不到配对数据");
					}
				}
			}
		}
		System.out.println("查找结束");
	}
	
	public void daofenbiao() throws ClassNotFoundException, SQLException, IOException{
		List list=null;
		String sql=null;
		
		//logid跨度，避免重复
		sql="select max(logid) from sa_pat_info";
		int count=jdbcTemplate_mysql.queryForObject(sql, int.class);
		
		//循环次数表示分表数
		for(int i=0;i<12;i++){
			//sa_pat_info数据
			sql="update sa_pat_info set logid=logid+"+count;
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="update sa_pat_info set usetime='2017-0"+(i+1)+"-01',usedate='20170"+(i+1)+"01'";
			}else{
				sql="update sa_pat_info set usetime='2017-"+(i+1)+"-01',usedate='2017"+(i+1)+"01'";
			}
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="insert into sa_pat_info_0"+(i+1)+" select * from sa_pat_info";
			}else{
				sql="insert into sa_pat_info_"+(i+1)+" select * from sa_pat_info";
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_screenresults数据
			sql="update sa_screenresults set logid=logid+"+count;
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="update sa_screenresults set usetime='2017-0"+(i+1)+"-01',usedate='20170"+(i+1)+"01'";
			}else{
				sql="update sa_screenresults set usetime='2017-"+(i+1)+"-01',usedate='2017"+(i+1)+"01'";
			}
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="insert into sa_screenresults_0"+(i+1)+" select * from sa_screenresults";
			}else{
				sql="insert into sa_screenresults_"+(i+1)+" select * from sa_screenresults";
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_disease数据
			sql="update sa_pat_disease set logid=logid+"+count;
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="update sa_pat_disease set usedate='20170"+(i+1)+"01'";
			}else{
				sql="update sa_pat_disease set usedate='2017"+(i+1)+"01'";
			}
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="insert into sa_pat_disease_0"+(i+1)+" select * from sa_pat_disease";
			}else{
				sql="insert into sa_pat_disease_"+(i+1)+" select * from sa_pat_disease";
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_allergens数据
			sql="update sa_pat_allergens set logid=logid+"+count;
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="update sa_pat_allergens set usedate='20170"+(i+1)+"01'";
			}else{
				sql="update sa_pat_allergens set usedate='2017"+(i+1)+"01'";
			}
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="insert into sa_pat_allergens_0"+(i+1)+" select * from sa_pat_allergens";
			}else{
				sql="insert into sa_pat_allergens_"+(i+1)+" select * from sa_pat_allergens";
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_orders数据
			sql="update sa_pat_orders set logid=logid+"+count;
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="update sa_pat_orders set usedate='20170"+(i+1)+"01'";
			}else{
				sql="update sa_pat_orders set usedate='2017"+(i+1)+"01'";
			}
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="insert into sa_pat_orders_0"+(i+1)+" select * from sa_pat_orders";
			}else{
				sql="insert into sa_pat_orders_"+(i+1)+" select * from sa_pat_orders";
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_operation数据
			sql="update sa_pat_operation set logid=logid+"+count;
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="update sa_pat_operation set usedate='20170"+(i+1)+"01'";
			}else{
				sql="update sa_pat_operation set usedate='2017"+(i+1)+"01'";
			}
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="insert into sa_pat_operation_0"+(i+1)+" select * from sa_pat_operation";
			}else{
				sql="insert into sa_pat_operation_"+(i+1)+" select * from sa_pat_operation";
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_request数据
			sql="update sa_request set logid=logid+"+count;
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="update sa_request set reqtime='20170"+(i+1)+"01111111111'";
				sql="update sa_request set endtime='20170"+(i+1)+"01111111111'";
			}else{
				sql="update sa_request set reqtime='2017"+(i+1)+"01111111111'";
				sql="update sa_request set endtime='20170"+(i+1)+"01111111111'";
			}
			jdbcTemplate_mysql.update(sql);
			
			if(i<9){
				sql="insert into sa_request_0"+(i+1)+" select * from sa_request";
			}else{
				sql="insert into sa_request_"+(i+1)+" select * from sa_request";
			}
			jdbcTemplate_mysql.update(sql);
			
			System.out.println(i+1+"月表导入完成");
		}
		
//		passmysqlconn.commit();  
		System.out.println("数据生成结束");
	}
	
	public void rebootlinshibiao() throws ClassNotFoundException, SQLException, IOException{
		List list=null;
		String sql=null;
		
		//清空临时表数据
		//sa_pat_info数据
		sql="truncate sa_pat_info";
		jdbcTemplate_mysql.update(sql);
		
//		sql="truncate sa_pat_info_copy";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
		
		//sa_screenresults数据
		sql="truncate sa_screenresults";
		jdbcTemplate_mysql.update(sql);
		
//		sql="truncate sa_screenresults_copy";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
		
		//sa_pat_disease数据
		sql="truncate sa_pat_disease";
		jdbcTemplate_mysql.update(sql);
		
//		sql="truncate sa_pat_disease_copy";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
		
		//sa_pat_allergens数据
		sql="truncate sa_pat_allergens";
		jdbcTemplate_mysql.update(sql);
		
//		sql="truncate sa_pat_allergens_copy";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
		
		//sa_pat_orders数据
		sql="truncate sa_pat_orders";
		jdbcTemplate_mysql.update(sql);
		
//		sql="truncate sa_pat_orders_copy";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
		
		//sa_pat_operation数据
		sql="truncate sa_pat_operation";
		jdbcTemplate_mysql.update(sql);
		
//		sql="truncate sa_pat_operation_copy";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
		
		//sa_request数据
		sql="truncate sa_request";
		jdbcTemplate_mysql.update(sql);
		
//		sql="truncate sa_request_copy";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
		
		System.out.println("临时表清空完成");
	}
	
	public void rebootfenbiao() throws ClassNotFoundException, SQLException, IOException{
		List list=null;
		String sql=null;
		
		for(int i=0;i<12;i++){
			//sa_pat_info数据
			if(i<9){
				sql="truncate sa_pat_info_0"+(i+1);
			}else{
				sql="truncate sa_pat_info_"+(i+1);
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_screenresults数据
			if(i<9){
				sql="truncate sa_screenresults_0"+(i+1);
			}else{
				sql="truncate sa_screenresults_"+(i+1);
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_disease数据
			if(i<9){
				sql="truncate sa_pat_disease_0"+(i+1);
			}else{
				sql="truncate sa_pat_disease_"+(i+1);
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_allergens数据
			if(i<9){
				sql="truncate sa_pat_allergens_0"+(i+1);
			}else{
				sql="truncate sa_pat_allergens_"+(i+1);
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_orders数据
			if(i<9){
				sql="truncate sa_pat_orders_0"+(i+1);
			}else{
				sql="truncate sa_pat_orders_"+(i+1);
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_operation数据
			if(i<9){
				sql="truncate sa_pat_operation_0"+(i+1);
			}else{
				sql="truncate sa_pat_operation_"+(i+1);
			}
			jdbcTemplate_mysql.update(sql);
			
			//sa_request数据
			if(i<9){
				sql="truncate sa_request_0"+(i+1);
			}else{
				sql="truncate sa_request_"+(i+1);
			}
			jdbcTemplate_mysql.update(sql);
			
			System.out.println(i+1+"月表清空完成");
		}
		
		System.out.println("数据重置结束");
	}
	
	public void copydata() throws ClassNotFoundException, SQLException, IOException{
		List list=null;
		String sql=null;
		
		//复制数据到临时表
//		//sa_pat_info数据
//		sql="insert into sa_pat_info_copy select * from sa_pat_info_copy1";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
//		
//		//sa_screenresults数据
//		sql="insert into sa_screenresults_copy select * from sa_screenresults_copy1";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
//		
//		//sa_pat_disease数据
//		sql="insert into sa_pat_disease_copy select * from sa_pat_disease_copy1";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
//		
//		//sa_pat_allergens数据
//		sql="insert into sa_pat_allergens_copy select * from sa_pat_allergens_copy1";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
//		
//		//sa_pat_orders数据
//		sql="insert into sa_pat_orders_copy select * from sa_pat_orders_copy1";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
//		
//		//sa_pat_operation数据
//		sql="insert into sa_pat_operation_copy select * from sa_pat_operation_copy1";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
			
		//修改临时表主键，处理完数据后再还原
		//sa_screenresults数据
		sql="alter table sa_screenresults change chkresid chkresid bigint null,drop primary key";
		jdbcTemplate_mysql.update(sql);
		
		//sa_pat_disease数据
		sql="alter table sa_pat_disease change disid disid bigint null,drop primary key";
		jdbcTemplate_mysql.update(sql);
		
		//sa_pat_allergens数据
		sql="alter table sa_pat_allergens change allerid allerid bigint null,drop primary key";
		jdbcTemplate_mysql.update(sql);
		
		//sa_pat_orders数据
		sql="alter table sa_pat_orders change cid cid bigint null,drop primary key";
		jdbcTemplate_mysql.update(sql);
		
		//sa_pat_operation数据
		sql="alter table sa_pat_operation change oprid oprid bigint null,drop primary key";
		jdbcTemplate_mysql.update(sql);
				
//		//sa_request数据
//		sql="alter table sa_request change logid logid bigint null,drop primary key";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
		
		//logid跨度，避免重复
		sql="select max(logid) from sa_pat_info_copy1";
		int count=jdbcTemplate_mysql.queryForObject(sql, int.class);
		
		//复制数据到临时表的循环次数，制造数据
		int xunhuan=50000;
		System.out.println("开始循环制造数据");
		for(int i=0;i<xunhuan;i++){
			//sa_pat_info数据
			sql="truncate sa_pat_info_copy";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_info_copy select * from sa_pat_info_copy1";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_info_copy set logid=logid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_info_copy set patcode=CONCAT_WS('_',patcode,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_info_copy set inhospno=CONCAT_WS('_',inhospno,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_info_copy set patname=CONCAT_WS('_',patname,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_info_copy set caseid=CONCAT_WS('_',caseid,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_info select * from sa_pat_info_copy";
			jdbcTemplate_mysql.update(sql);
			
			//sa_screenresults数据
			sql="truncate sa_screenresults_copy";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_screenresults_copy select * from sa_screenresults_copy1";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_screenresults_copy set chkresid=chkresid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_screenresults_copy set logid=logid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_screenresults_copy set patcode=CONCAT_WS('_',patcode,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_screenresults_copy set inhospno=CONCAT_WS('_',inhospno,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_screenresults_copy set patname=CONCAT_WS('_',patname,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_screenresults_copy set caseid=CONCAT_WS('_',caseid,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_screenresults select * from sa_screenresults_copy";
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_disease数据
			sql="truncate sa_pat_disease_copy";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_disease_copy select * from sa_pat_disease_copy1";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_disease_copy set disid=disid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_disease_copy set logid=logid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_disease_copy set caseid=CONCAT_WS('_',caseid,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_disease select * from sa_pat_disease_copy";
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_allergens数据
			sql="truncate sa_pat_allergens_copy";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_allergens_copy select * from sa_pat_allergens_copy1";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_allergens_copy set allerid=allerid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_allergens_copy set logid=logid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_allergens_copy set caseid=CONCAT_WS('_',caseid,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_allergens select * from sa_pat_allergens_copy";
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_orders数据
			sql="truncate sa_pat_orders_copy";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_orders_copy select * from sa_pat_orders_copy1";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_orders_copy set cid=cid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_orders_copy set logid=logid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_orders_copy set caseid=CONCAT_WS('_',caseid,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_orders select * from sa_pat_orders_copy";
			jdbcTemplate_mysql.update(sql);
			
			//sa_pat_operation数据
			sql="truncate sa_pat_operation_copy";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_operation_copy select * from sa_pat_operation_copy1";
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_operation_copy set oprid=oprid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_operation_copy set logid=logid+"+count*i;
			jdbcTemplate_mysql.update(sql);
			
			sql="update sa_pat_operation_copy set caseid=CONCAT_WS('_',caseid,'"+(i+1)+"')";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_pat_operation select * from sa_pat_operation_copy";
			jdbcTemplate_mysql.update(sql);
				
			//sa_request数据
			sql="truncate sa_request_copy";
			jdbcTemplate_mysql.update(sql);
			
			sql="insert into sa_request_copy select * from sa_request_copy1";
			jdbcTemplate_mysql.update(sql);
			
//			sql="update sa_request_copy set logid=logid+"+count*i;
//			st=passmysqlconn.createStatement();
//			st.executeUpdate(sql);
//			
//			sql="update sa_request_copy set caseid=CONCAT_WS('_',caseid,'"+(i+1)+"')";
//			st=passmysqlconn.createStatement();
//			st.executeUpdate(sql);
			
//			sql="insert into sa_request select * from sa_request_copy";
//			st=passmysqlconn.createStatement();
//			st.executeUpdate(sql);
			
			System.out.println(xunhuan+"-->"+(i+1));
		}
				
		
		//还原主键
		//sa_screenresults数据
		sql="alter table sa_screenresults change chkresid chkresid bigint not null auto_increment primary key";
		jdbcTemplate_mysql.update(sql);
		
		//sa_pat_disease数据
		sql="alter table sa_pat_disease change disid disid bigint not null auto_increment primary key";
		jdbcTemplate_mysql.update(sql);
		
		//sa_pat_allergens数据
		sql="alter table sa_pat_allergens change allerid allerid bigint not null auto_increment primary key";
		jdbcTemplate_mysql.update(sql);
		
		//sa_pat_orders数据
		sql="alter table sa_pat_orders change cid cid bigint not null auto_increment primary key";
		jdbcTemplate_mysql.update(sql);
		
		//sa_pat_operation数据
		sql="alter table sa_pat_operation change oprid oprid bigint not null auto_increment primary key";
		jdbcTemplate_mysql.update(sql);
				
//		//sa_request数据
//		sql="alter table sa_request change logid logid bigint not null auto_increment primary key";
//		st=passmysqlconn.createStatement();
//		st.executeUpdate(sql);
				
//		passmysqlconn.commit();  
		System.out.println("数据复制结束");
	}
	
	public void copydata1() throws ClassNotFoundException, SQLException, IOException, InterruptedException{
		List list=null;
		String sql=null;
			
		//每条数据的循环次数
		final int xunhuan=3;
		
		//logid跨度，避免重复
		sql="select max(logid) from sa_pat_info_copy";
		final int count=jdbcTemplate_mysql.queryForObject(sql, int.class);
		
		System.out.println("开始循环制造数据");
		//sa_pat_info数据
		Thread t1=new Thread(new Runnable(){
			public void run(){
				try {
					List list=null;
					final List listbatch=new ArrayList();
					String sql=null;
					
					sql="select * from sa_pat_info_copy";
					list=jdbcTemplate_mysql.queryForList(sql);
					
					for(int j=0;j<list.size();j++){
						Map map=(Map)list.get(j);
						for(int i=0;i<xunhuan;i++){
							map.put("patname", map.get("patname").toString()+"_"+(i+1));
							map.put("caseid", map.get("caseid").toString()+"_"+(i+1));//
							map.put("logid", Integer.parseInt(map.get("logid").toString())+count*i);//
							map.put("patcode", map.get("patcode").toString()+"_"+(i+1));//
							map.put("inhospno", map.get("inhospno").toString()+"_"+(i+1));//
							listbatch.add(map);
							
						}
						System.out.println("sa_pat_info:"+list.size()*xunhuan+"-->"+((j+1)*xunhuan));
					}
					
					sql="insert into sa_pat_info (birthday, hepdamagedegree, sex, doctorname, weight, patstatus,"
							+ " islactation, inserttime, height, deptcode, ispregnancy, rendamagedegree, usetime, "
							+ "usedate, deptname, patname, caseid, pregstartdate, visitcode, doctorcode, logid, "
							+ "hisname, patcode, inhospno, hiscode, checkmode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?,?,?,?,?)";
					
					jdbcTemplate_mysql.batchUpdate(sql,new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement pst, int i) throws SQLException {
							Map map=(Map)listbatch.get(i);
							
							try{
								pst.setString(1,map.get("birthday").toString());//
								pst.setString(2,map.get("hepdamagedegree").toString());//
								pst.setString(3,map.get("sex").toString());//
								pst.setString(4,map.get("doctorname").toString());//
								pst.setString(5,map.get("weight").toString());//
								pst.setString(6,map.get("patstatus").toString());//
								pst.setString(7,map.get("islactation").toString());//
								pst.setString(8,map.get("inserttime").toString());//
								pst.setString(9,map.get("height").toString());//
								pst.setString(10,map.get("deptcode").toString());//
								pst.setString(11,map.get("ispregnancy").toString());//
								pst.setString(12,map.get("rendamagedegree").toString());//
								pst.setString(13,map.get("usetime").toString());//
								pst.setString(14,map.get("usedate").toString());//
								pst.setString(15,map.get("deptname").toString());//
								pst.setString(16,map.get("patname").toString());//
								pst.setString(17,map.get("caseid").toString());//
								pst.setString(18,map.get("pregstartdate").toString());//
								pst.setString(19,map.get("visitcode").toString());//
								pst.setString(20,map.get("doctorcode").toString());//
								pst.setInt(21,Integer.parseInt(map.get("logid").toString()));//
								pst.setString(22,map.get("hisname").toString());//
								pst.setString(23,map.get("patcode").toString());//
								pst.setString(24,map.get("inhospno").toString());//
								pst.setString(25,map.get("hiscode").toString());//
								pst.setString(26,map.get("checkmode").toString());//
								
							}catch(Exception e){
								System.out.println("sa_pat_info出现异常的数据:"+map);
								System.out.println(e);
							}
						}
						public int getBatchSize() {
							//这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
							return listbatch.size();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("sa_pat_info制造数据异常");
				}
			}
		});
		t1.start();
		
		//sa_screenresults数据
		Thread t2=new Thread(new Runnable(){
			public void run(){
				try {
					List list=null;
					final List listbatch=new ArrayList();
					String sql=null;
					
					sql="select * from sa_screenresults_copy";
					list=jdbcTemplate_mysql.queryForList(sql);
					
					for(int j=0;j<list.size();j++){
						Map map=(Map)list.get(j);
						for(int i=0;i<xunhuan;i++){
							map.put("patname", map.get("patname").toString()+"_"+(i+1));
							map.put("caseid", map.get("caseid").toString()+"_"+(i+1));//
							map.put("logid", Integer.parseInt(map.get("logid").toString())+count*i);//
							map.put("patcode", map.get("patcode").toString()+"_"+(i+1));//
							map.put("inhospno", map.get("inhospno").toString()+"_"+(i+1));//
							listbatch.add(map);
							
						}
						System.out.println("sa_screenresults:"+list.size()*xunhuan+"-->"+((j+1)*xunhuan));
					}
					
					sql="insert into sa_screenresults (istempdrug, doctorname, moduleitems, orderindex, "
							+ "reason, recipno, frequency, slcode, patstatus, dosepertime, drugname, modulename, "
							+ "drugcode, isuser, routecode, deptcode, usetime, moduleid, usedate, deptname, "
							+ "patname, caseid, patstatusdesc, visitcode, doctorcode, slcodename, drug_unique_code, "
							+ "severity, startdatetime, is_forstatic, routename, enddatetime, checktype, logid, hisname, "
							+ "executetime, patcode, inhospno, hiscode, doseunit, shield, otherinfo, warning) "
							+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					jdbcTemplate_mysql.batchUpdate(sql,new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement pst, int i) throws SQLException {
							Map map=(Map)listbatch.get(i);
							
							try{
								pst.setString(1,map.get("istempdrug").toString());
								pst.setString(2,map.get("doctorname").toString());
								pst.setString(3,map.get("moduleitems").toString());
								pst.setString(4,map.get("orderindex").toString());
								pst.setString(5,map.get("reason").toString());
								pst.setString(6,map.get("recipno").toString());
								pst.setString(7,map.get("frequency").toString());
								pst.setString(8,map.get("slcode").toString());
								pst.setString(9,map.get("patstatus").toString());
								pst.setString(10,map.get("dosepertime").toString());
								pst.setString(11,map.get("drugname").toString());
								pst.setString(12,map.get("modulename").toString());
								pst.setString(13,map.get("drugcode").toString());
								pst.setString(14,map.get("isuser").toString());
								pst.setString(15,map.get("routecode").toString());
								pst.setString(16,map.get("deptcode").toString());
								pst.setString(17,map.get("usetime").toString());
								pst.setString(18,map.get("moduleid").toString());
								pst.setString(19,map.get("usedate").toString());
								pst.setString(20,map.get("deptname").toString());
								pst.setString(21,map.get("patname").toString());
								pst.setString(22,map.get("caseid").toString());
								pst.setString(23,map.get("patstatusdesc").toString());
								pst.setString(24,map.get("visitcode").toString());
								pst.setString(25,map.get("doctorcode").toString());
								pst.setString(26,map.get("slcodename").toString());
								pst.setString(27,map.get("drug_unique_code").toString());
								pst.setString(28,map.get("severity").toString());
								pst.setString(29,map.get("startdatetime").toString());
								pst.setString(30,map.get("is_forstatic").toString());
								pst.setString(31,map.get("routename").toString());
								pst.setString(32,map.get("enddatetime").toString());
								pst.setString(33,map.get("checktype").toString());
								pst.setInt(34,Integer.parseInt(map.get("logid").toString()));
								pst.setString(35,map.get("hisname").toString());
								pst.setString(36,map.get("executetime").toString());
								pst.setString(37,map.get("patcode").toString());
								pst.setString(38,map.get("inhospno").toString());
								pst.setString(39,map.get("hiscode").toString());
								pst.setString(40,map.get("doseunit").toString());
								pst.setString(41,map.get("shield").toString());
								pst.setString(42,map.get("otherinfo").toString());
								pst.setString(43,map.get("warning").toString());
								
							}catch(Exception e){
								System.out.println("sa_pat_info出现异常的数据:"+map);
								System.out.println(e);
							}
						}
						public int getBatchSize() {
							//这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
							return listbatch.size();
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("sa_screenresults制造数据异常");
				}
			}
		});
		t2.start();
		
		//sa_pat_disease数据
		Thread t3=new Thread(new Runnable(){
			public void run(){
				try {
					List list=null;
					final List listbatch=new ArrayList();
					String sql=null;
					
					sql="select * from sa_pat_disease_copy";
					list=jdbcTemplate_mysql.queryForList(sql);
					
					for(int j=0;j<list.size();j++){
						Map map=(Map)list.get(j);
						for(int i=0;i<xunhuan;i++){
							map.put("caseid", map.get("caseid").toString()+"_"+(i+1));//
							map.put("logid", Integer.parseInt(map.get("logid").toString())+count*i);//
							listbatch.add(map);
							
						}
						System.out.println("sa_pat_disease:"+list.size()*xunhuan+"-->"+((j+1)*xunhuan));
					}
					
					sql="insert into sa_pat_disease (hisname, discode, dissource, hiscode, recipno, "
							+ "usedate, disname, disindex, caseid, logid) "
							+ "values(?,?,?,?,?,?,?,?,?,?)";
					
					jdbcTemplate_mysql.batchUpdate(sql,new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement pst, int i) throws SQLException {
							Map map=(Map)listbatch.get(i);
							
							try{
								pst.setString(1,map.get("hisname").toString());
								pst.setString(2,map.get("discode").toString());
								pst.setString(3,map.get("dissource").toString());
								pst.setString(4,map.get("hiscode").toString());
								pst.setString(5,map.get("recipno").toString());
								pst.setString(6,map.get("usedate").toString());
								pst.setString(7,map.get("disname").toString());
								pst.setString(8,map.get("disindex").toString());
								pst.setString(9,map.get("caseid").toString());
								pst.setInt(10,Integer.parseInt(map.get("logid").toString()));
								
							}catch(Exception e){
								System.out.println("sa_pat_disease出现异常的数据:"+map);
								System.out.println(e);
							}
						}
						public int getBatchSize() {
							//这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
							return listbatch.size();
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("sa_pat_disease制造数据异常");
				}
			}
		});
		t3.start();
		
		//sa_pat_allergens数据
		Thread t4=new Thread(new Runnable(){
			public void run(){
				try {
					List list=null;
					final List listbatch=new ArrayList();
					String sql=null;
					
					sql="select * from sa_pat_allergens_copy";
					list=jdbcTemplate_mysql.queryForList(sql);
					
					for(int j=0;j<list.size();j++){
						Map map=(Map)list.get(j);
						for(int i=0;i<xunhuan;i++){
							map.put("caseid", map.get("caseid").toString()+"_"+(i+1));//
							map.put("logid", Integer.parseInt(map.get("logid").toString())+count*i);//
							listbatch.add(map);
							
						}
						System.out.println("sa_pat_allergens:"+list.size()*xunhuan+"-->"+((j+1)*xunhuan));
					}
					
					sql="insert into sa_pat_allergens (hisname, allercode, allersource, allerindex, "
							+ "hiscode, allername, usedate, caseid, symptom, logid) "
							+ "values(?,?,?,?,?,?,?,?,?,?)";
					
					jdbcTemplate_mysql.batchUpdate(sql,new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement pst, int i) throws SQLException {
							Map map=(Map)listbatch.get(i);
							
							try{
								pst.setString(1,map.get("hisname").toString());
								pst.setString(2,map.get("allercode").toString());
								pst.setString(3,map.get("allersource").toString());
								pst.setString(4,map.get("allerindex").toString());
								pst.setString(5,map.get("hiscode").toString());
								pst.setString(6,map.get("allername").toString());
								pst.setString(7,map.get("usedate").toString());
								pst.setString(8,map.get("caseid").toString());
								pst.setString(9,map.get("symptom").toString());
								pst.setInt(10,Integer.parseInt(map.get("logid").toString()));
								
							}catch(Exception e){
								System.out.println("sa_pat_allergens出现异常的数据:"+map);
								System.out.println(e);
							}
						}
						public int getBatchSize() {
							//这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
							return listbatch.size();
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("sa_pat_allergens制造数据异常");
				}
			}
		});
		t4.start();
		
		//sa_pat_orders数据
		Thread t5=new Thread(new Runnable(){
			public void run(){
				try {
					List list=null;
					final List listbatch=new ArrayList();
					String sql=null;
					
					sql="select * from sa_pat_orders_copy";
					list=jdbcTemplate_mysql.queryForList(sql);
					
					for(int j=0;j<list.size();j++){
						Map map=(Map)list.get(j);
						for(int i=0;i<xunhuan;i++){
							map.put("caseid", map.get("caseid").toString()+"_"+(i+1));//
							map.put("logid", Integer.parseInt(map.get("logid").toString())+count*i);//
							listbatch.add(map);
							
						}
						System.out.println("sa_pat_allergens:"+list.size()*xunhuan+"-->"+((j+1)*xunhuan));
					}
					
					sql="insert into sa_pat_orders (grouptag, istempdrug, orderindex, doctorname, maxwarn, remark, orderno, "
							+ "recipno, purpose, frequency, dosepertime, operationcode, drugname, routecode, deptcode, "
							+ "allergens_strs, usedate, freqsource, deptname, caseid, operation_strs, drugsource, doctorcode, "
							+ "routesource, drug_unique_code, startdatetime, ordertype, routename, enddatetime, disease_strs, "
							+ "logid, hisname, num, executetime, medtime, hiscode, orders_strs, doseunit, numunit) "
							+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					
					jdbcTemplate_mysql.batchUpdate(sql,new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement pst, int i) throws SQLException {
							Map map=(Map)listbatch.get(i);
							
							try{
								pst.setString(1,map.get("grouptag").toString());
								pst.setString(2,map.get("istempdrug").toString());
								pst.setString(3,map.get("orderindex").toString());
								pst.setString(4,map.get("doctorname").toString());
								pst.setString(5,map.get("maxwarn").toString());
								pst.setString(6,map.get("remark").toString());
								pst.setString(7,map.get("orderno").toString());
								pst.setString(8,map.get("recipno").toString());
								pst.setString(9,map.get("purpose").toString());
								pst.setString(10,map.get("frequency").toString());
								pst.setString(11,map.get("dosepertime").toString());
								pst.setString(12,map.get("operationcode").toString());
								pst.setString(13,map.get("drugname").toString());
								pst.setString(14,map.get("routecode").toString());
								pst.setString(15,map.get("deptcode").toString());
								pst.setString(16,(String)map.get("allergens_strs"));
								pst.setString(17,map.get("usedate").toString());
								pst.setString(18,map.get("freqsource").toString());
								pst.setString(19,map.get("deptname").toString());
								pst.setString(20,map.get("caseid").toString());
								pst.setString(21,(String)map.get("operation_strs"));
								pst.setString(22,map.get("drugsource").toString());
								pst.setString(23,map.get("doctorcode").toString());
								pst.setString(24,map.get("routesource").toString());
								pst.setString(25,map.get("drug_unique_code").toString());
								pst.setString(26,map.get("startdatetime").toString());
								pst.setString(27,map.get("ordertype").toString());
								pst.setString(28,map.get("routename").toString());
								pst.setString(29,map.get("enddatetime").toString());
								pst.setString(30,(String)map.get("disease_strs"));
								pst.setInt(31,Integer.parseInt(map.get("logid").toString()));
								pst.setString(32,map.get("hisname").toString());
								pst.setString(33,map.get("num").toString());
								pst.setString(34,map.get("executetime").toString());
								pst.setString(35,map.get("medtime").toString());
								pst.setString(36,map.get("hiscode").toString());
								pst.setString(37,(String)map.get("orders_strs"));
								pst.setString(38,map.get("doseunit").toString());
								pst.setString(39,map.get("numunit").toString());
								
							}catch(Exception e){
								System.out.println("sa_pat_orders出现异常的数据:"+map);
								System.out.println(e);
							}
						}
						public int getBatchSize() {
							//这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
							return listbatch.size();
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("sa_pat_orders制造数据异常");
				}
			}
		});
		t5.start();
		
		//sa_pat_operation数据
		Thread t6=new Thread(new Runnable(){
			public void run(){
				try {
					List list=null;
					final List listbatch=new ArrayList();
					String sql=null;
					
					sql="select * from sa_pat_operation_copy";
					list=jdbcTemplate_mysql.queryForList(sql);
					
					for(int j=0;j<list.size();j++){
						Map map=(Map)list.get(j);
						for(int i=0;i<xunhuan;i++){
							map.put("caseid", map.get("caseid").toString()+"_"+(i+1));//
							map.put("logid", Integer.parseInt(map.get("logid").toString())+count*i);//
							listbatch.add(map);
							
						}
						System.out.println("sa_pat_allergens:"+list.size()*xunhuan+"-->"+((j+1)*xunhuan));
					}
					
					sql="insert into sa_pat_operation (hisname, oprenddate, oprname, hiscode, incisiontype, oprindex, "
							+ "usedate, oprstartdate, operationcode, caseid, logid) "
							+ "values(?,?,?,?,?,?,?,?,?,?,?)";
					
					jdbcTemplate_mysql.batchUpdate(sql,new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement pst, int i) throws SQLException {
							Map map=(Map)listbatch.get(i);
							
							try{
								pst.setString(1,map.get("hisname").toString());
								pst.setString(2,map.get("oprenddate").toString());
								pst.setString(3,map.get("oprname").toString());
								pst.setString(4,map.get("hiscode").toString());
								pst.setInt(5,Integer.parseInt(map.get("incisiontype").toString()));
								pst.setString(6,map.get("oprindex").toString());
								pst.setString(7,map.get("usedate").toString());
								pst.setString(8,map.get("oprstartdate").toString());
								pst.setString(9,map.get("operationcode").toString());
								pst.setString(10,map.get("caseid").toString());
								pst.setInt(11,Integer.parseInt(map.get("logid").toString()));
								
							}catch(Exception e){
								System.out.println("sa_pat_orders出现异常的数据:"+map);
								System.out.println(e);
							}
						}
						public int getBatchSize() {
							//这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
							return listbatch.size();
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("sa_pat_operation制造数据异常");
				}
			}
		});
		t6.start();
		
		//sa_pat_operation数据
		Thread t7=new Thread(new Runnable(){
			public void run(){
				try {
					List list=null;
					final List listbatch=new ArrayList();
					String sql=null;
					
					sql="select * from sa_request_copy";
					list=jdbcTemplate_mysql.queryForList(sql);
					
					for(int j=0;j<list.size();j++){
						Map map=(Map)list.get(j);
						for(int i=0;i<xunhuan;i++){
							map.put("caseid", map.get("caseid").toString()+"_"+(i+1));//
							map.put("logid", Integer.parseInt(map.get("logid").toString())+count*i);//
							listbatch.add(map);
							
						}
						System.out.println("sa_pat_allergens:"+list.size()*xunhuan+"-->"+((j+1)*xunhuan));
					}
					
					//sa_request数据
					sql="alter table sa_request change logid logid bigint null,drop primary key";
					jdbcTemplate_mysql.update(sql);
					
					sql="insert into sa_request (endtime, reqtype, clientip, RESPONDTIME, reqtime, caseid, logid) "
							+ "values(?,?,?,?,?,?,?)";
					
					jdbcTemplate_mysql.batchUpdate(sql,new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement pst, int i) throws SQLException {
							Map map=(Map)listbatch.get(i);
							try{
								pst.setString(1,map.get("endtime").toString());
								pst.setInt(2,Integer.parseInt(map.get("reqtype").toString()));
								pst.setString(3,map.get("clientip").toString());
								pst.setString(4,map.get("RESPONDTIME").toString());
								pst.setString(5,map.get("reqtime").toString());
								pst.setString(6,map.get("caseid").toString());
								pst.setInt(7,Integer.parseInt(map.get("logid").toString()));
								
							}catch(Exception e){
								System.out.println("sa_pat_orders出现异常的数据:"+map);
								System.out.println(e);
							}
						}
						public int getBatchSize() {
							//这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
							return listbatch.size();
						}
					});
					
					
					//sa_request还原主键
					sql="alter table sa_request change logid logid bigint not null auto_increment primary key";
					jdbcTemplate_mysql.update(sql);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("sa_request制造数据异常");
				}
			}
		});
		t7.start();
		
		t1.join();
		System.out.println("sa_pat_info 复制结束");
		t2.join();
		System.out.println("sa_screenresults 复制结束");
		t3.join();
		System.out.println("sa_pat_disease 复制结束");
		t4.join();
		System.out.println("sa_pat_allergens 复制结束");
		t5.join();
		System.out.println("sa_pat_orders 复制结束");
		t6.join();
		System.out.println("sa_pat_operation 复制结束");
		t7.join();
		System.out.println("sa_request 复制结束");
		
		System.out.println("数据制造结束");
	}
	
	
	
	
	public void passredis(){
		//redis连接
		jedis = new Jedis("172.18.7.160", 6379);
		jedis.auth("123");
		jedis.select(0);

		System.out.println(jedis.ping());
		
		//PASS redis查询
		Set<String> keys = jedis.keys("*X_SA_SR*"); 
		System.out.println("redis-key PASS总数:"+keys.size());
	}
	
	public void passredis_clear(){
		//redis连接
		jedis = new Jedis("172.18.7.160", 6379);
		jedis.auth("123");
		jedis.select(0);

		System.out.println(jedis.ping());
		
		//PASS redis查询
		Set<String> keys = jedis.keys("*X_SA_SR*"); 
		Iterator<String> it=keys.iterator() ;   
		int a =0;
		while(it.hasNext()){
			a=a+1;
		    String key = it.next();   
//				    System.out.println(key);
		    System.out.println(keys.size()+"-->"+a);
		    jedis.del(key);
		}
	}
	
	public void paredis(){
		//redis连接
		jedis = new Jedis("172.18.7.160", 6379);
		jedis.auth("123");
		jedis.select(0);

		System.out.println(jedis.ping());
		
		//PASS redis查询
		Set<String> keys1 = jedis.keys(PA_SCREENRESULTS);
		System.out.println("redis-key PA总数:"+keys1.size());
		
//		jedis.del(PA_SCREENRESULTS);
//		System.out.println(jedis.get(PA_SCREENRESULTS));
		
		//redis数据类型为list时
//		System.out.println("list内部数据总数："+jedis.llen(PA_SCREENRESULTS));
//		System.out.println(jedis.lrange(PA_SCREENRESULTS, 0, 10));
		
		//jedis.lrange(key, start, len); len=-1表示不限制
//		List<String> values = jedis.lrange("PA_SCREENRESULT_LIST", 0, -1);
//		for(int i=0;i<values.size();i++){
//			System.out.println(values.get(i));
//		}
		
	}
	
	public void paredis_clear(){
		//redis连接
		jedis = new Jedis("172.18.7.160", 6379);
		jedis.auth("123");
		jedis.select(0);

		System.out.println(jedis.ping());
		
		jedis.del(PA_SCREENRESULTS);
		System.out.println("redis剩余数量："+jedis.llen(PA_SCREENRESULTS));
	}
	
	@Test
	public void test1(){
		Autoscreen autoscreen=new Autoscreen();
		
		//PASS功能===========================
		
		/**
		 * 检查案例里面的医生、科室、疾病等是否在字典表中
		 */
//		autoscreen.Checkdata();
		
		/**
		 * 调用PASS审查，制造redis数据,测试redis效率
		 */
			//清空临时表
//		autoscreen.rebootlinshibiao();
//		long startTime = System.currentTimeMillis();
//		autoscreen.PASS();
//		long endTime = System.currentTimeMillis();
//		System.out.println("总耗时："+(endTime-startTime)+"毫秒");
		
		//redis查询
//		autoscreen.passredis();
		//redis清空方法
//		autoscreen.passredis_clear();
		
		/**
		 * 制作PASS统计分析数据
		 */
//		long startTime = System.currentTimeMillis();
			//PASS统计分析数据准备，准备一份案例，通过PASS审查，通过AP工程从redis里面将数据预处理到临时表，1,拷贝1份出来，2，拷贝两份出来
			//清空分表正式表数据，清空临时表,
//		autoscreen.rebootlinshibiao();
//		autoscreen.rebootfenbiao();
			//临时表制造大数据（方案1：拷贝两份出来）（不考虑使用这个方法）
//		autoscreen.copydata();
			//临时表制造大数据,（方案2：拷贝1份出来）
//		autoscreen.copydata1();
//			//从临时表导数据到正式表（分别导入到12份分表）
//		autoscreen.daofenbiao();
//		long endTime = System.currentTimeMillis();
//		System.out.println("总耗时："+(endTime-startTime)+"毫秒");
		
		
		//PA功能=====================
		
		/**
		 * 测试PA效率
		 */
			//调用pa自动审查接口
//		long startTime = System.currentTimeMillis();
			//十万级病人数据审查时间估计15分钟
//		autoscreen.PA();
//		long endTime = System.currentTimeMillis();
//		System.out.println("总耗时："+(endTime-startTime)+"毫秒");
		
		//redis查询
//		autoscreen.paredis();
		//redis清空方法
//		autoscreen.paredis_clear();
	}
}
