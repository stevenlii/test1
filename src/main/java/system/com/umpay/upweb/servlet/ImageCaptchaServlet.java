package com.umpay.upweb.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.umpay.upweb.system.common.CaptchaServiceSingleton;

public class ImageCaptchaServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig servletConfig) throws ServletException{

		super.init(servletConfig);

	}

	protected void doGet(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException{

		boolean error = false;
		do {
			try {
	            CaptchaServiceSingleton.getInstance().writeCaptchaImage(httpServletRequest, httpServletResponse);
	            error = false;
	        } catch (Exception e) {
	            error = true;
	        }
		} while(error);
			
		
		
	}
}
