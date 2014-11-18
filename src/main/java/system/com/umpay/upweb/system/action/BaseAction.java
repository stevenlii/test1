package com.umpay.upweb.system.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.bs.mpsp.util.FileUtil;
import com.bs.mpsp.util.SignUtil;
import com.bs.mpsp.util.StringUtil;
import com.bs3.ext.bs2.Base64;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.Encryptor;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.Generate;
import com.umpay.upweb.system.common.HTTPUtil;
import com.umpay.upweb.system.common.HttpClientControler;
import com.umpay.upweb.system.common.LogTemplateHandler;
import com.umpay.upweb.system.common.LoggerManager;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.common.SessionThreadLocal;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;
import com.umpay.upweb.system.service.CheckService;
import com.umpay.upweb.system.service.MessageService;
import com.umpay.upweb.system.service.RestService;

/**
 * ******************  类说明  ******************
 * class       :  BaseAction
 * date        :  2014年9月28日 
 * @author     :  fanxiangchi
 * @version    :  V1.0  
 * description :  
 * @see        :                         
 * **********************************************
 */
public abstract class BaseAction {
	protected Logger logger = Logger.getLogger(getClass());
	private static final Logger mpspLogger = LoggerManager.getMpspLogger();
	
	@Autowired
	protected CheckService checkService; // 校验参数的Service
	
	@Autowired
	protected RestService restService; // 请求rest服务的Service
	
	@Autowired
	protected MessageService messageService; // 配置资源的Service
	
	// 解密data
	public static final Encryptor encryptor = new Encryptor(MessageUtil.getSysconf("WXCLIENT.DES.KEY"));
	
	@Autowired
	protected LogTemplateHandler logTemplateHandler;
	
	protected void preHandle(){
		SessionThreadLocal.setSessionValue(HFBusiDict.RPID, Generate.getRpid());
		SessionThreadLocal.setSessionValue(HFBusiDict.FUNCODE, getFunCodeString());

		boolean isPhone = HTTPUtil.check(getHttpServletRequest());
		SessionThreadLocal.setSessionValue("isphone", String.valueOf(isPhone));
		
		long beginTime = System.currentTimeMillis();
		SessionThreadLocal.setSessionValue("beginTime", String.valueOf(beginTime));
	}
	
	protected boolean isPhone(HttpServletRequest request){
		String isPhone = ("false".equals(StringUtil.trim(SessionThreadLocal.getSessionValue("isphone"))) || StringUtil.trim(SessionThreadLocal.getSessionValue("isphone")).length() == 0) ? "false" : "true";
		return Boolean.valueOf(isPhone);
	}
	
	@SuppressWarnings("unchecked")
	protected RequestMsg getRequestMsg(){
		RequestMsg rm = new RequestMsg();
		try {
			String dataType = this.getRequestDataType();
			if(Constants.DATA_TYPE_JSON.equalsIgnoreCase(dataType)){
				rm.putAllAttr((Map<String, Object>) this.requestJson2Bean(getHttpServletRequest(), Map.class));
			}
			else if(Constants.DATA_TYPE_KV.equalsIgnoreCase(dataType)){
				Map<String, String> map = HttpClientControler.parseRequestParam(getHttpServletRequest());
				rm.putAllParam(map);
			}
			else{
				
			}
			rm.put(HFBusiDict.FUNCODE, getFunCode().getFunCode());
		} catch (Exception e) {
			logger.error(this.getFunCodeString() + " get request param fail");
		}
		ObjectUtil.logInfo(logger, "request data : %s", rm.getWrappedMap());
		return rm;
	}
	
	/**
	 * *****************  方法说明  *****************<br>
	 * method name   :  doIPSignCheck
	 * @param		 :  @param mpspMessage  请求参数对象
	 * @param		 :  @return
	 * @return		 :  MpspMessage 返回结果   判断retCode的值  非0000的就为校验失败
	 * @author       :  fanxiangchi 2014年9月25日 下午2:08:48
	 * description   :  IP白名单校验
	 * @see          :  
	 * **********************************************
	 */
	protected MpspMessage doIPSignCheck(MpspMessage mpspMessage){
		mpspMessage.put(HFBusiDict.FUNCODE, SessionThreadLocal.getFunCode());
		mpspMessage.put(HFBusiDict.RPID, SessionThreadLocal.getRpid());
		
		// 参数校验
		MpspMessage checkResponse = this.checkService.doParamCheck(mpspMessage);
		
		// 如果参数校验不通过
		if(!checkResponse.isRetCode0000()){
			ObjectUtil.logInfo(logger, "doParamCheck fail. retCode: %s", checkResponse.getRetCode());
			return checkResponse;
		}
		
		ObjectUtil.logInfo(logger, "doParamCheck pass. retCode: %s", checkResponse.getRetCode());
		
		// 验签
		checkResponse = this.checkService.doSignCheck(mpspMessage);
		
		// 如果验签不通过
		if(!checkResponse.isRetCode0000()){
			ObjectUtil.logInfo(logger, "doSignCheck fail. retCode: %s", checkResponse.getRetCode());
			return checkResponse;
		}
		
		ObjectUtil.logInfo(logger, "doSignCheck pass. retCode: %s", checkResponse.getRetCode());
		
		// IP白名单
		checkResponse = this.checkService.doIPCheck(mpspMessage);
		
		// 如果验签不通过
		if(!checkResponse.isRetCode0000()){
			ObjectUtil.logInfo(logger, "doIPCheck fail. retCode: %s", checkResponse.getRetCode());
			return checkResponse;
		}
		
		ObjectUtil.logInfo(logger, "doIPCheck pass. retCode: %s", checkResponse.getRetCode());
		
		return checkResponse;
	}
	
