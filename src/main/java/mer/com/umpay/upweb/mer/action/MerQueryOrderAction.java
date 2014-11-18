package com.umpay.upweb.mer.action;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs.mpsp.util.StringUtil;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

/**
 * ****************** 类说明 ****************** 
 * class : MerDayBillAction date :
 * @author : lizhiqiang 2014年11月3日
 * @version : V1.0 description : 负责商户查单，funcode为MERCXORD
 * @see :
 * ***********************************************/
@Controller
@RequestMapping("/mer")
public class MerQueryOrderAction extends BaseAction {
/**
* @Title: merQueryOrder
* @Description: 商户查单方法
* @author lizhiqiang
* @param response
* @throws Exception
* @throws
 */
	@RequestMapping("/queryOrder")
	protected void merQueryOrder(HttpServletResponse response)
			throws Exception {

		super.preHandle();
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();
		StringBuffer returnMsg = new StringBuffer("<META NAME=");
		returnMsg.append("\"").append("MobilePayPlatform").append("\"").append(" ").append("CONTENT=").append("\"");
		Map<String, Object> responseMap = new HashMap<String, Object>();

		RequestMsg requestMsg = super.getRequestMsg();
		responseMap.put(HFBusiDict.MERID, requestMsg.getStr(HFBusiDict.MERID));
		responseMap.put(HFBusiDict.ORDERDATE, requestMsg.getStr(HFBusiDict.ORDERDATE));
		// ip + 参数校验 
		MpspMessage checkResponse = super.doIPSignCheck(requestMsg);
		String retCode = checkResponse.getRetCode();
		ObjectUtil.logInfo(logger, "商户查询验参结果：%s, %s",
				retCode, checkResponse.getRetMsg());
		if (!checkResponse.isRetCode0000()) {
			responseMap.put(HFBusiDict.RETCODE, retCode);
			responseMap.put(HFBusiDict.RETMSG, MessageUtil.getMessage(retCode));

			requestMsg.setRetCode(retCode);
			super.mpspLog(requestMsg);

			// 验参异常返回
			
			String sign = makeMerSign((String) responseMap.get(HFBusiDict.MERID), getUnsignstring(responseMap));
			//响应给商户的数据格式
			
			returnMsg.append(getUnsignstring(responseMap));
			
			returnMsg.append("|").append(sign);
			returnMsg.append("\"").append(">");
			ObjectUtil.logInfo(logger, "商户查询验参失败，返回数据：%s",returnMsg);			
			out.print(returnMsg);
			return;
		}
		//验签
		checkResponse = this.restService.checkMerSign(requestMsg, getUnsignstring(requestMsg));
		retCode = checkResponse.getRetCode();
		ObjectUtil.logInfo(logger, "商户查询验签结果：%s, %s",
				retCode, checkResponse.getRetMsg());
		if (!checkResponse.isRetCode0000()) {
			responseMap.put(HFBusiDict.RETCODE, retCode);
			responseMap.put(HFBusiDict.RETMSG, MessageUtil.getMessage(retCode));

			requestMsg.setRetCode(retCode);

			super.mpspLog(requestMsg);
			// 验签异常返回
			String sign = makeMerSign((String) responseMap.get(HFBusiDict.MERID), getUnsignstring(responseMap));
			//响应给商户的数据格式
			
			returnMsg.append(getUnsignstring(responseMap));
			
			returnMsg.append("|").append(sign);
			returnMsg.append("\"").append(">");
			requestMsg.put("returnMsg", returnMsg);
			ObjectUtil.logInfo(logger, "商户查询验签失败，返回数据：%s",returnMsg);	
			out.print(returnMsg);
			return;
		}

		MpspMessage restRtnMap = this.restService.merQueryOrder(requestMsg);
		responseMap.putAll(requestMsg.getWrappedMap());
		responseMap.putAll(restRtnMap.getWrappedMap());

		super.mpspLog(new MpspMessage(responseMap));
		responseMap.put(HFBusiDict.RETMSG, MessageUtil.getMessage(ObjectUtil
				.trim(responseMap.get(HFBusiDict.RETCODE))));
		String unSignString = getUnsignstring(responseMap);
		String sign = makePlatSign(unSignString);
		//响应给商户的数据格式
		
		returnMsg.append(unSignString);
		
		returnMsg.append("|").append(sign);
		returnMsg.append("\"").append(">");
		ObjectUtil.logInfo(logger, "商户查询结果返回数据：%s",returnMsg);	
		out.print(returnMsg);

	}

