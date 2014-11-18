package com.umpay.upweb.collate.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs.mpsp.util.StringUtil;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;

/**
 * ****************** 类说明 ****************** 
 * class : MerDayBillAction date :
 * 2014年11月3日
 * 
 * @author : lizhiqiang
 * @version : V1.0 description : 负责商户对账，funcode为MERDAYBILL
 * @see :
 * ***********************************************/
@Controller
@RequestMapping("/mer")
public class MerDayBillAction extends BaseAction {
	@RequestMapping("/collate")
	protected void collate(HttpServletResponse response) throws Exception {
		super.preHandle();
		RequestMsg requestMsg = super.getRequestMsg();
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String errmsg = "";
		// 商户号
		String merId = ObjectUtil.trim(requestMsg.get(HFBusiDict.MERID));
		// 支付日期
		String transDate = ObjectUtil.trim(requestMsg.get(HFBusiDict.UPTRANSDATE));
		String upversion = ObjectUtil.trim(requestMsg.get(HFBusiDict.UPVERSION));
		// ip + 参数校验 + 验签
		MpspMessage checkResponse = super.doIPSignCheck(requestMsg);
		if (!checkResponse.isRetCode0000()) {
			requestMsg.setRetCode(checkResponse.getRetCode());
			super.mpspLog(requestMsg);
			errmsg = merId + "," + transDate + "," + upversion + "," + checkResponse.getRetCode() + ",商户验参失败,-1,-1";
			out.print(errmsg);
			return;
		}
		
		checkResponse = this.restService.checkMerSign(requestMsg,getUnsignstring(requestMsg));
		ObjectUtil.logInfo(logger, "商户对账验签结果：%s, %s",
				checkResponse.getRetCode(), checkResponse.getRetMsg());

		if (!checkResponse.isRetCode0000()) {
			requestMsg.setRetCode(checkResponse.getRetCode());
			super.mpspLog(requestMsg);
			errmsg = merId + "," + transDate + "," + upversion + "," + checkResponse.getRetCode() + ",商户验签失败,-1,-1";
			out.print(errmsg);
			return;
		}

		// FIXME FILEPATH.BILL.TRADE=写成字典
		//路径：/usr/mpsp/duizhang/up/mer/collate/20141010/9996.20141010.txt
		String billPath = ObjectUtil.unifyUrl(messageService.getSystemParam("FILEPATH.BILL.TRADE"));

		String filePath = billPath	+ File.separator + transDate + File.separator + merId + "." + transDate + ".txt";
				
		File billFile = new File(filePath);
		
		if (!billFile.exists()) {
			errmsg = merId + "," + transDate + "," + upversion + ",0001,对账文件不存在,-1,-1";
					
			out.print(errmsg);
			ObjectUtil.logInfo(logger, errmsg);
		} else {
			FileReader input = new FileReader(filePath);
			BufferedReader br = new BufferedReader(input);
			String str = null;
			while ((str = br.readLine()) != null) {
				out.print(str + "\r\n");
			}
			br.close();
		}
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.MERDAYBILL;
	}

	/**
	 * 获取商户对账时，请求信息的明文串 
	 */
	public String getUnsignstring(MpspMessage requestMsg){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(HFBusiDict.MERID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.MERID)));
		buffer.append("&").append(HFBusiDict.UPTRANSDATE).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.UPTRANSDATE)));
		buffer.append("&").append(HFBusiDict.UPVERSION).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.UPVERSION)));
		String unsignstring = buffer.toString();
		ObjectUtil.logInfo(logger, "SignCheck unsignstring %s", unsignstring);
		
		return unsignstring;
	}
}
