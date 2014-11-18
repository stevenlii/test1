package com.umpay.upweb.system.common;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public class MessageUtil {
	public static Logger log = Logger.getLogger(MessageUtil.class);
	
	private static MessageSource messageSource;
	
	private static MessageSource sysconfSource;
	
	private static MessageSource validateSource;
	
	private static MessageSource signSource;
	
	public static void setMessageSource(MessageSource messageSource) {
		if(messageSource != null){
			MessageUtil.messageSource = messageSource;
		}
	}
	
	public static void setSysconfSource(MessageSource sysconfSource) {
		if(sysconfSource != null){
			MessageUtil.sysconfSource = sysconfSource;
		}
	}
	
	public static void setValidateSource(MessageSource validateSource) {
		if(validateSource != null){
			MessageUtil.validateSource = validateSource;
		}
	}
	
	
	public static void setSignSource(MessageSource signSource) {
		if(signSource != null){
			MessageUtil.signSource = signSource;
		}
	}
	
	
	
	/**
	 * 获得配置信息
	 * 
	 * @param key
	 * @return
	 */
	public static String getMessage(String key) {
		String rtn = "";
		try{
			rtn = ObjectUtil.trim(messageSource.getMessage(key, null, Locale.CHINA));
		}catch(NoSuchMessageException e){
			rtn = "";
			log.debug(e);
		}
		
		log.debug("getMessage() key[" + key + "] localMsg[" + rtn + "]");
		return rtn;
	}
	
	
	public static String getSysconf(String key) {
		String rtn = "";
		try{
			rtn = ObjectUtil.trim(sysconfSource.getMessage(key, null, Locale.CHINA));
		}catch(NoSuchMessageException e){
			rtn = "";
			log.debug(e);
		}
		
		log.debug("getSysconf() key[" + key + "] localMsg[" + rtn + "]");
		return rtn;
	}
	
	public static String getValidate(String key) {
		String rtn = "";
		try{
			rtn = ObjectUtil.trim(validateSource.getMessage(key, null, Locale.CHINA));
		}catch(NoSuchMessageException e){
			rtn = "";
			log.debug(e);
		}
		
		log.debug("getSysconf() key[" + key + "] localMsg[" + rtn + "]");
		return rtn;
	}
	
	public static String getSign(String key) {
		String rtn = "";
		try{
			rtn = ObjectUtil.trim(signSource.getMessage(key, null, Locale.CHINA));
		}catch(NoSuchMessageException e){
			rtn = "";
			log.debug(e);
		}
		
		log.debug("getSign() key[" + key + "] localMsg[" + rtn + "]");
		return rtn;
	}
}
