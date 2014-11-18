package com.umpay.upweb.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.bs.mpsp.util.StringUtil;
import com.bs2.core.ext.Service4QObj;
import com.bs3.ext.bs2.Base64;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.common.SessionThreadLocal;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

@Controller
@RequestMapping("/web")
public class WEBConfirmPayAction extends BaseAction {
	
	@Resource(name = "gwNotifyQueue")
	private Service4QObj gwNotifyQueue;
	
	@RequestMapping("/confirmPay")
	public String confirmPay(String model, String verifycode, HttpServletResponse response, ModelMap modelMap){
		super.preHandle();
		modelMap.put(Constants.KEFU_ONLINE_URL, MessageUtil.getSysconf("KEFU.ONLINE.URL"));
		modelMap.put(Constants.KEFU_ONLINE_STYLE, MessageUtil.getSysconf("KEFU.ONLINE.STYLE"));
		RequestMsg requestMsg = super.getRequestMsg();

		Map<String, Object> requestMap = JSONObject.parseObject(Base64.decode(model), Map.class);
		requestMap.put(HFBusiDict.VERIFYCODE, verifycode);
		requestMsg.getWrappedMap().putAll(requestMap);
		
		ObjectUtil.logInfo(logger, "请求参数：%s", requestMap.toString());
		
		// 参数校验
		MpspMessage checkMsg = super.doIPSignCheck(requestMsg);
		requestMsg.putAll(checkMsg);
		if(!checkMsg.isRetCode0000()){
			requestMap.put(HFBusiDict.RETCODE, checkMsg.getRetCode());
			requestMap.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(checkMsg.getRetCode()));
			requestMsg.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
			modelMap.putAll(requestMsg.getWrappedMap());
			super.mpspLog(requestMsg);
			if(super.isPhone(getHttpServletRequest())){
				return "phone/error";
			}
			return "error";
		}
				
		MpspMessage restMsg = (MpspMessage) super.restService.confirmRestPay(requestMsg);
		requestMsg.putAll(restMsg);
		
		// 有的通道没有支付结果通知接口 需要直接放入队列
		String businesstype = StringUtil.trim(requestMsg.getStr(HFBusiDict.BUSINESSTYPE));
		
		// 游戏基地
		if(Constants.BUSINESSTYPE_GM_WEB_YZM.equals(businesstype)){
			// 将支付结果加入队列
			Map<String, String> qMap = new HashMap<String, String>();
			
			qMap.put(HFBusiDict.TRANSEQ, StringUtil.trim(requestMsg.getStr(HFBusiDict.TRANSEQ)));
			qMap.put(HFBusiDict.BUSINESSTYPE, StringUtil.trim(requestMsg.getStr(HFBusiDict.BUSINESSTYPE)));
			qMap.put(HFBusiDict.PAYRETCODE, restMsg.getRetCode());
			qMap.putAll(super.restService.format2StringMap(requestMsg.getWrappedMap()));
			qMap.put(HFBusiDict.RPID, SessionThreadLocal.getRpid());
			qMap.put(HFBusiDict.FUNCODE, getFunCode().getFunCode());
			qMap.put(Constants.CLIENTIP, SessionThreadLocal.getSessionValue(Constants.CLIENTIP));

			// 放进队列 
			this.gwNotifyQueue.putJob(qMap);
		}
		
		modelMap.putAll(requestMsg.getWrappedMap());
		modelMap.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
		super.mpspLog(requestMsg);
		
		modelMap.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(restMsg.getRetCode()));
		if(!restMsg.isRetCode0000()){
			if(super.isPhone(getHttpServletRequest())){
				return "phone/error";
			}
			return "error";
		}
		
		if(super.isPhone(getHttpServletRequest())){
			return "phone/result";
		}
		return "result";
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.WEBCONFIRMPAY;
	}

}
