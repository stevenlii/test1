package com.umpay.upweb.gateway.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thoughtworks.xstream.XStream;
import com.umpay.upweb.gateway.vo.GMIDORequest;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.ObjectUtil;

@Controller
@RequestMapping("/gw")
public class GMIDOResultAction extends BaseAction{
	
	@RequestMapping("/gmidoresult")
	public void idoResult(HttpServletRequest request, HttpServletResponse response){
		super.preHandle();
		XStream xstream = Constants.getXStreamInstance();
		String hRet = "";
		try {
			byte[] bytes = getDataFromInputStream(request);
			String inputXML = new String(bytes, Constants.CHART_UTF8);
			ObjectUtil.logInfo(logger, "游戏基地ido业务结果通知接口请求报文：%s", inputXML);
			
			xstream.alias("request", GMIDORequest.class);
			
			GMIDORequest idoRequest = (GMIDORequest) xstream.fromXML(inputXML);
			hRet = idoRequest.gethRet();
			System.out.println(idoRequest.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		String hRet = idoRequest.gethRet();
		String message = "ok";
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<hRet>");
		buffer.append(hRet);
		buffer.append("</hRet>");
		buffer.append("<message>");
		buffer.append(message);
		buffer.append("</message>");
		
		try {
			response.getOutputStream().write(buffer.toString().getBytes(Constants.CHART_UTF8));
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.GMIDORESULT;
	}
}
