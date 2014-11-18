package com.umpay.upweb.web.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.bs.mpsp.util.StringUtil;
import com.bs3.ext.bs2.Base64;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

/** ******************  类说明  ******************
 * class       :  WebUPOrderNMOAction
 * date        :  2014年10月9日 
 * @author     :  lizhiqiang
 * @version    :  V1.0  
 * description :  负责接收没有手机号的请求，funcode为WEBUPORDERNMO
 * @see        :                         
 * ***********************************************/
@Controller
@RequestMapping("/web")
public class WebUPOrderNMOAction extends BaseAction {
	
	@RequestMapping("/noMobileId")
	public String noMobileId(HttpServletRequest request, ModelMap modelMap) throws Exception{
		super.preHandle();
		modelMap.put(Constants.KEFU_ONLINE_URL, MessageUtil.getSysconf("KEFU.ONLINE.URL"));
		modelMap.put(Constants.KEFU_ONLINE_STYLE, MessageUtil.getSysconf("KEFU.ONLINE.STYLE"));
		RequestMsg requestMsg = super.getRequestMsg();
		
		// 做一步不带手机号的交易鉴权  返回结果里会有商户名   商户电话等
		String mobileid = ObjectUtil.trim(requestMsg.getWrappedMap().remove(HFBusiDict.MOBILEID));
		MpspMessage checkMsg = this.restService.checkTrans(requestMsg);
		requestMsg.put(HFBusiDict.MOBILEID, mobileid); // 将手机号重新放进来
		
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
		
		checkMsg = this.restService.checkMerSign(requestMsg);
		// 商户验签  如果这步验签通过了 会放一个标记进去   进入order.do的时候就不再验签了
		ObjectUtil.logInfo(logger, "web商户下单无手机号验签结果：%s, %s", checkMsg.getRetCode(), checkMsg.getRetMsg());
		// 如果验签失败 返回错误页面
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
		
		// 将商户参数放到页面   输入手机号之后提交到/web/pay  Base64加密
		Map<String, Object> requestMap = requestMsg.getWrappedMap();
		
		
		
		modelMap.addAllAttributes(requestMap);
		String model = Base64.encode(JSONObject.toJSONString(requestMap).getBytes(Constants.CHART_UTF8));
		modelMap.put("model", model);
		
		super.mpspLog(requestMsg);
		
		if(super.isPhone(getHttpServletRequest())){
			return "phone/noMobileId";
		}
		return "noMobileId";
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.WEBUPORDERNMO;
	}

}
