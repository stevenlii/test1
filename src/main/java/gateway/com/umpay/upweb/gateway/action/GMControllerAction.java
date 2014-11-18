package com.umpay.upweb.gateway.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;


@Controller
@RequestMapping("/gw")
public class GMControllerAction extends BaseAction {
	
	@RequestMapping("/gmship")
	public void ship(HttpServletResponse response) throws Exception{
		super.preHandle();
		logger.info("/gw/gmship.do");

		Map<String, Object> responseMap = new HashMap<String, Object>();
		
		RequestMsg requestMsg = super.getRequestMsg();
		
		String code = "0";
		String msg = "Successful";
		
		// ip + 参数校验 + 验签
		MpspMessage checkResponse = super.doIPSignCheck(requestMsg);
		// 如果验签不通过
		if(!checkResponse.isRetCode0000()){
			logger.info("验签不通过!");
			code = "9999";
			msg = "fail";
		}
		
		responseMap.put("hRet", code);
		responseMap.put("message", msg);
		responseMap.put("data", "{}");
		
		super.doResponse(responseMap, response);
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.WEBCONFIRMPAY;
	}

}
