package com.umpay.upweb.system.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.bs.mpsp.util.StringUtil;
import com.umpay.loadstrategy.base.LoadStrategyInf;
  

/** ******************  类说明  ******************
 * class       :  JedisUtil
 * date        :  2014-6-18 
 * @author     :  LiuJiLong
 * @version    :  V1.0  
 * description :  JEDIS工具类,维护多个REDIS读写池
 * @see        :                         
 * ***********************************************/
public class JedisUtil {
	static LoadStrategyInf hfLoadStrategy4redisReader = (LoadStrategyInf) SpringContextUtil.getBean("hfLoadStrategy4redisReader");
	static LoadStrategyInf hfLoadStrategy4redisWriter = (LoadStrategyInf) SpringContextUtil.getBean("hfLoadStrategy4redisWriter");;
	static final String sysPre = "upwebbusi:";
	
	protected static Logger logger = LoggerFactory.getLogger(JedisUtil.class);  
	
	private static Map<String, JedisPool> maps = new HashMap<String, JedisPool>();
    
    /** 
     * 获取连接池. 
     * @return 连接池实例 
     */  
    private static JedisPool getPool(String ip,int port) {  
        String key = ip+":" +port;  
        JedisPool pool = null;  
        if(!maps.containsKey(key)) {  
            JedisPoolConfig config = new JedisPoolConfig();  
          	config.setMaxActive(10);
            config.setMaxIdle(1000);  
            config.setMaxWait(1000);
            config.setTestOnBorrow(true);  
            config.setTestOnReturn(true);  
            
            try{
                /**
                 * JedisPool默认的超时时间是2秒(单位毫秒) 
                 */
            	
            	String pass = "";//StringUtil.trim(MessageUtil.getLocalProperty(sysconfSource, "redisPass");
                pool = new JedisPool(config, ip, port, 2000, (pass==null||"".equals(pass))?"umpayredis":pass);  
                maps.put(key, pool);  
            } catch(Exception e) {  
                e.printStackTrace();  
            }  
        }else{  
            pool = maps.get(key);  
        }  
        return pool;  
    }  
      
    /** 
     * 获取Redis实例. 
     * @return Redis工具类实例 
     */  
    public static Jedis getJedis(String ip,int port) {  
        Jedis jedis  = null;  
        int count =0;  
        do{  
            try{   
                jedis = getPool(ip,port).getResource();
                //log.info("get redis master1!");  
            } catch (Exception e) {  
            	logger.error("get redis failed!" + ip + "|" + port, e);  
                 // 销毁对象    
                getPool(ip,port).returnBrokenResource(jedis);    
            }  
            count++;  
        }while(jedis==null&&count<3);  
        return jedis;  
    }  
  
    /** 
     * 释放redis实例到连接池. 
     * @param jedis redis实例 
     */  
    public static void closeJedis(Jedis jedis,String ip,int port) {  
        if(jedis != null) {
            getPool(ip,port).returnResource(jedis);  
        }  
    } 
    
    /** *****************  方法说明  *****************
	 * method name   :  getReadJedis
	 * @param		 :  @return
	 * @return		 :  Jedis
	 * @author       :  LiuJiLong 2014-6-18 上午11:10:43
	 * description   :  获取读连接
	 * @see          :  
	 * ***********************************************/
	private static Jedis getReadJedis(String ipPortStr){
		String ip = ipPortStr.split(":")[0];
		String port = ipPortStr.split(":")[1];
		return JedisUtil.getJedis(ip, Integer.valueOf(port));
	}
	
	/** *****************  方法说明  *****************
	 * method name   :  getWriteJedis
	 * @param		 :  @return
	 * @return		 :  Jedis
	 * @author       :  LiuJiLong 2014-6-18 上午11:12:32
	 * description   :  获取写连接
	 * @see          :  
	 * ***********************************************/
	private static Jedis getWriteJedis(String ipPortStr){
		String ip = ipPortStr.split(":")[0];
		String port = ipPortStr.split(":")[1];
		return JedisUtil.getJedis(ip, Integer.valueOf(port));
	}
	
	/** *****************  方法说明  *****************
	 * method name   :  finishRead
	 * @param		 :  @param js
	 * @param		 :  @param retCode
	 * @return		 :  void
	 * @author       :  LiuJiLong 2014-6-18 上午11:13:02
	 * description   :  结束读访问
	 * @see          :  
	 * ***********************************************/
	private static void finishRead(Jedis js, String retCode, String ipPortStr){
		hfLoadStrategy4redisReader.finish(ipPortStr, retCode);
		if(js==null){
			logger.info("JS获取为空", "");
		}
		String ip = js.getClient().getHost();
		int port = js.getClient().getPort();
		closeJedis(js, ip, port);
	}
	
