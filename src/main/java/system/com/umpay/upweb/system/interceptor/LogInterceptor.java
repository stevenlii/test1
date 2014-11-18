package com.umpay.upweb.system.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.SessionThreadLocal;

/**
 * 日志记录拦截器
 * <p>创建日期：2013-1-15</p>
 * 
 * @version V1.0
 * @author jxd
 * @see
 */
public class LogInterceptor extends HandlerInterceptorAdapter {
	private static final Logger _log = Logger.getLogger(LogInterceptor.class);
	private ThreadLocal<Long> time_ThreadLocal = new ThreadLocal<Long>() ;//业务处理用时毫秒数
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		long start = System.currentTimeMillis();
		SessionThreadLocal.setSessionValue("start", Long.valueOf(start).toString());
		SessionThreadLocal.setSessionValue("startTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(start)));
		SessionThreadLocal.setSessionValue(Constants.CLIENTIP, new RemoteWrapperRequest(request, "X-Real-IP").getRemoteAddr());
//		SessionThreadLocal.setSessionValue(HFBusiDict.RPID, Generate.getRpid());
		
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		long start = Long.valueOf(SessionThreadLocal.getSessionValue("start")).longValue();
		
		SessionThreadLocal.setSessionValue("endTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		
		SessionThreadLocal.setSessionValue("useTime", Long.valueOf(System.currentTimeMillis() - start).toString());
	}
	

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		try {
			long time = Long.valueOf(SessionThreadLocal.getSessionValue("useTime")).longValue();
			if(time > 120 * 1000){//处理时间如果大于2分钟，说明执行了preHandle方法，但没执行postHandle方法，则结束时间为设置为当前时间
				time = System.currentTimeMillis() - time;
			}
			
			_log.info(String.format("请求IP:%s, 请求功能:%s, 开始时间%s, 结束时间:%s, 处理用时:%s毫秒", SessionThreadLocal.getSessionValue(Constants.CLIENTIP), request.getServletPath(), SessionThreadLocal.getSessionValue("startTime"), SessionThreadLocal.getSessionValue("endTime"), time));
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			time_ThreadLocal.remove();
		}
	}

}
