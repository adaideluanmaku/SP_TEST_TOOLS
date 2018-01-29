package ch.com.PAandPassModule_Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试  
@ContextConfiguration(locations={"classpath:Spring-mvc-servlet.xml"}) //加载配置文件   
public class Tests {
	
	@Autowired
	JdbcTemplate jdbcTemplate_sqlserver;
	
	@Autowired
	JdbcTemplate jdbcTemplate_mysql;
	
	@Test
	public void test1(){
		final int count=1;//一批案例的循环次数
		
		int mz=1;//控制数据制造开关 0关，1开
		int zy=0;
		int cy=0;
		int dict=0;
		
		final int mhiscode=199004;
		final int ienddate=20170101;
		final String enddate="2017-01-01";
		
//		int iid=0;
//		int a=0;
//		int b=0;
		
		//缺少案例 "超多日用量025"
		String[] anliname={"不良反应015","体外配伍068","儿童用药027","剂量范围014","哺乳用药046","围手术期020",
				"妊娠用药012","性别用药031","成人用药018","相互作用022","细菌耐药率015","给药途径036","老人用药021",
				"肝损害剂量027","肾损害剂量066","药物禁忌症032","药物过敏028","超适应症023",
				"越权用药019","配伍浓度020","重复用药031","钾离子浓度069"};
//		String[] anliname={"不良反应015"};
		
		final List anlilist=new ArrayList();
		List list=null;
		String sql=null;
		
		//打印字段
//		sql="select * from t_mc_inhosp_patient where rownum = 1 ";//oracle
		sql="select * from mc_review_main limit 1 ";
		list=jdbcTemplate_sqlserver.queryForList(sql);
		
		Map map=(Map) list.get(0);
		String a=map.keySet().toString();
		System.out.println(a);
		String[] b=a.substring(1,a.length()-1).split(",");
		
		System.out.println(b.length);
		String c="";
		int d=0;
		for(int i=0;i<b.length;i++){
			d=d+1;
			//去除没用字段
			if("inserttime11".equals(b[i].trim())){
				d=d-1;
			}else{
				c=c+"?,";
				System.out.println("pst.setString("+(d)+",strisnull.isnull(map.get(\""+b[i].trim()+"\")).toString());//"+b[i].trim());
//				System.out.println("pst.setString("+(d)+",map.get(\""+b[i].trim()+"\").toString());//"+b[i].trim());
//				System.out.println("pst.setString("+(d)+",ScreenDrug.getString(\""+b[i].trim()+"\"));//"+b[i].trim());
//				System.out.println("pst.setString("+(d)+",Patient.getString(\""+b[i].trim()+"\"));//"+b[i].trim());
			}
		}
		System.out.println(c);
		
	}
}
