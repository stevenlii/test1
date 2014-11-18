package com.umpay.upweb.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.bs3.ext.bs2.Base64;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;
import com.umpay.upweb.web.action.service.WebService;

@Controller
@RequestMapping("/web")
public class WebUPPayAction extends BaseAction {
	
	@Autowired
	private WebService webService;
	
	/**
	 * ***************** ajax请求获取验证码  *****************<br>
	 * method name   :  getVerifyCode
	 * @param		 :  @param mobileid
	 * @param		 :  @param model
	 * @param		 :  @param response
	 * @param		 :  @param modelMap
	 * @param		 :  @throws Exception
	 * @return		 :  void
	 * @author       :  fanxiangchi 2014年10月8日 下午7:19:49
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	@RequestMapping("/pay")
	public void getVerifyCode(String mobileid, String model, HttpServletResponse response, ModelMap modelMap) throws Exception{
		super.preHandle();
		modelMap.put(Constants.KEFU_ONLINE_URL, MessageUtil.getSysconf("KEFU.ONLINE.URL"));
		modelMap.put(Constants.KEFU_ONLINE_STYLE, MessageUtil.getSysconf("KEFU.ONLINE.STYLE"));
		// 定义返回Map  里面包含retCode retMsg model三个key
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("model", model);
		
		RequestMsg requestMsg = super.getRequestMsg();

		Map<String, Object> requestMap = JSONObject.parseObject(Base64.decode(model), Map.class);
		requestMap.put(HFBusiDict.MOBILEID, mobileid);
		requestMsg.getWrappedMap().putAll(requestMap);
		
		ObjectUtil.logInfo(logger, "请求参数：%s", requestMap.toString()); 
		
		// 参数校验
		MpspMessage checkMsg = super.doIPSignCheck(requestMsg);
		requestMsg.putAll(checkMsg);
		
		if(!checkMsg.isRetCode0000()){
			responseMap.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
			responseMap.put(HFBusiDict.RETCODE, checkMsg.getRetCode());
			responseMap.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(checkMsg.getRetCode()));
			super.mpspLog(requestMsg);
			super.doResponse(responseMap, response);
			return;
		}
		
		MpspMessage restMsg = (MpspMessage) super.restService.upRestPay(requestMsg);
		requestMsg.putAll(restMsg);
		if(!restMsg.isRetCode0000()){
			responseMap.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
			responseMap.put(HFBusiDict.RETCODE, restMsg.getRetCode());
			responseMap.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(restMsg.getRetCode()));
			super.mpspLog(requestMsg);
			super.doResponse(responseMap, response);
			return;
		}
		
		// 将rest返回的数据放入请求Map中   注意顺序    千万不能覆盖rest返回数据  只能是rest数据覆盖请求的
		model = Base64.encode(JSONObject.toJSONString(requestMsg.getWrappedMap()).getBytes(Constants.CHART_UTF8));
		
		responseMap.put("model", model);
		responseMap.put(HFBusiDict.RETCODE, restMsg.getRetCode());
		responseMap.put(Constants.PAGE_RETMSG, MessageUtil.getMessage(restMsg.getRetCode()));
		responseMap.put(HFBusiDict.BUSINESSTYPE, restMsg.getStr(HFBusiDict.BUSINESSTYPE));
		responseMap.put(Constants.PAGE_RETURL, super.genWholeRetUrl(requestMsg));
		
		super.mpspLog(requestMsg);
		super.doResponse(responseMap, response);
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.WEBUPPAY;
	}

}
