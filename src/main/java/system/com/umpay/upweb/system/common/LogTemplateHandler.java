/**
 * filename   :   LogTemplateHandler.java
 * owner      :   zhaowei
 * copyright  :   UMPAY
 * description:
 * modified   :   2009-7-6
 */
package com.umpay.upweb.system.common;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.bs.mpsp.util.StringUtil;


/*
 * class       :  LogTemplateHandler.java
 * @author     :  zhaowei
 * @version    :  1.0  
 * description :  日志模板生产类
 * @see        :                        
 */

public class LogTemplateHandler {
	
	private Logger log = Logger.getLogger(getClass());
	private String temp;

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String createLog(Map<String, Object> mp) {
		StringTokenizer token = new StringTokenizer(temp, ",");
		StringBuilder buffer = new StringBuilder();
		while (token.hasMoreTokens()) {
			String str = ObjectUtil.trim(token.nextToken());
			try {
				
				String configKey = Constants.hfBusiDictMap.get(str); // 真实的参数key
				
				// 如果字典里不存在
				String log = "";
				if(configKey != null){
					log = ObjectUtil.trim(mp.get(configKey));
				}
				
				buffer.append(log).append(",");
			} catch (Exception e) {
				ObjectUtil.logError(log, "记录简要日志%s出现异常 : %s", e, str, e.getMessage());
				
				buffer.append("").append(",");
			}
		}
		buffer.append(StringUtil.trim(SessionThreadLocal.getSessionValue(Constants.CLIENTIP))).append(",");
		return buffer.toString();
	}
}