	/**
	 * *****************  方法说明  *****************<br>
	 * method name   :  requestJson2Bean
	 * @param		 :  @param request  HttpServletRequest
	 * @param		 :  @param clazz JavaBean类型 Map.class 或者 其他Object
	 * @param		 :  @return 根据入参的clazz确定返回类型
	 * @param		 :  @throws Exception 如果转换出现了异常
	 * @return		 :  Object 转换后的对象
	 * @author       :  fanxiangchi 2014年9月25日 下午2:10:19
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object requestJson2Bean(HttpServletRequest request, Class clazz) throws Exception{
		// 入参是json串
		byte[] paramByte = getDataFromInputStream(request);
		
		String paramJson = "{}";
		// 判断是否需要解密
		if("1".equals(getEncyFlag())){
			// 获取无限客户端的解密密钥
			ObjectUtil.logInfo(logger, "Requtest include encrypted data, system will decrypt it...");
			ObjectUtil.logInfo(logger, "the data from request are: " + paramByte + "It's length :" + paramByte.length);
			
			try {
				paramJson = encryptor.decryptString(paramByte);
				ObjectUtil.logInfo(logger, "the decrypted data are: %s", paramJson);
			} catch (Exception e) {
				ObjectUtil.logError(logger, " encryptor.decrypt fail: %s", e, e.getMessage());
				if(Map.class.equals(clazz)){
					Map<String, Object> rtnMap = new HashMap<String, Object>();
					rtnMap.put(HFBusiDict.RETCODE, Constants.RETCODE_DECODE_ERR);
					rtnMap.put(HFBusiDict.RETMSG, Constants.RETMSG_DECODE_ERR);
					return rtnMap;
				}
			} finally {
				if (StringUtil.trim(paramJson).length() == 0 || !paramJson.startsWith("{")) {
					paramJson = "{}";
					ObjectUtil.logInfo(logger, "encryptor.decrypt result is empty or not regex json rule", new Object[]{});
				}
			}
		}
		else{
			ObjectUtil.logInfo(logger, "No encrypted data, system will parse it directly!");
			paramJson = new String(paramByte, "UTF-8");
			ObjectUtil.logInfo(logger, "The parsed data are ：%s", paramJson);
		}
		
		// 转成java对象
		Object o = JSON.parseObject(paramJson, clazz);
		return o;
	}
	
	/**
	 * 获取HttpServletRequest对象
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getHttpServletRequest(){
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	public void doResponse(Map<String, Object> responseMap, HttpServletResponse response) throws Exception{
		String dataType = getResponseDataType();
	
		if(Constants.DATA_TYPE_JSON.equalsIgnoreCase(dataType)){
			this.responseJson(responseMap, response);
		}
		else if(Constants.DATA_TYPE_XML.equalsIgnoreCase(dataType)){
			
		}
		else if(Constants.DATA_TYPE_KV.equalsIgnoreCase(dataType)){
			this.responseKV(responseMap, response);
		}
		
	}
	
	
	/**
	 * *****************  方法说明  *****************<br>
	 * method name   :  responseKV
	 * @param		 :  @param responseMap
	 * @param		 :  @param response
	 * @param		 :  @throws IOException
	 * @return		 :  void
	 * @author       :  fanxiangchi 2014年9月28日 下午5:17:11
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	private void responseKV(Map<String, Object> responseMap, HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("UTF-8");
		OutputStream os = null;
		try {
			
//			String out = JSONObject.toJSONString(responseMap);
			
			// 需要用net.sf.json包的JsonObject  fastjson会报错
			String out = net.sf.json.JSONObject.fromObject(responseMap).toString();
			
			ObjectUtil.logInfo(logger, "response json: %s", out);
			
			
			os = response.getOutputStream();
			os.write(out.getBytes(Constants.CHART_UTF8));
		} 
		catch(Exception e){
			ObjectUtil.logError(logger, "responseJson fail：%s", e, e.getMessage());
		}
		finally {
			if(os != null){
				os.flush();
				os.close();
			}
		}
	}
	
	private void responseJson(Map<String, Object> responseMap, HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("UTF-8");
		OutputStream os = null;
		try {
			
//			String out = JSONObject.toJSONString(responseMap);
			
			// 需要用net.sf.json包的JsonObject  fastjson会报错
			String out = net.sf.json.JSONObject.fromObject(responseMap).toString();
			
			byte[] encByte;
			if("1".equals(getEncyFlag())){
				ObjectUtil.logInfo(logger, "response need to Encryptor", new Object[]{});
				
				encByte = encryptor.encyptString(out);
				
				ObjectUtil.logInfo(logger, "response Encryptor pass", new Object[]{});
			}
			else{
				encByte = out.getBytes(Constants.CHART_UTF8);
			}
			
			os = response.getOutputStream();
			os.write(encByte);
		} 
		catch(Exception e){
			ObjectUtil.logError(logger, "responseJson fail：%s", e, e.getMessage());
		}
		finally {
			if(os != null){
				os.flush();
				os.close();
			}
		}
	}
	
	protected byte[] getDataFromInputStream(HttpServletRequest request) {
		InputStream in = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			request.setCharacterEncoding(Constants.CHART_UTF8);
			in = request.getInputStream();
			
			byte[] buffer = new byte[in.available()];
			while ((in.read(buffer)) != -1) {
				baos.write(buffer);
			}
			
		} catch (IOException e) {
			ObjectUtil.logError(logger, "getDataFromInputStream.readInputStream fail：%s", e, e.getMessage());
		} finally {
			try {
				if (in != null){
					in.close();
				}
				if (baos != null){
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				ObjectUtil.logError(logger, "getDataFromInputStream.readInputStream fail：%s", e, e.getMessage());
			}
		}
		
		return baos.toByteArray();
	}
	
	/**
	 * @Title: platSign
	 * @Description: 平台签名
	 * @param @param content
	 * @param @return    设定文件
	 * @return String    返回类型
	 * @throws WebBusiException 
	 * @throws
	 */
	protected String makePlatSign(String content){
		String keyFile = messageService.getSystemParam(Constants.PLATEFORM_KEY);
		return this.makeSign(keyFile, content);
	}
	
