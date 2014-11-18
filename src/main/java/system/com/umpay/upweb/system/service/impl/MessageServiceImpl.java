package com.umpay.upweb.system.service.impl;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;

import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.service.MessageService;

public class MessageServiceImpl implements MessageService{
	public static Logger log = Logger.getLogger(MessageServiceImpl.class);
	
	private MessageSource messageSource;
	
	private MessageSource sysconfSource;
	
	
	public String getSystemParam(String key){
		return MessageUtil.getSysconf(key);
	}
	public String getSystemParam(String key,String defaultValue){
		String value = getSystemParam(key);
		if(ObjectUtil.isEmpty(value)){
			value = defaultValue;
		}
		return value;
	}
	
	
	public String getMessage(String key){
		return MessageUtil.getMessage(key);
	}
	
	
	public String getMessageDetail(String key){
		String detail = MessageUtil.getMessage(key + ".detail");
		if(ObjectUtil.isEmpty(detail)){
			detail = MessageUtil.getMessage(key);
		}
		return detail;
	}
	public MessageSource getMessageSource() {
		return messageSource;
	}
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	public MessageSource getSysconfSource() {
		return sysconfSource;
	}
	public void setSysconfSource(MessageSource sysconfSource) {
		this.sysconfSource = sysconfSource;
	}

}
