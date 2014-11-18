package com.umpay.upweb.sdk.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs.mpsp.util.StringUtil;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.JedisUtil;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

@Controller
@RequestMapping("/sdk")
public class SDKPayAction extends BaseAction{
	
	@RequestMapping("/pay")
	public void pay(HttpServletResponse response) throws Exception{
		super.preHandle();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		// 获取参数
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
		
		// 请求Rest支付    这一步可能不是真正的支付  目的是做到支付兼容  并且支付前保存交易
		/**
		 * 对于R4： 这步保存交易 返回mo1 mo2
		 * 对于BS：这步保存交易  发送短信  并且返回MM页面的元素值
		 */
		try {
			MpspMessage restRtnMap = this.restService.upRestPay(requestMsg);
			responseMap.putAll(requestMsg.getWrappedMap());
			responseMap.putAll(restRtnMap.getWrappedMap());
		} catch (Exception e) {
			e.printStackTrace();
			ObjectUtil.logError(logger, "sdk支付请求异常。", e, e.getMessage());
			
			responseMap.put(HFBusiDict.RETCODE, Constants.RETCODE_SYSERR);
			responseMap.put(HFBusiDict.RETMSG, "抱歉，支付请求出现异常。");
			// 返回sdk
			super.doResponse(responseMap, response);
			return;
		}
		
		super.mpspLog(new MpspMessage(responseMap));
		
		// 缓存手机号到redis
		String imsi = StringUtil.trim(requestMsg.getStr(HFBusiDict.IMSI));
		String imei = StringUtil.trim(requestMsg.getStr(HFBusiDict.IMEI));
		//FIXME 设置过期时间
		String key = imsi + "_" + imei;
		String mobileid = StringUtil.trim(requestMsg.getStr(HFBusiDict.MOBILEID));
		JedisUtil.cacheString2Redis(key, mobileid);
		
		ObjectUtil.logInfo(logger, "缓存手机号到redis：key[%s], mobileid[%s]", key, mobileid);
		responseMap.put(HFBusiDict.RETMSG, MessageUtil.getMessage(ObjectUtil.trim(responseMap.get(HFBusiDict.RETCODE))));
		// 返回sdk
		super.doResponse(responseMap, response);
	}


	@Override
	public FunCode getFunCode() {
		return FunCode.SDKUPPAY;
	}
}
