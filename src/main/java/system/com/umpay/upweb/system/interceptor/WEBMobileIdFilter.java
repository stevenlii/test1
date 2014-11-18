package com.umpay.upweb.system.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;

public class WEBMobileIdFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		
		// 获取手机号
		String mobileid = ObjectUtil.trim(request.getParameter(HFBusiDict.MOBILEID));
		
		String retCode = Constants.RETCODE_SUCCESS;
		// 如果手机号为空
		if(mobileid.length() == 0){
			retCode = Constants.RETCODE_WEBORD_NOMOBILEID;
		}
		else{
			// 如果手机号填写规则不正确
			String regex = MessageUtil.getValidate("CheckReq.MOBILEID");
			if(!mobileid.matches(regex)){
				// 转发到输入手机号的页面
				retCode = Constants.RETCODE_WEBORD_ERRMOBILEID;
			}
		}
		
		if(!Constants.RETCODE_SUCCESS.equals(retCode)){
			request.getRequestDispatcher("/web/noMobileId.do").forward(servletRequest, servletResponse);
		}
		else{
			chain.doFilter(servletRequest, servletResponse);
		}
	}
	
	@Override
	public void init(FilterConfig config) throws ServletException {

	}
}
