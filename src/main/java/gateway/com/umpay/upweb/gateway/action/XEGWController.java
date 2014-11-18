package com.umpay.upweb.gateway.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs.mpsp.util.StringUtil;
import com.bs2.core.ext.Service4QObj;
import com.bs3.ext.bs2.Base64;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.common.SessionThreadLocal;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

/***
 * 接收小额支付结果通知接口
 * @author fanxiangchi
 *
 */
@Controller
@RequestMapping("/gw")
public class XEGWController extends BaseAction {
	
	@Resource(name = "gwNotifyQueue")
	private Service4QObj gwNotifyQueue;
	
	@RequestMapping("xeresult")
	public void notice(HttpServletResponse response) throws Exception{
		super.preHandle();
		/**
		 * 从话费商户前置拿到的通知参数
		 * 
		 *  String param = "merId="+merId;    
			    param += "&goodsId="+goodsId;    
			    param += "&orderId="+orderId;   
			    param += "&merDate="+orderDate;         
			    param += "&payDate="+reqDate;   
			    param += "&amount="+amt;   
			    param += "&amtType=02";   
			    param += "&bankType=3"; 
			    param += "&mobileId="+calling; 
			    param += "&transType="+transType;
			    param += "&settleDate="+settleDate;
			    param += "&merPriv="+merPriv;
			    param += "&retCode="+merRetCode;
			    param += "&version=3.0";
		 */
		
		RequestMsg requestMsp = super.getRequestMsg();
		
		Map<String, String> qMap = new HashMap<String, String>();
		
		qMap.put(HFBusiDict.TRANSEQ, requestMsp.getStr("orderId"));
		qMap.put(HFBusiDict.MOBILEID, requestMsp.getStr("mobileId"));
		qMap.put(HFBusiDict.MERPRIV, requestMsp.getStr("merPriv"));
		qMap.put(HFBusiDict.ORDERDATE, requestMsp.getStr("merDate"));
		qMap.put(HFBusiDict.PAYDATE, requestMsp.getStr("payDate"));
		qMap.put(HFBusiDict.TRANSTYPE, requestMsp.getStr("transType"));
		qMap.put(HFBusiDict.MERID, requestMsp.getStr("merId"));
		qMap.put(HFBusiDict.GOODSID, requestMsp.getStr("goodsId"));
		qMap.put(HFBusiDict.SETTLEDATE, requestMsp.getStr("settleDate"));
		
		// 其实这个接口是拿不到businesstype这个值的   目前写死R4流程
		String businesstype = StringUtil.trim(requestMsp.getStr(HFBusiDict.BUSINESSTYPE));
		if(businesstype.length() == 0){
			businesstype = Constants.BUSINESSTYPE_XE_SDK_YZM;
		}
		
		qMap.put(HFBusiDict.BUSINESSTYPE, businesstype);
		qMap.put(HFBusiDict.PAYRETCODE, requestMsp.getStr(HFBusiDict.RETCODE));
		qMap.putAll(super.restService.format2StringMap(requestMsp.getWrappedMap()));
		
		
		qMap.put(HFBusiDict.RPID, SessionThreadLocal.getRpid());
		qMap.put(HFBusiDict.FUNCODE, getFunCode().getFunCode());
		qMap.put(Constants.CLIENTIP, SessionThreadLocal.getSessionValue(Constants.CLIENTIP));
		// 放进队列 
		this.gwNotifyQueue.putJob(qMap);
		
		doResponse(requestMsp, response);
	}

	private void doResponse(RequestMsg requestMsp, HttpServletResponse response) throws IOException{
		String merid = StringUtil.trim(requestMsp.getStr("merId"));
		String goodsid = StringUtil.trim(requestMsp.getStr("goodsId"));
		String orderId = StringUtil.trim(requestMsp.getStr("orderId"));
		String merDate = StringUtil.trim(requestMsp.getStr("merDate"));
		String retCode = Constants.RETCODE_SUCCESS;
		
		MpspMessage goodsinfo = super.restService.getGoodsInf(merid, goodsid);
		String retMsg = StringUtil.trim(goodsinfo.getStr(HFBusiDict.GOODSDESC));
		
		String version = Constants.XEVERSION;
		
		StringBuffer buffer = new StringBuffer("<META NAME=\"MobilePayPlatform\" CONTENT=\"");
		buffer.append(merid).append("|");
		buffer.append(goodsid).append("|");
		buffer.append(orderId).append("|");
		buffer.append(merDate).append("|");
		buffer.append(retCode).append("|");
		buffer.append(Base64.encode(retMsg.getBytes(Constants.CHART_GBK))).append("|");
		buffer.append(version).append("|sign\">");
		
		String info2Mer = buffer.toString();
		ObjectUtil.logInfo(logger, "响应小额前置的内容：%s", info2Mer);
		
		response.setCharacterEncoding("GBK");
		response.getWriter().print(info2Mer);
		response.flushBuffer();
	}
	
	@Override
	public FunCode getFunCode() {
		return FunCode.GWXENOTIFY;
	}
}
