package com.umpay.upweb.sdk.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs.mpsp.util.StringUtil;
import com.bs2.core.ext.Service4QObj;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.common.SessionThreadLocal;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

/**
 * 博升通道的确认支付
 * @author fanxiangchi
 */
@Controller
@RequestMapping("/sdk")
public class SDKConfirmPayAction extends BaseAction{
	
	@Resource(name = "gwNotifyQueue")
	private Service4QObj gwNotifyQueue;
	
	@RequestMapping("/confirmPay")
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
		// 确认支付
		MpspMessage restRtnMap = this.restService.confirmRestPay(requestMsg);
		
		// 有的通道没有支付结果通知接口 需要直接放入队列
		String businesstype = StringUtil.trim(requestMsg.getStr(HFBusiDict.BUSINESSTYPE));
		
		// 游戏基地
		if(Constants.BUSINESSTYPE_GM_WEB_YZM.equals(businesstype)){
			// 将支付结果加入队列
			Map<String, String> qMap = new HashMap<String, String>();
			
			qMap.put(HFBusiDict.TRANSEQ, StringUtil.trim(requestMsg.getStr(HFBusiDict.TRANSEQ)));
			qMap.put(HFBusiDict.BUSINESSTYPE, StringUtil.trim(requestMsg.getStr(HFBusiDict.BUSINESSTYPE)));
			qMap.put(HFBusiDict.PAYRETCODE, restRtnMap.getRetCode());
			qMap.putAll(super.restService.format2StringMap(requestMsg.getWrappedMap()));
			
			
			qMap.put(HFBusiDict.RPID, SessionThreadLocal.getRpid());
			qMap.put(HFBusiDict.FUNCODE, getFunCode().getFunCode());
			qMap.put(Constants.CLIENTIP, SessionThreadLocal.getSessionValue(Constants.CLIENTIP));

			// 放进队列 
			this.gwNotifyQueue.putJob(qMap);
		}
		
		responseMap.putAll(requestMsg.getWrappedMap());
		responseMap.putAll(restRtnMap.getWrappedMap());
		
		responseMap.put(HFBusiDict.RETMSG, MessageUtil.getMessage(ObjectUtil.trim(responseMap.get(HFBusiDict.RETCODE))));
		super.mpspLog(new MpspMessage(responseMap));
		
		// 返回sdk
		super.doResponse(responseMap, response);
	}


	@Override
	public FunCode getFunCode() {
		return FunCode.SDKCONFIRMPAY;
	}
}
