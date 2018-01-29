package ch.com.PAandPassModule_Test;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;

//redis浣块敓鐭嚖鎷烽敓鏂ゆ嫹

public class Reditsdu {
	public static Jedis jedis;
	public static final String PA_SCREENRESULTS = "PA_SCREENRESULT_LIST";
	public static void main(String args[]){
		//redis连接
		jedis = new Jedis("172.18.7.160", 6379);
		jedis.auth("123");
		jedis.select(0);

		System.out.println(jedis.ping());
		
//		 //PASS redis查询
		Set<String> keys = jedis.keys("*X_SA_SR*"); 
		System.out.println("redis-key PASS总数:"+keys.size());
//		Iterator<String> it=keys.iterator() ;   
//		int a =0;
//		while(it.hasNext()){
//			a=a+1;
//		    String key = it.next();   
////		    System.out.println(key);
//		    System.out.println(keys.size()+"-->"+a);
//		    jedis.del(key);
//		}
		
		
		//PA redis查询
		Set<String> keys1 = jedis.keys(PA_SCREENRESULTS);
		System.out.println("redis-key PA总数:"+keys1.size());
//		
//		//读取list里面数据
//		System.out.println(jedis.llen(PA_SCREENRESULTS));
//		System.out.println(jedis.lrange(PA_SCREENRESULTS, 0, 10));
		
		//清空
//		jedis.del(PA_SCREENRESULTS);
//		System.out.println(jedis.llen(PA_SCREENRESULTS));
	}
}