	@Override
	public FunCode getFunCode() {
		return FunCode.MERCXORD;
	}
	/**
	 * 获取商户查单时，请求信息的明文串 
	 * 签名原串： merid=$merid&orderdate=$orderdate&orderid=$orderid&upversion=1.0
	 */
	public String getUnsignstring(MpspMessage requestMsg){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HFBusiDict.MERID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.MERID)));
		buffer.append("&").append(HFBusiDict.ORDERDATE).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERDATE)));
		buffer.append("&").append(HFBusiDict.ORDERID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERID)));
		buffer.append("&").append(HFBusiDict.UPVERSION).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.UPVERSION)));
		String unsignstring = buffer.toString();
		ObjectUtil.logInfo(logger, "SignCheck unsignstring %s", unsignstring);
		
		return unsignstring;
	}
	/**
	 * 获取商户查单时，响应信息的明文串 
	 * 签名原串：merid|goodsid|orderid|orderdate|mobileid|transdate|amount|amttype|banktype|transtype|orderstate|merpriv|expand|channeled
	 * |appid|upversion|retCode
	 */
	public String getUnsignstring(Map<String, Object> responseMap){
		StringBuffer buffer = new StringBuffer();
		buffer.append(responseMap.get(HFBusiDict.MERID));
		Object goodsid = responseMap.get(HFBusiDict.GOODSID);
		buffer.append("|");
		if (goodsid == null) {
			buffer.append("");
		}else {
			buffer.append(goodsid);
		}
		Object orderid = responseMap.get(HFBusiDict.ORDERID);
		buffer.append("|");
		if (orderid == null) {
			buffer.append("");
		}else {
			buffer.append(orderid);
		}
		buffer.append("|").append(responseMap.get(HFBusiDict.ORDERDATE));
		Object mobileid = responseMap.get(HFBusiDict.MOBILEID);
		buffer.append("|");
		if (mobileid == null) {
			buffer.append("");
		}else {
			buffer.append(mobileid);
		}
		Object transdate = responseMap.get(HFBusiDict.UPTRANSDATE);
		buffer.append("|");
		if (transdate == null) {
			buffer.append("");
		}else {
			buffer.append(transdate);
		}
		Object amount = responseMap.get(HFBusiDict.AMOUNT);
		buffer.append("|");
		if (amount == null) {
			buffer.append("");
		}else {
			buffer.append(amount);
		}
		Object amttype = responseMap.get(HFBusiDict.AMTTYPE);
		buffer.append("|");
		if (amttype == null) {
			buffer.append("");
		}else {
			buffer.append(amttype);
		}
		
		Object banktype = responseMap.get(HFBusiDict.BANKTYPE);
		buffer.append("|");
		if (banktype == null) {
			buffer.append(3);//3
		}else {
			buffer.append(banktype);//3
		}
		Object transtype = responseMap.get(HFBusiDict.TRANSTYPE);
		buffer.append("|");
		if (transtype == null) {
			buffer.append(0);//0
		}else {
			buffer.append(transtype);//0
		}
		
		Object orderstate = responseMap.get(HFBusiDict.ORDERSTATE);
		buffer.append("|");
		if (orderstate == null) {
			buffer.append("");
		}else {
			buffer.append(orderstate);
		}
		Object merpriv = responseMap.get(HFBusiDict.MERPRIV);
		buffer.append("|");
		if (merpriv == null) {
			buffer.append("");
		}else {
			buffer.append(merpriv);
		}
		Object expand = responseMap.get(HFBusiDict.EXPAND);
		buffer.append("|");
		if (expand == null) {
			buffer.append("");
		}else {
			buffer.append(expand);
		}
		Object channelid = responseMap.get(HFBusiDict.CHANNELID);
		buffer.append("|");
		if (channelid == null) {
			buffer.append("");
		}else {
			buffer.append(channelid);
		}
		Object appid = responseMap.get(HFBusiDict.APPID);
		buffer.append("|");
		if (appid == null) {
			buffer.append("");
		}else {
			buffer.append(appid);
		}
		Object upversion = responseMap.get(HFBusiDict.UPVERSION);
		buffer.append("|");
		if (upversion == null) {
			buffer.append("");
		}else {
			buffer.append(upversion);
		}
		buffer.append("|").append(responseMap.get(HFBusiDict.RETCODE));
		String unsignstring = buffer.toString();
		ObjectUtil.logInfo(logger, "SignCheck unsignstring %s", unsignstring);
		
		return unsignstring;
	}
}

