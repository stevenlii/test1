package com.umpay.upweb.web.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.bs.mpsp.util.DateTimeUtil;
import com.bs.mpsp.util.StringUtil;
import com.bs3.ext.bs2.Base64;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.CaptchaServiceSingleton;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.DateUtil;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

@Controller
@RequestMapping("/web")
public class WebUPOrderAction extends BaseAction {
	
	@RequestMapping("/order")
	public String order(String mobileid, String model, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception{
		super.preHandle();
		modelMap.put(Constants.KEFU_ONLINE_URL, MessageUtil.getSysconf("KEFU.ONLINE.URL"));
		modelMap.put(Constants.KEFU_ONLINE_STYLE, MessageUtil.getSysconf("KEFU.ONLINE.STYLE"));
		/**
		 * 进入这个action有两个情况
		 * 1、商户页面直接接入
		 * 2、商户参数没有手机号，从noMobileId.jsp页面进来
		 * 这样的话，在第一种情况 model是没有值的
		 */
		RequestMsg requestMsg = new RequestMsg();
		String random = null;
		
		boolean merSignResult = false; // 是否已经验签通过
		
		boolean checkPicCode = false;
		// 说明是商户页面直接进入
		if(model == null || model.length() == 0){
			requestMsg = super.getRequestMsg();
		}
		else{
			// 从noMobileId.jsp页面进入
			checkPicCode = true;
			merSignResult = true; 
			
			Map<String, Object> requestMap = JSONObject.parseObject(Base64.decode(model), Map.class);
			requestMsg.putAllAttr(requestMap);
		}
		
		// 默认充值账号字段
		requestMsg.put(HFBusiDict.ACCOUNTID, Constants.ACCOUNTID);
		
		// 做一步不带手机号的交易鉴权  返回结果里会有商户名   商户电话等
		requestMsg.getWrappedMap().remove(HFBusiDict.MOBILEID);
		MpspMessage checkMsg = this.restService.checkTrans(requestMsg);
		requestMsg.put(HFBusiDict.MOBILEID, mobileid); // 将手机号重新放进来
		
//		requestMsg.putAll(checkMsg);
		// 把商户名和商户电话取出来  其他值废掉  省得出现商户参数被覆盖的问题
		requestMsg.put(HFBusiDict.GOODSNAME, StringUtil.trim(checkMsg.getStr(HFBusiDict.GOODSNAME)));
		requestMsg.put(HFBusiDict.CUSPHONE, StringUtil.trim(checkMsg.getStr(HFBusiDict.CUSPHONE)));
		
		if(!checkMsg.isRetCode0000()){
			requestMsg.put(HFBusiDict.RETCODE, checkMsg.getRetCode());
			requestMsg.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(checkMsg.getRetCode()));
			requestMsg.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
			modelMap.putAll(requestMsg.getWrappedMap());
			super.mpspLog(requestMsg);
			if(super.isPhone(getHttpServletRequest())){
				return "phone/error";
			}
			return "error";
		}
		
		// 参数校验
		checkMsg = super.doIPSignCheck(requestMsg);
//		requestMsg.putAll(checkMsg);
		if(!checkMsg.isRetCode0000()){
			requestMsg.put(HFBusiDict.RETCODE, checkMsg.getRetCode());
			requestMsg.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(checkMsg.getRetCode()));
			requestMsg.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
			modelMap.putAll(requestMsg.getWrappedMap());
			super.mpspLog(requestMsg);
			if(super.isPhone(request)){
				return "phone/error";
			}
			return "error";
		}
		
		ObjectUtil.logInfo(logger, "商户请求参数：%s", requestMsg.getWrappedMap().toString());
		
