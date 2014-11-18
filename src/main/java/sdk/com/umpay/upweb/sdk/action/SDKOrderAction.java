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

/**
 * ****************** 类说明 *********************
 * 
 * class : WxOrderAction
 * 
 * @author : lizhiqiang
 * @description : 无线下单
 * @see :
 * @version : 1.0 ******************************************
 */
@Controller
@RequestMapping("/sdk")
public class SDKOrderAction extends BaseAction {

	@RequestMapping(value = "/order")
	public void order(HttpServletResponse response) throws Exception {
		super.preHandle();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		// 获取参数
		RequestMsg requestMsg = super.getRequestMsg();
		
		// 默认充值账号字段
		requestMsg.put(HFBusiDict.ACCOUNTID, Constants.ACCOUNTID);

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
		
		MpspMessage restRtnMap = this.restService.upOrder(requestMsg);

		responseMap.putAll(requestMsg.getWrappedMap());
		responseMap.putAll(restRtnMap.getWrappedMap());
		
		super.mpspLog(new MpspMessage(responseMap));
		
		// 20141011 看请求的参数里面有没有手机号字段 如果没有 就去redis缓存去查  查到了返回给sdk  查不到就当做空
		String mobileid = StringUtil.trim(requestMsg.getStr(HFBusiDict.MOBILEID));
		if(mobileid.length() == 0){
			String imsi = StringUtil.trim(requestMsg.getStr(HFBusiDict.IMSI));
			String imei = StringUtil.trim(requestMsg.getStr(HFBusiDict.IMEI));
			
			String key = imsi + "_" + imei;
			
			//FIXME 设置过期时间
			String redisMobileid = StringUtil.trim(JedisUtil.readString2Redis(key));
			ObjectUtil.logInfo(logger, "从redis取得手机号：key[%s], mobileid[%s]", key, redisMobileid);
			
			responseMap.put(HFBusiDict.MOBILEID, redisMobileid);
		}
		responseMap.put(HFBusiDict.RETMSG, MessageUtil.getMessage(ObjectUtil.trim(responseMap.get(HFBusiDict.RETCODE))));
		// 返回sdk
		super.doResponse(responseMap, response);
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.SDKUPORD;
	}

}