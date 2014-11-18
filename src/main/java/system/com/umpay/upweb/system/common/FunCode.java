package com.umpay.upweb.system.common;


/**
 * 支付流程的枚举
 * @author fanxiangchi
 *
 */
public enum FunCode {
	
	/**
	 * 统一下单
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 */
	SDKUPORD("SDKUPORD", "SDK请求UPWEB下单", Constants.DATA_TYPE_JSON, Constants.DATA_TYPE_JSON, "1", "0", "0"),
	
	/**
	 * 准备支付funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 * R4：返回mo1  mo2
	 * BS: 返回inner_id等要素 并发送验证码
	 * 
	 */
	SDKUPPAY("SDKUPPAY", "SDK请求UPWEB准备支付", Constants.DATA_TYPE_JSON, Constants.DATA_TYPE_JSON, "1", "0", "0"),
	
	/**
	 * 博升通道的支付  包括验证短信码+支付
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 */
	SDKCONFIRMPAY("SDKQRPAY", "SDK确认支付", Constants.DATA_TYPE_JSON, Constants.DATA_TYPE_JSON, "1", "0", "0"),
	
	
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 */
	GWBSSHIP("GWBSSHIP", "博升请求UPWEB发货接口", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "1", "1"),
	
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 */
	GWBSCHECK("GWBSCHEk", "博升请求UPWEB支付验证接口", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "1", "1"),
	
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 */
	GWBSNOTIFY("GWBSNOTI", "接收博升通道的支付结果通知", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "1", "1"),
	
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 */
	SDKQUERYORD("SDKCXORD", "SDK查单", Constants.DATA_TYPE_JSON, Constants.DATA_TYPE_JSON, "1", "0", "0"), // sdk查单接口

	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 * 20140930 WEB下单
	 */
	WEBUPORDER("WEBUPORD", "WEB统一下单（进入统一支付页面）", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "0", "0"),
	
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 * 20141008 WEB下单
	 */
	WEBUPORDERNMO("WEBNMORD", "WEB统一下单（无手机号的请求）", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "0", "0"),
	
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 * 20140930 WEB支付
	 */
	WEBUPPAY("WEBUPPAY", "WEB统一支付（发送验证码）", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "0", "0"),
	
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 * 20140930  游戏基地确认支付   GM=game market
	 */
	WEBCONFIRMPAY("WEBQRPAY", "WEB确认支付", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "0", "0"),
	
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 */
	GWXENOTIFY("GWXENOTI", "接收小额通道的支付结果通知", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "0", "1"),
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 *  * 2014.11.03  商户查单   
	 */
	MERCXORD("MERCXORD", "商户查单", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_JSON, "0", "0", "1"),
	/**
	 * funcode, desc, requestDataType, responseDataType, 解密 ， 验签， ip控制
	 *  * 2014.11.04  商户对账   
	 */
	MERDAYBILL("MERDAYBL", "商户对账", Constants.DATA_TYPE_KV, Constants.DATA_TYPE_KV, "0", "0", "1"),
	
	/**
	 * 游戏基地ido业务的结果通知接口
	 * game market ido return
	 */
	GMIDORESULT("GMIDORTN", "游戏基地ido业务通知", Constants.DATA_TYPE_XML, Constants.DATA_TYPE_XML, "0", "0", "0");
	
	private String funCode;
	private String desc;
	private String requestDataType;
	private String responseDataType;
	private String encyFlag; // 是否需要解密
	private String signFlag; // 是否需要验签
	private String ipLimit; // ip访问限制   
	
	private FunCode(String funCode, String desc, String requestDataType, String responseDataType, String encyFlag, String signFlag, String ipLimit){
		this.funCode = funCode;
		this.desc = desc;
		this.requestDataType = requestDataType;
		this.responseDataType = responseDataType;
		this.encyFlag = encyFlag;
		this.signFlag = signFlag;
		this.ipLimit = ipLimit;
	}
	
	public static FunCode getFunCode(String funcode){
		FunCode[] array = FunCode.values();
		for(FunCode code : array){
			if(funcode.equals(code.getFunCode())){
				return code;
			}
		}
		return null;
	}
	
	public String getFunCode(){
		return this.funCode;
	}
	
	public String getDesc(){
		return this.desc;
	}

	public String getRequestDataType() {
		return requestDataType;
	}

	public String getResponseDataType() {
		return responseDataType;
	}

	public String getEncyFlag() {
		return encyFlag;
	}

	public String getSignFlag() {
		return signFlag;
	}

	public void setResponseDataType(String responseDataType) {
		this.responseDataType = responseDataType;
	}

	public String getIpLimit() {
		return ipLimit;
	}
	
}
