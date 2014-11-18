package com.umpay.upweb.system.action;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.MessageSource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bs2.core.ext.Service4QObj;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.SpringContextUtil;

/** 
 * 注册系统配置文件
 */
public class UPWebContextListener extends ContextLoaderListener {
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		ServletContext context = event.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		
		MessageUtil.setMessageSource((MessageSource)ctx.getBean("messageSource"));
		MessageUtil.setSysconfSource((MessageSource)ctx.getBean("sysconfSource"));
		MessageUtil.setValidateSource((MessageSource)ctx.getBean("validateSource"));
		MessageUtil.setSignSource((MessageSource)ctx.getBean("signSource"));
		
		Service4QObj gwNotifyQueue = (Service4QObj) ctx.getBean("gwNotifyQueue");
		gwNotifyQueue.start();
		
		SpringContextUtil.setApplicationContext(ctx);
		
	}

}
 