	/** *****************  方法说明  *****************
	 * method name   :  finishWrite
	 * @param		 :  @param js
	 * @param		 :  @param retCode
	 * @return		 :  void
	 * @author       :  LiuJiLong 2014-6-18 上午11:13:12
	 * description   :  结束写访问
	 * @see          :  
	 * ***********************************************/
	private static void finishWrite(Jedis js, String retCode, String ipPortStr){
		hfLoadStrategy4redisWriter.finish(ipPortStr, retCode);
		if(js==null){
			logger.info("JS获取为空", "");
			return;
		}
		String ip = js.getClient().getHost();
		int port = js.getClient().getPort();
		closeJedis(js, ip, port);
	}
	
	
	/**
	 * @param mobileid
	 * @return
	 */
	public static void cacheString2Redis(String key, String obj){
		Jedis js = null;
		String ipPortStr = hfLoadStrategy4redisReader.lookup();
		boolean flag = true;
		try {
			js = getWriteJedis(ipPortStr);
        	String cachetime = MessageUtil.getSysconf("redis.mobileid.cachetime");//缓存时间
        	
        	js.set(sysPre + key, obj);//ISO编码使字节按单字节转化为字符
        	
        	// 是否过期
        	js.expire(key, Integer.parseInt(cachetime));
			logger.debug("REDIS存储, KEY:" + key);
		}catch(Exception e){
			flag = false;
			e.printStackTrace();
		}finally{
			try {
				if (flag) {// 是否正常返回
					finishWrite(js, "0000", ipPortStr);
				} else {
					finishWrite(js, "9999", ipPortStr);
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * @param <T>
	 * @param key 缓存时的key
	 * @return
	 */
	public static String readString2Redis(String key){
		Jedis js = null;
		boolean flag = true;
		String ipPortStr = hfLoadStrategy4redisReader.lookup();
		try {
			js = getReadJedis(ipPortStr);
			/*
			 * 取出缓存字符串
			 */
			String objStr = StringUtil.trim(js.get(sysPre + key));
			logger.debug("REDIS取缓存, KEY:" + key + ";OBJ:" + objStr);
			return objStr;
		}catch(Exception e){
			flag = false;
			e.printStackTrace();
		}finally{
			try {
				if (flag) {// 是否正常返回
					finishRead(js, "0000", ipPortStr);
				} else {
					finishRead(js, "9999", ipPortStr);
				}
			} catch (Exception e) {
			}
		}
		return "";
	}
	
	
	/**
	 * @param mobileid
	 * @return
	 */
	public static void cacheObj2Redis(String key, Object obj){
		Jedis js = null;
		String ipPortStr = hfLoadStrategy4redisReader.lookup();
		boolean flag = true;
		ObjectOutputStream oos = null;
		try {
			js = getWriteJedis(ipPortStr);
        	String cachetime = "200";//StringUtil.trim(SpringPropertiesUtil.getProproperty("cachetime", "200"));//缓存时间
        	
        	/*
        	 * 将对象序列化为字节数组
        	 */
        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	oos = new ObjectOutputStream(bos);
        	oos.writeObject(obj);
        	oos.flush();
        	byte[] cacheBytes = bos.toByteArray();//1.5不支持getBytes,暂时采用兼容JDK1.5,1.6的写法
        	
        	/*
        	 * 将字节数组按单字节转化为字符，存储在REDIS中
        	 * 从而保证反向转化存储字符的字节数组与原字节数组一致
        	 */
        	js.set(key, new String(cacheBytes, "ISO-8859-1"));//ISO编码使字节按单字节转化为字符
        	js.expire(key, Integer.parseInt(cachetime));
			logger.debug("REDIS存储, KEY:" + key);
		}catch(Exception e){
			flag = false;
			e.printStackTrace();
		}finally{
			try {
				if (flag) {// 是否正常返回
					finishWrite(js, "0000", ipPortStr);
				} else {
					finishWrite(js, "9999", ipPortStr);
				}
			} catch (Exception e) {
			}
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param <T>
	 * @param mobileid
	 * @return
	 */
	public static <T> T readObj2Redis(String key){
		T obj = null;
		Jedis js = null;
		boolean flag = true;
		ObjectInputStream ois = null;
		String ipPortStr = hfLoadStrategy4redisReader.lookup();
		try {
			js = getReadJedis(ipPortStr);
			
        	/*
        	 * 取出缓存字符串
        	 */
        	String objStr = js.get(key);
        	
        	/*
        	 * 将字节数组反序列化为对象
        	 */
        	ByteArrayInputStream bis = new ByteArrayInputStream(objStr.getBytes("ISO-8859-1"));
        	ois = new ObjectInputStream(bis);
        	
        	obj = (T) ois.readObject();
        	logger.debug("REDIS取缓存, KEY:" + key + ";OBJ:" + obj);
		}catch(Exception e){
			flag = false;
			e.printStackTrace();
		}finally{
			try {
				if (flag) {// 是否正常返回
					finishRead(js, "0000", ipPortStr);
				} else {
					finishRead(js, "9999", ipPortStr);
				}
			} catch (Exception e) {
			}
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}
}
