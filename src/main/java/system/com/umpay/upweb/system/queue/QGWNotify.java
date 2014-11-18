package com.umpay.upweb.system.queue;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bs.mpsp.util.StringUtil;
import com.bs2.inf.Datalet2Inf;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.LogTemplateHandler;
import com.umpay.upweb.system.common.LoggerManager;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.common.SessionThreadLocal;
import com.umpay.upweb.system.common.SpringContextUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.service.RestService;
import com.umpay.upweb.web.action.service.WebService;

public class QGWNotify implements Datalet2Inf {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	/**
	 * @param paramObject Map形式
	 */
	@Override
	public void onData(Object paramObject) {

		long beginTime = System.currentTimeMillis();
		// 发送到Rest
		@SuppressWarnings("unchecked")
		Map<String, String> qMap = (Map<String, String>) paramObject;
		
		if(qMap != null){
//			SessionThreadLocal.setSessionValue(HFBusiDict.FUNCODE, qMap.get(HFBusiDict.FUNCODE));
			//TODO 确认支付通知的funcode
			SessionThreadLocal.setSessionValue(HFBusiDict.FUNCODE, "GWNOTIFY");
			SessionThreadLocal.setSessionValue(HFBusiDict.RPID, qMap.get(HFBusiDict.RPID));
			SessionThreadLocal.setSessionValue(Constants.CLIENTIP, qMap.get(Constants.CLIENTIP));
			
			ObjectUtil.logInfo(logger, "出支付结果通知队列： %s", qMap.toString());
			
			// 如果是游戏基地的通知并且是支付成功的单子   就 先去调用游戏基地的查单接口   如果支付不成功  就不需要查单了
			String businesstype = StringUtil.trim(qMap.get(HFBusiDict.BUSINESSTYPE));
			if(Constants.BUSINESSTYPE_GM_WEB_YZM.equals(businesstype) && Constants.RETCODE_SUCCESS.equals(qMap.get(HFBusiDict.PAYRETCODE))){
				WebService webService = (WebService) SpringContextUtil.getApplicationContext().getBean("webService");
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put(HFBusiDict.BANKTRACE, StringUtil.trim(qMap.get(HFBusiDict.BANKTRACE)));
				
				Map<String, String> queryMap = webService.querypurchase(paramMap);
				qMap.putAll(queryMap);
			}
			
			RestService restService = (RestService) SpringContextUtil.getApplicationContext().getBean("restService");
			MpspMessage mpspMsg = restService.payNotifyRest(qMap);
			
			// 不能把retCode覆盖掉
			qMap.remove(HFBusiDict.RETCODE);
			qMap.remove(HFBusiDict.RETMSG);
			mpspMsg.getWrappedMap().putAll(qMap);
			
			long useTime = System.currentTimeMillis() - beginTime;
			
			StringBuffer logBuffer = new StringBuffer(((LogTemplateHandler) SpringContextUtil.getApplicationContext().getBean("logTemplateHandler")).createLog(mpspMsg.getWrappedMap()));
			logBuffer.append(useTime);
			
			LoggerManager.getMpspLogger().info(logBuffer.toString());
			
			// qMap中已经没有retCode和retMsg了  如果后续还再使用qMap 需要加进入 fanxiangchi 20141103
		}
	}
}
