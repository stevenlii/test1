package com.umpay.upweb.system.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.bs.mpsp.util.StringUtil;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.Generate;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.common.RestConnPool;
import com.umpay.upweb.system.common.SessionThreadLocal;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;
import com.umpay.upweb.system.service.MessageService;
import com.umpay.upweb.system.service.RestService;

public class RestServiceImpl implements RestService {
	private Logger log = Logger.getLogger(getClass());

	private MessageService messageService;

	public MessageService getMessageService() {
		return messageService;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public RestConnPool getRestConnPool() {
		return restConnPool;
	}

	public void setRestConnPool(RestConnPool restConnPool) {
		this.restConnPool = restConnPool;
	}

	private RestConnPool restConnPool;

	@Override
	public MpspMessage upRestPay(MpspMessage mpspMsg) {
		Map<String, String> requestMap = this.format2StringMap(mpspMsg.getWrappedMap());
		requestMap.put(HFBusiDict.MERCUSTID, mpspMsg.getStr(HFBusiDict.ACCOUNTID));
		requestMap.put(HFBusiDict.ACCOUNTID, mpspMsg.getStr(HFBusiDict.ACCOUNTID));

		String platordid = mpspMsg.getStr(HFBusiDict.PLATORDID);
		String url = this.getRestUrl("UPPAY", platordid, mpspMsg);

		return (MpspMessage) requestUpRestPost(url, requestMap, mpspMsg);
	}


	@Override
	public MpspMessage confirmRestPay(MpspMessage mpspMsg) {
		Map<String, String> requestMap = this.format2StringMap(mpspMsg.getWrappedMap());

		/**
		 * /branch/{businesstype}/{rpid} 各流程的确认支付地址
		 *	CONFIRMPAY.REST.URL = /branch/{0}/{1}.xml
		 */

		String url = this.getRestUrl("CONFIRMPAY", "", mpspMsg);
		return (MpspMessage) requestUpRestPost(url, requestMap, mpspMsg);
	}

	public Map<String, String> format2StringMap(Map<String, Object> objectMap){
		Map<String, String> map = new HashMap<String, String>(objectMap.size());
		
		Set<Entry<String, Object>> entrySet = objectMap.entrySet();
		
		Iterator<Entry<String, Object>> it = entrySet.iterator();
		
		while(it.hasNext()){
			Entry<String, Object> entry = it.next();
			
			String key = StringUtil.trim(entry.getKey());
			String value = ObjectUtil.trim(entry.getValue());
			
			map.put(key, value);
		}
		
		return map;
	}

	@Override
	public MpspMessage payNotifyRest(Map<String, String> qMap) {
		MpspMessage mpspMsg = new MpspMessage();
		
		String url = this.getRestUrl("GWNOTIFY", qMap.get(HFBusiDict.TRANSEQ) + "-" + qMap.get(HFBusiDict.BUSINESSTYPE) + "-" + qMap.get(HFBusiDict.PAYRETCODE), mpspMsg);
		return (MpspMessage) requestUpRestPost(url, qMap, mpspMsg);
	}

	@Override
	public MpspMessage sdkQueryOrder(RequestMsg requestMsg) {
		Map<String, String> restMap = new HashMap<String, String>();

		String platordid = requestMsg.getStr(HFBusiDict.PLATORDID);
		restMap.put(HFBusiDict.PLATORDID, platordid);

		String url = this.getRestUrl("QUERYORDER", platordid, requestMsg);
		
		return (MpspMessage) this.requestUpRestGet(url, restMap, requestMsg);

	}
	@Override
	public MpspMessage merQueryOrder(RequestMsg requestMsg) {
		Map<String, String> restMap = new HashMap<String, String>();

		String orderdate = requestMsg.getStr(HFBusiDict.ORDERDATE);
		restMap.put(HFBusiDict.ORDERDATE, orderdate);
		String orderid = requestMsg.getStr(HFBusiDict.ORDERID);
		restMap.put(HFBusiDict.ORDERID, orderid);
		String merid = requestMsg.getStr(HFBusiDict.MERID);
		restMap.put(HFBusiDict.MERID, merid);
		//id顺序
		String id = orderdate + merid + orderid;
		String url = this.getRestUrl("QUERYUPMER", id, requestMsg);
		
		return (MpspMessage) this.requestUpRestGet(url, restMap, requestMsg);

	}

	@Override
	public MpspMessage upOrder(MpspMessage mpspMsg) {

		Map<String, String> requestMap = this.format2StringMap(mpspMsg.getWrappedMap());

		String id = requestMap.get(HFBusiDict.MERID) + "-" + requestMap.get(HFBusiDict.GOODSID) + "-" + requestMap.get(HFBusiDict.ORDERID);
		String url = this.getRestUrl("UPORDER", id, mpspMsg);

		return (MpspMessage) this.requestUpRestPost(url, requestMap, mpspMsg);
	}

	@Override
	public MpspMessage checkMerSign(MpspMessage mpspMsg) {
		String unsignstring = this.getUnsignstring(mpspMsg);
		
		String signstring = StringUtil.trim(mpspMsg.getStr(HFBusiDict.SIGN));
		ObjectUtil.logInfo(log, "SignCheck signstring %s", signstring);
		//调用资源层商户验签服务
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(HFBusiDict.UNSIGNSTR, unsignstring);
		requestMap.put(HFBusiDict.SIGNSTR, signstring);
		
		String merid = StringUtil.trim(mpspMsg.getStr(HFBusiDict.MERID));
		requestMap.put(HFBusiDict.MERID, merid);
		
		String url = this.getRestUrl("MERSIGN", merid, mpspMsg);
		
		MpspMessage respMessage = (MpspMessage) this.requestUpRestGet(url, requestMap, mpspMsg);
		
		/*
		 * 20131105 liujilong 临时增加一条规则,凡事因与REST连接异常导致的验签失败全都算成功
		 */
		if(respMessage.getRetCode().startsWith(Constants.RETCODE_PREFIX))	{
			respMessage.setRetCode0000();
		}
		
		ObjectUtil.logInfo(log, "SignCheck Result[RetCode]%s:%s",respMessage.getRetCode(), respMessage.getRetMsg());
		return respMessage;
	}
	@Override
	public MpspMessage checkMerSign(MpspMessage mpspMsg, String unsignstring) {
		String signstring = StringUtil.trim(mpspMsg.getStr(HFBusiDict.SIGN));
		ObjectUtil.logInfo(log, "SignCheck signstring %s", signstring);
		//调用资源层商户验签服务
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(HFBusiDict.UNSIGNSTR, unsignstring);
		requestMap.put(HFBusiDict.SIGNSTR, signstring);
		
		String merid = StringUtil.trim(mpspMsg.getStr(HFBusiDict.MERID));
		requestMap.put(HFBusiDict.MERID, merid);
		
		String url = this.getRestUrl("MERSIGN", merid, mpspMsg);
		
		MpspMessage respMessage = (MpspMessage) this.requestUpRestGet(url, requestMap, mpspMsg);
		
		/*
		 * 20131105 liujilong 临时增加一条规则,凡事因与REST连接异常导致的验签失败全都算成功
		 */
		if(respMessage.getRetCode().startsWith(Constants.RETCODE_PREFIX))	{
			respMessage.setRetCode0000();
		}
		
		ObjectUtil.logInfo(log, "SignCheck Result[RetCode]%s:%s",respMessage.getRetCode(), respMessage.getRetMsg());
		return respMessage;
	}
	
	/**
	 * 获取页面下单时，请求信息的明文串 
	 */
	public String getUnsignstring(MpspMessage requestMsg){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(HFBusiDict.MERID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.MERID)));
		buffer.append("&").append(HFBusiDict.GOODSID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.GOODSID)));
		buffer.append("&").append(HFBusiDict.GOODSINFO).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.GOODSINFO)));
		buffer.append("&").append(HFBusiDict.MOBILEID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.MOBILEID)));
