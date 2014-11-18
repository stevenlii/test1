package com.umpay.upweb.system.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.umpay.hfbusi.HFBusiDict;

/**
 * 常量类
 * 
 * <p>创建日期：2012-11-14</p>
 * 
 * @version V1.0
 * @author jxd
 * @see
 */
public class Constants {
	
	private static XStream xstream = new XStream();
	public static XStream getXStreamInstance(){
		return xstream;
	}
	
	public static Map<String, String> hfBusiDictMap = new HashMap<String, String>();

	static {
		Field[] fields = HFBusiDict.class.getFields();
		for (Field f : fields) {
			try {
				hfBusiDictMap.put(f.getName(), f.get(null).toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 *  综合支付的接口版本号   之所以定义这个值是为了和小额平台的version区分开  
	 *  rest请求返回的version有可能是小额的3.0 会被覆盖 导致和商户的交互失败  比如验签 通知发货等
	 *  在Rest请求返回时 需要强制转成这个值
	 */
	public static final String UPVERSION = "1.0";
	public static final String XEVERSION = "3.0";
	
	public static final String ACCOUNTID = "umpay";
	
	/**
	 * 游戏基地的businesstype
	 */
	public static final String BUSINESSTYPE_GM_WEB_YZM = "0620";
	
	public static final String BUSINESSTYPE_XE_WEB_YZM = "0612";
	
	public static final String BUSINESSTYPE_XE_SDK_YZM = "0611";
	
	public static final String BUSINESSTYPE_BS = "0601";

	/**
	 * 平台签名key路径标签
	 */
	public static final String PLATEFORM_KEY = "PlatformKey";
	
	public static final String CLIENTIP = "clientip";
	
	public static final String RETCODE_PREFIX = "8681";

	/** 返回码——成功 */
	public static final String RETCODE_SUCCESS = "0000";
	
	/** 返回码——成功 */
	public static final String RETMSG_SUCCESS = "交易成功";
	
	/** 返回码——系统错误 */
	public static final String RETCODE_SYSERR = "9999";
	
	/** 返回信息——系统错误 */
	public static final String RETMSG_SYSERR = "系统忙，请稍候再试";
	
	/** 返回码——验签失败 */
	public static final String RETCODE_SIGN_ERR = "9998";
	
	/** 返回信息——验签失败 */
	public static final String RETMSG_SIGN_ERR = "验签失败";
	
	/** 返回码——解密数据失败 */
	public static final String RETCODE_DECODE_ERR = "9997";
	
	/** 返回信息——解密数据失败 */
	public static final String RETMSG_DECODE_ERR = "解密数据失败";
	
	/** 返回码——校验配置不存在 */
	public static final String RETCODE_MISSINGKEY = "86811001";
	
	/** 返回码——校验配置规则匹配失败 */
	public static final String RETCODE_REGEXKEY = "86811002";
	
	/** 返回码——校验配置规则匹配失败 */
	public static final String RETCODE_DICTERR = "86811003";
	
	/** 返回码——校验配置规则匹配失败 */
	public static final String RETCODE_ORDERDATE_ERROR = "86811004";
	
	// 验证码错误次数过多
	public static final String RETCODE_IMGCODE_ERROR = "86811005";

	// 游戏基地查询单笔订单返回计费失败
	public static final String RETCODE_GMCXORD_ERROR = "86811006";
	
	//Rest返回类型有误
	public static final String SYSTEM_ERROR_REST_ERROR_TYPE = "86811007";

	//Rest返回异常信息
	public static final String SYSTEM_ERROR_REST_ERROR = "86811008";
	
	//Rest未返回信息
	public static final String SYSTEM_ERROR_REST_NULL = "86811009";
	
	// WEB下单时没有手机号   进入输入手机号的页面
	public static final String RETCODE_WEBORD_NOMOBILEID = "86811010";
	
	// 手机号错误
	public static final String RETCODE_WEBORD_ERRMOBILEID = "86811011";
	
	// UPNETTYPE 错误
	public static final String RETCODE_SDKORD_ERRUPNETTYPE = "86811012";
	
	
	public static final String CHART_UTF8 = "UTF-8";
	
	public static final String CHART_GBK = "GBK";

	/** 日志——MPSP */
	public static final String LOG_MPSP = "MPSP";

	/** 防止SQL非法注入字符 */
	public static final String[] SQLKeyGroup = { "and", "exec", "insert", "select", "delete", "update", "count", "chr",
			"mid", "master", "truncate", "char", "declare" };
	public static final String[] SQLinGroup = { "'", "-", "*", "%", ";", "&", "=", ">", "<" };
	
	
	public static final String MISSINGKEY = "MISSINGKEY";
	public static final String REGEXKEY = "REGEXKEY";
	public static final String DICTERRKEY = "DICTERRKEY";
	
	
	public static final String DATA_TYPE_JSON = "JSON";
	public static final String DATA_TYPE_XML = "XML";
	public static final String DATA_TYPE_KV = "Key-Value";
	public static final String RET_CODE_BUSSI = "retcodeBussi";
	
	public static final String PAGE_RETURL = "page_returl";
	public static final String PAGE_RETMSG = "page_retmsg";
	public static final String KEFU_ONLINE_URL = "kefu_online_url";
	public static final String KEFU_ONLINE_STYLE = "kefu_online_style";
}
