package cn.fywspring.spdierdemo.china10086.dao;



import java.sql.Connection;
import java.sql.Statement;

import cn.fywspring.spdierdemo.china10086.module.GPRS;
import cn.fywspring.spdierdemo.china10086.utils.DBUtils;
import cn.fywspring.spdierdemo.china10086.utils.JedisUtils;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

public class SaveDB {

	 public static void insertIntoDB(GPRS gprs) {
		 // 插入数据库
		 Connection conn = null;
		 Statement st = null;
		try {
			
			String sql = "INSERT INTO gprs VALUES ('"+gprs.getGprs_time()+"','"+gprs.getGprs_addr()+"','"+gprs.getGprs_way()+"','"+gprs.getGprs_long()+"','"+gprs.getGprs_sum()+"','"+gprs.getGprs_tcyh()+"','"+gprs.getGprs_fee()+"')";
			conn = DBUtils.getConnection();
			st = conn.createStatement();
			
			System.out.println(gprs.getGprs_time()+"---"+gprs.getGprs_addr()+"---"+gprs.getGprs_way()+"---"+gprs.getGprs_long()+"---"+gprs.getGprs_sum()+""+gprs.getGprs_tcyh()+"---"+gprs.getGprs_fee());
			st.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeConn(conn);
		}
	}
	 
	public static void saveFromRedis2DB() {
		
			
		Jedis jedis = JedisUtils.getJedis();
		while (true) {
			String json = jedis.lpop("cmcc_gprs");
			System.out.println(json);
			if (!"nil".equals(json)) {
				GPRS gprs = (GPRS) JSONObject.toBean(JSONObject.fromObject(json), GPRS.class);
				System.out.println(gprs);
				if (gprs != null) {
					insertIntoDB(gprs);
				}
			} else {
				break;
			}
		}
		JedisUtils.returnResource(jedis);
		
	}
	public static void main(String[] args) {
		saveFromRedis2DB();
	}

}
