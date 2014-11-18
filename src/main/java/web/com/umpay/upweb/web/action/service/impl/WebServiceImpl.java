package com.umpay.upweb.web.action.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.HTTPUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.web.action.service.WebService;

public class WebServiceImpl implements WebService {
	
	private static final String APP = "ldysyx";
	private static final String KEY = "38b9d080e00bd62345562fee8121239d";
	
	private Logger log = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> querypurchase(Map<String, Object> paramMap) {
		Map<String, String> rtnMap = new HashMap<String, String>();
		String sessionkey = getSessionkey();
		Map<String, String> requestMap = new HashMap<String, String>();
		
		String method = "querypurchase";
		String orderId = ObjectUtil.trim(paramMap.get(HFBusiDict.BANKTRACE)).toString();
		requestMap.put("app", APP);
		requestMap.put("method", method);
		requestMap.put("orderid", orderId);
		String time = String.valueOf(System.currentTimeMillis()/1000);
		requestMap.put("time", time);
		requestMap.put("sessionkey", sessionkey);

		StringBuilder sb = new StringBuilder();
		
		sb.append("app=").append(APP);
		sb.append("&method=").append(method);
		sb.append("&orderid=").append(orderId);
		sb.append("&time=").append(time);
		sb.append("&sessionkey=").append(sessionkey);
		sb.append("&key=").append(KEY);
		
		requestMap.put("hash", DigestUtils.md5Hex(sb.toString()));
		requestMap.put("format", "json");
		
		String url = "http://g.10086.cn/pay/open/index";
		
		Object rtn = (Object) HTTPUtil.get(url, requestMap);
		ObjectUtil.logInfo(log, "游戏基地查单接口返回值：%s", rtn);
		if(rtn != null){
			Map<String, String> map = JSONObject.parseObject(rtn.toString(), Map.class);
			String retCode = String.valueOf(map.get("resultCode"));
			if("2000".equals(retCode)){
				retCode = Constants.RETCODE_SUCCESS;
			}
			else{
				retCode = Constants.RETCODE_GMCXORD_ERROR;
			}
			rtnMap.put(HFBusiDict.RETCODE, retCode);
			rtnMap.put(HFBusiDict.RETMSG, map.get("resultMsg"));
			
			return rtnMap;
		}
		return rtnMap;
	}

	@SuppressWarnings("unchecked")
	private String getSessionkey(){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("app", APP);
		map.put("method", "getsessionkey");
		
		String time = String.valueOf(System.currentTimeMillis()/1000);
		map.put("time", time);
		
//		md5(app=$s&method=$s&time=$s&key=$s)参数值为原始值非加密 其中key取值由我方分配
		StringBuilder sb = new StringBuilder();
		sb.append("app=").append(APP);
		sb.append("&method=").append("getsessionkey");
		sb.append("&time=").append(time);
		sb.append("&key=").append(KEY);
		
		
		map.put("hash", DigestUtils.md5Hex(sb.toString()));
		
		// 一定要小写的json   因为大写JSON的话会当作XML处理
		map.put("format", "json");
		
		String url = "http://g.10086.cn/pay/open/index";
		
		Object rtn = (Object) HTTPUtil.get(url, map);
		ObjectUtil.logInfo(log, "请求sessionKey返回：%s", rtn);
		if(rtn != null){
			Map<String, String> rtnMap = JSONObject.parseObject(rtn.toString(), Map.class);
			return rtnMap.get("sessionkey");
		}
		return "";
	}
}