	protected String makeMerSign(String merid, String content){
		String keyFile = messageService.getSystemParam("TEST.MER.SignKey");
		return this.makeSign(keyFile, content);
	}
	
	private String makeSign(String keyFile, String content){
		File file = new File(keyFile);
		if(!file.exists()){
			throw new IllegalArgumentException("platform key file is not exists: " + keyFile);
		}
		byte[] key = FileUtil.getFileContent(keyFile);
		PrivateKey pk = SignUtil.genPrivateKey(key);
		byte[] signData = SignUtil.sign(pk, content.getBytes());
		String sign = Base64.encode(signData);
		return sign;
	}
	
	protected String genWholeRetUrl(MpspMessage mpspMsg) {
		String retUrl = mpspMsg.getStr(HFBusiDict.RETURL);
		if (retUrl.indexOf("?") == -1){
			retUrl += "?";
		}else{
			retUrl += "&";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(retUrl);
		
		String unsignstring = this.getUnsignstring(mpspMsg);
		String sign = makePlatSign(unsignstring);
		ObjectUtil.logInfo(logger, "Info2Mer Plain Text:%s", unsignstring);
		ObjectUtil.logInfo(logger, "Info2Mer Sign Text:%s", sign);
		buffer.append(getEncodedText(sign, mpspMsg));
		return buffer.toString();
	}
	
	/**
	 * ***************** 组装完整returl  *****************<br>
	 * method name   :  getEncodedText
	 * @param		 :  @param signStr 签名串
	 * @param		 :  @param requestMsg 参数对象
	 * @param		 :  @return
	 * @return		 :  String url的串
	 * @author       :  fanxiangchi 2014年10月13日 下午3:29:14
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	private String getEncodedText(String signStr, MpspMessage requestMsg){
		StringBuffer buffer = new StringBuffer();
		try {
			buffer.append(HFBusiDict.MERID).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.MERID)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.GOODSID).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.GOODSID)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.ORDERID).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERID)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.ORDERDATE).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERDATE)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.AMOUNT).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.AMOUNT)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.AMTTYPE).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.AMTTYPE)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.BANKTYPE).append("=").append(URLEncoder.encode("3", Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.MOBILEID).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.MOBILEID)), Constants.CHART_UTF8)).append("&");
//			buffer.append(HFBusiDict.ACCOUNTID).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.ACCOUNTID)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.CHANNELID).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.CHANNELID)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.APPID).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.APPID)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.MERPRIV).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.MERPRIV)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.RETCODE).append("=").append(URLEncoder.encode(requestMsg.getRetCode(), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.UPVERSION).append("=").append(URLEncoder.encode(StringUtil.trim(requestMsg.getStr(HFBusiDict.UPVERSION)), Constants.CHART_UTF8)).append("&");
			buffer.append(HFBusiDict.SIGN).append("=").append(URLEncoder.encode(signStr, Constants.CHART_UTF8));
			
			return buffer.toString();
		} catch (Exception e) {
			ObjectUtil.logError(logger, "组装返回商户url异常：%s", e, e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * ***************** 返回商家页面的url的参数串       区别于验签时的串  验签时调用的已有的rest服务  所以用到了Constants类的key  *****************<br>
	 * method name   :  getUnsignstring
	 * @param		 :  @param requestMsg
	 * @return		 :  String
	 * @author       :  fanxiangchi 2014年10月9日 下午3:08:13
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public String getUnsignstring(MpspMessage requestMsg){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(HFBusiDict.MERID).append("=").append(ObjectUtil.trim(requestMsg.getStr(HFBusiDict.MERID))).append("&");
		buffer.append(HFBusiDict.GOODSID).append("=").append(ObjectUtil.trim(requestMsg.getStr(HFBusiDict.GOODSID))).append("&");
		buffer.append(HFBusiDict.MOBILEID).append("=").append(ObjectUtil.trim(requestMsg.getStr(HFBusiDict.MOBILEID))).append("&");
		buffer.append(HFBusiDict.ORDERID).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERID))).append("&");
		buffer.append(HFBusiDict.ORDERDATE).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.ORDERDATE))).append("&");
		buffer.append(HFBusiDict.AMOUNT).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.AMOUNT))).append("&");
		buffer.append(HFBusiDict.AMTTYPE).append("=").append(StringUtil.trim(requestMsg.getStr(HFBusiDict.AMTTYPE))).append("&");
		buffer.append(HFBusiDict.BANKTYPE).append("=").append("3").append("&");
		buffer.append(HFBusiDict.MERPRIV).append("=").append(ObjectUtil.trim(requestMsg.getStr(HFBusiDict.MERPRIV))).append("&");
		buffer.append(HFBusiDict.EXPAND).append("=").append(ObjectUtil.trim(requestMsg.getStr(HFBusiDict.EXPAND))).append("&");
		buffer.append(HFBusiDict.CHANNELID).append("=").append(ObjectUtil.trim(requestMsg.getStr(HFBusiDict.CHANNELID))).append("&");
		buffer.append(HFBusiDict.APPID).append("=").append(ObjectUtil.trim(requestMsg.getStr(HFBusiDict.APPID))).append("&");
		buffer.append(HFBusiDict.RETCODE).append("=").append(requestMsg.getRetCode()).append("&");
		buffer.append(HFBusiDict.UPVERSION).append("=").append(ObjectUtil.trim(requestMsg.getStr(HFBusiDict.UPVERSION)));
		
		return buffer.toString();
	}
	
	protected void mpspLog(MpspMessage requestMsg){
		long beginTime = Long.parseLong(SessionThreadLocal.getSessionValue("beginTime"));
		long useTime = System.currentTimeMillis() - beginTime;
		
		StringBuffer logBuffer = new StringBuffer(logTemplateHandler.createLog(requestMsg.getWrappedMap()));
		logBuffer.append(useTime);
		
		mpspLogger.info(logBuffer.toString());
	}
	
	/**
	 * 接收请求接口的数据类型   json/key-value/xml
	 * 在配置文件中做的映射  根据FunCode.methodName.request
	 * @return JSON   KV
	 */
	private String getRequestDataType(){
		return getFunCode().getRequestDataType();
	}
	
	/**
	 * 响应接口的数据类型    json/key-value/xml
	 * FunCode.methodName.response
	 * @return JSON   KV
	 */
	private String getResponseDataType(){
		return getFunCode().getResponseDataType();
	}

	private String getEncyFlag(){
		return getFunCode().getEncyFlag();
	}
	
	private String getFunCodeString(){
		return getFunCode().getFunCode();
	}
	
	public String getSignFlag(){
		return getFunCode().getSignFlag();
	}
	
	/**
	 * 获取FunCode   目前用于参数校验
	 * @return
	 */
	public abstract FunCode getFunCode();
}