//		buffer.append("&").append(HFBusiDict.ACCOUNTID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.ACCOUNTID)));
		buffer.append("&").append(HFBusiDict.ORDERID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERID)));
		buffer.append("&").append(HFBusiDict.ORDERDATE).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERDATE)));
		buffer.append("&").append(HFBusiDict.AMOUNT).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.AMOUNT)));
		buffer.append("&").append(HFBusiDict.AMTTYPE).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.AMTTYPE)));
		buffer.append("&").append(HFBusiDict.BANKTYPE).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.BANKTYPE)));
		buffer.append("&").append(HFBusiDict.PLATTYPE).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.PLATTYPE)));
		buffer.append("&").append(HFBusiDict.GATEID).append("=").append(ObjectUtil.trim(StringUtil.trim(requestMsg.getStr(HFBusiDict.GATEID))));
		buffer.append("&").append(HFBusiDict.RETURL).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.RETURL)));
//		buffer.append("&").append(HFBusiDict.NOTIFYURL).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.NOTIFYURL)));
		buffer.append("&").append(HFBusiDict.MERPRIV).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.MERPRIV)));
		buffer.append("&").append(HFBusiDict.EXPAND).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.EXPAND)));
		buffer.append("&").append(HFBusiDict.CHANNELID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.CHANNELID)));
		buffer.append("&").append(HFBusiDict.APPID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.APPID)));
		buffer.append("&").append(HFBusiDict.UPVERSION).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.UPVERSION)));
		
		String unsignstring = buffer.toString();
		ObjectUtil.logInfo(log, "SignCheck unsignstring %s", unsignstring);
		
		return unsignstring;
	}
	

	@Override
	public MpspMessage getGoodsInf(String merid, String goodsid) {
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(HFBusiDict.MERID, merid);
		requestMap.put(HFBusiDict.GOODSID, goodsid);
		
		String id = merid + "-" + goodsid;
		
		MpspMessage mpspMsg = new MpspMessage();
		String url = this.getRestUrl("GOODSINF", id, mpspMsg);
		
		return (MpspMessage) this.requestUpRestGet(url, requestMap, mpspMsg);
	}

	@Override
	public boolean isXEOrder(String bankid) {
		return StringUtil.trim(bankid).startsWith("XE");
	}

	@Override
	public MpspMessage getMerInf(String merid) {
		Map<String, String> requestMap = new HashMap<String, String>();
		
		requestMap.put(HFBusiDict.MERID, merid);
		
		MpspMessage mpspMsg = new MpspMessage();
		String url = this.getRestUrl("MERINF", merid, mpspMsg);
		
		return (MpspMessage) this.requestUpRestGet(url, requestMap, mpspMsg);
	}
	
	private String getRestRpid(MpspMessage mpspMsg){
		String rpid = SessionThreadLocal.getRpid();
		
		if(ObjectUtil.trim(rpid).length() == 0){
			// 如果sessionLocal里没有 再看请求参数里有没有
			String requestMapRpid = ObjectUtil.trim(mpspMsg.getStr(HFBusiDict.RPID));
			if(requestMapRpid.length() == 0){
				// 如果请求参数里没有  生成一个
				rpid = Generate.getRpid();
			}
			SessionThreadLocal.setSessionValue(HFBusiDict.RPID, rpid);
		}
		
		return rpid;
	}
	
	private void setRestFunCode(MpspMessage mpspMsg){
		String funcode = SessionThreadLocal.getFunCode();
		
		// 如果sessionLocal里没有 再看请求参数里有没有
		if(ObjectUtil.trim(funcode).length() == 0){
			funcode = ObjectUtil.trim(mpspMsg.getStr(HFBusiDict.FUNCODE));
			if(funcode.length() == 0){
				funcode = "UNKNOW"; // 如果sessionLocal和requestMap里都没有 那就没法确定了
			}
			SessionThreadLocal.setSessionValue(HFBusiDict.FUNCODE, funcode);
		}
	}

	@Override
	public MpspMessage checkTrans(MpspMessage mpspMsg) {
		String merid = StringUtil.trim(mpspMsg.getStr(HFBusiDict.MERID));
		String goodsid = StringUtil.trim(mpspMsg.getStr(HFBusiDict.GOODSID));
		
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(HFBusiDict.MERID, merid);
		requestMap.put(HFBusiDict.GOODSID, goodsid);
		
		String id = merid + "-" + goodsid;
		
		String urlKey = "CHECKTRANSNOMO";
		String mobileid = StringUtil.trim(mpspMsg.getStr(HFBusiDict.MOBILEID));
		// 如果有手机号
		if(mobileid.length() > 0){
			urlKey = "CHECKTRANSMO";
			id += "-" + mobileid;
			requestMap.put(HFBusiDict.MOBILEID, mobileid);
		}
		String url = this.getRestUrl(urlKey, id, mpspMsg);
		
		return (MpspMessage) this.requestUpRestGet(url, requestMap, mpspMsg);
	}
	
	private String getRestUrl(String urlKey, String id, MpspMessage mpspMsg){
		String url = this.messageService.getSystemParam(urlKey + ".REST.URL");
		url = url.replaceAll("\\{businesstype\\}", ObjectUtil.trim(mpspMsg.get(HFBusiDict.BUSINESSTYPE))).replaceAll("\\{rpid\\}", getRestRpid(mpspMsg)).replaceAll("\\{id\\}", id);
		return url;
	}
	
	private MpspMessage requestUpRestGet(String url, Map<String, String> requestMap, MpspMessage mpspMsg){
		setRestFunCode(mpspMsg);
		requestMap.remove("model");
		
		MpspMessage retMsg = this.restConnPool.doGet(url, requestMap);
//		retMsg.put(HFBusiDict.VERSION, Constants.UPVERSION);
		return retMsg;
	}
	
	private MpspMessage requestUpRestPost(String url, Map<String, String> requestMap, MpspMessage mpspMsg){
		setRestFunCode(mpspMsg);
		requestMap.remove("model");
		
		MpspMessage retMsg = this.restConnPool.doPost(url, requestMap);
//		retMsg.put(HFBusiDict.VERSION, Constants.UPVERSION);
		return retMsg;
	}

	@Override
	public MpspMessage queryUpTrans(String transeq, String funcode) {
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(HFBusiDict.TRANSEQ, transeq);
		requestMap.put(HFBusiDict.FUNCODE, funcode);
		
		String id = transeq + "-" + funcode;
		
		MpspMessage mpspMsg = new MpspMessage();
		String url = this.getRestUrl("QUERYUPTRANS", id, mpspMsg);
		
		return (MpspMessage) this.requestUpRestGet(url, requestMap, mpspMsg);
		
	}
}
