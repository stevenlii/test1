package com.umpay.upweb.web.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs.mpsp.util.DateTimeUtil;
import com.bs3.ext.bs2.Base64;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.HTTPUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.model.MpspMessage;

@Controller
@RequestMapping("/test/merpage")
public class MerPageTest extends BaseAction{

	@RequestMapping("/self")
	public void self(HttpServletResponse response){
		super.preHandle();
		Map<String, String> requestMap = new HashMap<String, String>();
		this.setRequestMap(requestMap);
		
		/**
		 * <input type="hidden" name="platType" value="6"/>
			<input type="hidden" name="amttype" value="02"/>
			<input type="hidden" name="banktype" value="3"/>
			<input type="hidden" name="version" value="1.0"/>
			<input type="hidden" name="gateid" value="HF"/>
		 */
//		requestMap.put("platType", "6");
//		requestMap.put("amttype", "02");
//		requestMap.put("banktype", "3");
//		requestMap.put("version", "1.0");
//		requestMap.put("gateid", "HF");
		
		MpspMessage requestMsg = new MpspMessage();
		requestMsg.getWrappedMap().putAll(requestMap);
		
		String sign = super.makeMerSign(requestMap.get(HFBusiDict.MERID), super.restService.getUnsignstring(requestMsg));
		requestMap.put(HFBusiDict.SIGN, sign);
		
		String url = "http://localhost:9001/upwebbusi/web/order.do";
		try {
			String result = (String) HTTPUtil.get(url, requestMap);
			ObjectUtil.logInfo(logger, "商户破解测试返回结果：%s", result);
			
			response.getWriter().print(result);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setRequestMap(Map<String, String> requestMap){
		String param = "orderdate=20141015, amttype=02, goodsinfo=Test goodsinfo, platType=6, merpriv=9996001, appid=angbird, funcode=WEBUPORD, merid=9996, channelid=0001, version=1.0, amount=100, retCode=9999, goodsid=100, banktype=3, returl=http://localhost:9001/upwebbusi/web/merpage.do, expand=merexpand, gateid=HF, orderid=948078233, mobileid=15017568581";
		String[] paramList = param.split(",");
		for(String paramstring : paramList){
			paramstring = paramstring.trim();
			String[] entry = paramstring.split("=");
			requestMap.put(entry[0], entry[1]);
		}
		
		requestMap.put(HFBusiDict.ORDERID, String.valueOf(Math.round(Math.random() * 1000000000L)));
		requestMap.put(HFBusiDict.ORDERDATE, DateTimeUtil.getDateString(System.currentTimeMillis()));
	}

	@Override
	public FunCode getFunCode() {
		return FunCode.WEBUPORDER;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String model = "eyJCVVNJU1BFTkRUSU1FIjo2NiwiYWNjb3VudGlkIjoidW1wYXkiLCJhbW91bnQiOiIxMDAiLCJhbXR0eXBlIjoiMDIiLCJhcHBpZCI6ImFuZ2JpcmQiLCJhcmVhY29kZSI6IjAyMCIsImFyZWFuYW1lIjoi5bm/5beeIiwiYmFua2lkIjoiR00wMjAwMDAiLCJiYW5rdHlwZSI6IjMiLCJidXNpZGVzYyI6IiIsImJ1c2luZXNzdHlwZSI6IjA2MjAiLCJidXNpcm9sbHR5cGUiOiIqIiwiY2FyZHR5cGUiOiIwIiwiY2hhbm5lbGlkIjoiMDAwMSIsImN1c3Bob25lIjoiMDEwLTU4MzUxMTIyLTg4OTQiLCJleHBhbmQiOiJtZXJleHBhbmQiLCJleHBpcmV0aW1lIjoiMjAxNC0xMC0xNiAyMDoyODozMi44MzgiLCJmZWV0eXBlIjoyLCJmdW5jb2RlIjoiV0VCVVBPUkQiLCJnYXRlaWQiOiJIRiIsImdvb2RzZGVzYyI6InRlc3QiLCJnb29kc2lkIjoiMTAwIiwiZ29vZHNpbmZvIjoiVGVzdCBnb29kc2luZm8iLCJnb29kc25hbWUiOiJzcG5hbWUiLCJoYXNVTUciOiIwIiwibWVyaWQiOiI5OTk2IiwibWVybmFtZSI6IuiBlOWKqOS8mOWKv1NQ5rWL6K+VIiwibWVycHJpdiI6Ijk5OTYwMDEiLCJtb2JpbGVpZCI6IjE1MDE3NTY4NTgxIiwibXRudW0iOjIsIm5ldHR5cGUiOjEsIm5vdGlmeW1vZGUiOjMxLCJvcmRlcmRhdGUiOiIyMDE0MTAxNSIsIm9yZGVyaWQiOiI4NDY5ODkzMzMiLCJwbGF0VHlwZSI6IjYiLCJwbGF0b3JkaWQiOiIyMDE0MTAxNTIwMjgzMjk5OTYwMDAwMDAwMDAyOTAyMyIsInByaWNlbW9kZSI6MCwicHJvdmNvZGUiOiIwMjAiLCJwcm92bmFtZSI6IuW5v+S4nCIsInB1c2hpbmYiOjEsInJldENvZGUiOiIwMDAwIiwicmV0TXNnIjoi5Lqk5piT5oiQ5YqfIiwicmV0dXJsIjoiaHR0cDovL2xvY2FsaG9zdDo5MDAxL3Vwd2ViYnVzaS93ZWIvbWVycGFnZS5kbyIsInJwaWQiOiJVMTAxNTIwMzMwNmw1Q0V3Iiwic2VydmljZWlkIjoiMDA2MDQzNDk4MDg2Iiwic2VydmljZWlkMSI6IiIsInNlcnZpY2VpZDIiOiIiLCJzZXJ2aWNlbmFtZSI6IuaYhuS7keW4gTLlhYMiLCJzZXJ2aWNlbmFtZTEiOiIiLCJzZXJ2aWNlbmFtZTIiOiIiLCJzZXJ2dHlwZSI6Miwic2lnbiI6IlYraFZ0NnlnOHF1MmNRV2pXb05QME5QS2tGbHlnS2tWbE50cWlWUkxXTlBQSzZMRFBqVjRHVmtyR0FGa0dPVDV0bXV0UVJxQyt5WldwdVdnZFRsb1FGRVZReFJOYlUzNGhEZHZlWmhvSFdmelBMaG91a0JtYlIrNzVIM3B0UjAzZHNkU2tpalkrMG5wczZOb0UyUXRrZWdvY3lRN1I5bE8wbE5TQW1TRzM4OD0iLCJzdGF0ZSI6MiwidmVyc2lvbiI6IjEuMCJ9";
		System.out.println(new String(Base64.decode(model)));
		
		System.out.println(URLEncoder.encode("举火", "UTF-8"));
	}
}
