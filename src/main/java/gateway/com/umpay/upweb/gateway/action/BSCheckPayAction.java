package com.umpay.upweb.gateway.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

@Controller
@RequestMapping("/gw")
public class BSCheckPayAction extends BaseAction {

	@RequestMapping("/bscheck")
	public void bsCheck(HttpServletResponse response) throws Exception{
		super.preHandle();
		logger.info("/gw/bsCheck.do");
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		
		RequestMsg requestMsg = super.getRequestMsg();
		
		String code = "00";
		String msg = "同意";
		
		// ip + 参数校验 + 验签
		MpspMessage checkResponse = super.doIPSignCheck(requestMsg);
		// 如果验签不通过
		if(!checkResponse.isRetCode0000()){
			code = "01";
			msg = "不同意";
		}
		
		responseMap.put("code", code);
		responseMap.put("msg", msg);
		responseMap.put("data", "{}");
		
//		logger.info("bsatinfo.check" + responseMap);
		ObjectUtil.logInfo(logger, " response data : %s", responseMap);
		
		super.doResponse(responseMap, response);
	}
	
	
	@Override
	public FunCode getFunCode() {
		return FunCode.GWBSCHECK;
	}
}
