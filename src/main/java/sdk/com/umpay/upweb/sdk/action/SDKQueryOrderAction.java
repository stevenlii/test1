package com.umpay.upweb.sdk.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

@Controller
@RequestMapping("/sdk")
public class SDKQueryOrderAction extends BaseAction {

	@RequestMapping("/queryOrder")
	public void sdkQueryOrder(HttpServletResponse response) throws Exception{
		super.preHandle();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		
		RequestMsg requestMsg = super.getRequestMsg();
		
		// ip + 参数校验 + 验签
		MpspMessage checkResponse = super.doIPSignCheck(requestMsg);
		if(!checkResponse.isRetCode0000()){
			responseMap.put(HFBusiDict.RETCODE, checkResponse.getRetCode());
			responseMap.put(HFBusiDict.RETMSG, MessageUtil.getMessage(checkResponse.getRetCode()));
			
			requestMsg.setRetCode(checkResponse.getRetCode());
			super.mpspLog(requestMsg);
			
			// 返回sdk
			super.doResponse(responseMap, response);
			return;
		}
		
		MpspMessage restRtnMap = this.restService.sdkQueryOrder(requestMsg);
		responseMap.putAll(requestMsg.getWrappedMap());
		responseMap.putAll(restRtnMap.getWrappedMap());
		
		super.mpspLog(new MpspMessage(responseMap));
		responseMap.put(HFBusiDict.RETMSG, MessageUtil.getMessage(ObjectUtil.trim(responseMap.get(HFBusiDict.RETCODE))));
		super.doResponse(responseMap, response);
	}
	@Override
	public FunCode getFunCode() {
		return FunCode.SDKQUERYORD;
	}

}
