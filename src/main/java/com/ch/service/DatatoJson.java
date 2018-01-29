package com.ch.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//将PA的审查数据转成JSON串
@Service
public class DatatoJson{
	@Autowired
	JdbcTemplate jdbcTemplate_sqlserver;

	public String pa_winresult(String visitcode){
		String sql=null;
		String result=null;
		
		sql="select distinct severity,orderno,drugmaxwarn,recipeno,visitcode,drugname,drug_unique_code,usetime,"
				+ "costunit,moduleitem,patstatus,modulename,warning,slcode,moduleid,cid,drugspec from "
				+ "t_pharm_screenresults where visitcode=?";
		List list=jdbcTemplate_sqlserver.queryForList(sql,new Object[]{visitcode});
		System.out.println(list);
		
//		List list1=new ArrayList(17);
//		for(int i=0;i<list.size();i++){
//			
//		}
		
		JSONArray jsonarray=new JSONArray();
//		JSONObject json=new JSONObject();
		for(int i=0;i<list.size();i++){
			Map map=(Map)list.get(i);
			JSONObject json=JSONObject.fromObject(map);
			jsonarray.add(json);
		}
		System.out.println(jsonarray);
		result=jsonarray.toString();
		
		sql="delete from t_pharm_screenresults where visitcode=?";
		jdbcTemplate_sqlserver.update(sql,new Object[]{visitcode});
		return result;
	}
}