		// 校验订单日期   目前只允许当天的订单提交   不许跨天
		String orderdate = StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERDATE));
		boolean checkDateResult = DateUtil.verifyOrderDateStrict(orderdate);
		ObjectUtil.logInfo(logger, "订单时间校验：订单时间[%s], 校验结果[%s]", orderdate, checkDateResult);
		
		// 如果校验未通过 返回错误页面
		if(!checkDateResult){
			requestMsg.put(HFBusiDict.RETCODE, Constants.RETCODE_ORDERDATE_ERROR);
			requestMsg.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(Constants.RETCODE_ORDERDATE_ERROR));
			requestMsg.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
			modelMap.putAll(requestMsg.getWrappedMap());
			super.mpspLog(requestMsg);
			if(super.isPhone(request)){
				return "phone/error";
			}
			return "error";
		}
		
		if(checkPicCode){
			//校验验证码
			String key = "wrongNum";
			random = ObjectUtil.trim((String)request.getParameter("j_captcha_response"));
			if(!CaptchaServiceSingleton.getInstance().validateCaptchaResponse(random, request.getSession())){
				Integer num = (Integer) request.getSession().getAttribute(key);
				if(num == null){
					num = 0;
				}
				int wrongNum = num.intValue();
				wrongNum++;
				//wrongNum超过3次，forward error.jsp page
				if(wrongNum == 3){
					request.getSession().removeAttribute(key);
					
					requestMsg.put(HFBusiDict.RETCODE, Constants.RETCODE_IMGCODE_ERROR);
					requestMsg.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(Constants.RETCODE_IMGCODE_ERROR));
					requestMsg.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
					
					modelMap.putAll(requestMsg.getWrappedMap());
					if(super.isPhone(request)){
						return "phone/error";
					}
					return "error";
				}
				request.getSession().setAttribute(key,wrongNum);
				ObjectUtil.logInfo(logger, "validateCaptchaResponse Result Failed[RetCode]:%s:%s","1307","验证码校验失败");
				requestMsg.put("jcaptchaError","true");
				requestMsg.put("mobileid", mobileid);
				requestMsg.put(HFBusiDict.RETCODE, Constants.RETCODE_IMGCODE_ERROR);
				
				modelMap.addAllAttributes(requestMsg.getWrappedMap());
				modelMap.put("model", model);
				
				super.mpspLog(requestMsg);
				if(super.isPhone(request)){
					return "phone/noMobileId";
				}
				return "noMobileId";
			}
		}
		
		if(!merSignResult){
			checkMsg = this.restService.checkMerSign(requestMsg);
			ObjectUtil.logInfo(logger, "web商户下单验签结果：%s, %s", checkMsg.getRetCode(), checkMsg.getRetMsg());
			// 如果验签失败 返回错误页面
//			requestMsg.putAll(checkMsg);
			
			if(!checkMsg.isRetCode0000()){
				requestMsg.put(HFBusiDict.RETCODE, checkMsg.getRetCode());
				requestMsg.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(checkMsg.getRetCode()));
				requestMsg.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
				modelMap.putAll(requestMsg.getWrappedMap());
				super.mpspLog(requestMsg);
				if(super.isPhone(getHttpServletRequest())){
					return "phone/error";
				}
				return "error";
			}
		}

		MpspMessage responseMsg = super.restService.upOrder(requestMsg);
		
		requestMsg.putAll(responseMsg);
		
		// 将参数放入model参数   请求验证码的时候获取到来使用
		model = Base64.encode(JSONObject.toJSONString(requestMsg.getWrappedMap()).getBytes(Constants.CHART_UTF8));
		modelMap.put("model", model);
		
		// 将参数带回页面    页面上要用到的
		modelMap.addAllAttributes(requestMsg.getWrappedMap());
		
		modelMap.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(responseMsg.getRetCode()));
		modelMap.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
		ObjectUtil.logInfo(logger, "返回页面的结果：%s", modelMap.toString());
		
		super.mpspLog(requestMsg);
		
		if(!responseMsg.isRetCode0000()){
			if(super.isPhone(getHttpServletRequest())){
				return "phone/error";
			}
			return "error";
		}
		
		if(super.isPhone(request)){
			return "phone/pay";
		}
		return "pay";
	
	}
	
	@RequestMapping("/sign")
	public String sign(HttpServletResponse response, ModelMap modelMap){
		super.preHandle();
		
		MpspMessage requestMsg = super.getRequestMsg();
		String sign = super.makeMerSign(requestMsg.getStr(HFBusiDict.MERID), super.restService.getUnsignstring(requestMsg));
		requestMsg.put(HFBusiDict.SIGN, sign);
		
		requestMsg.put("isPhone", String.valueOf(super.isPhone(getHttpServletRequest())));
		
		modelMap.putAll(requestMsg.getWrappedMap());
		return "mersign";
	}
	
	@RequestMapping("/merpage")
	public String merPage(HttpServletResponse response, ModelMap modelMap){
		super.preHandle();
		
		modelMap.put(HFBusiDict.ORDERID, Math.round(Math.random() * 1000000000L));
		modelMap.put(HFBusiDict.ORDERDATE, DateTimeUtil.getDateString(System.currentTimeMillis()));
		modelMap.put("isPhone", String.valueOf(super.isPhone(getHttpServletRequest())));
		
		return "merPage";
	}
	
	
	@Override
	public FunCode getFunCode() {
		return FunCode.WEBUPORDER;
	}

}
