package com.umpay.upweb.mer.action;


import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs.mpsp.util.StringUtil;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;

/**
 * ****************** 类说明 ****************** 
 * class : MerDayBillAction date :
 * @author : lizhiqiang 2014年11月3日
 * @version : V1.0 description : 负责商户查单，funcode为MERCXORD
 * @see :
 * ***********************************************/
@Controller
@RequestMapping("/mertest")
public class MerQueryOrder extends BaseAction{
/**
* @Title: merQueryOrder
* @Description: 商户查单方法
* @author lizhiqiang
* @param response
* @throws Exception
* @throws
 */
	@RequestMapping("/queryOrder")
	protected String merQueryOrder(HttpServletResponse response)
			throws Exception {
		return "merqueryorder";
	}
	@RequestMapping("/sign")
	protected String merSign(HttpServletResponse response)
			throws Exception {
		
		String sign = makePlatSign(getUnsignstring(super.getRequestMsg()));
		super.getHttpServletRequest().getSession().setAttribute("sign", sign);
		return "merqueryordersign";
	}
	@Override
	public FunCode getFunCode() {
		// TODO Auto-generated method stub
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
	
}

