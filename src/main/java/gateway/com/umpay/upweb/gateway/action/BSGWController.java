package com.umpay.upweb.gateway.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs2.core.ext.Service4QObj;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.SessionThreadLocal;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

@Controller
@RequestMapping("/gw")
public class BSGWController extends BaseAction {
	
	@Resource(name = "gwNotifyQueue")
	private Service4QObj gwNotifyQueue;
	
	@RequestMapping("/bsresult")
	public void notice(HttpServletResponse response) throws Exception{
		super.preHandle();
		// logger.info("notice接口接收参数 order_id、account_id、create_time、status、sign");
		RequestMsg requestMsp = super.getRequestMsg();
		
		Map<String, String> qMap = new HashMap<String, String>();
		
		// ip + 参数校验 + 验签
		MpspMessage checkResponse = super.doIPSignCheck(requestMsp);
		// 如果验签不通过
		if(checkResponse.isRetCode0000()){
			qMap.put(HFBusiDict.TRANSEQ, requestMsp.getStr("order_id"));
			
			String status = requestMsp.getStr("status");
			if("0".equals(status)){
				status = Constants.RETCODE_SUCCESS;
			}
			qMap.put(HFBusiDict.PAYRETCODE, status);
			qMap.put(HFBusiDict.BUSINESSTYPE, Constants.BUSINESSTYPE_BS); // 博升的
			qMap.putAll(super.restService.format2StringMap(requestMsp.getWrappedMap()));
			// 放进队列 
			qMap.put(HFBusiDict.RPID, SessionThreadLocal.getRpid());
			qMap.put(HFBusiDict.FUNCODE, getFunCode().getFunCode());
			qMap.put(Constants.CLIENTIP, SessionThreadLocal.getSessionValue(Constants.CLIENTIP));
			
			this.gwNotifyQueue.putJob(qMap);
		}
		
		super.doResponse(new HashMap<String, Object>(), response);
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.GWBSNOTIFY;
	}

}
