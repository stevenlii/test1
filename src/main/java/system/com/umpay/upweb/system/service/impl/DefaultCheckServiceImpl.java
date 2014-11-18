package com.umpay.upweb.system.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.bs.mpsp.util.StringUtil;
import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.MessageUtil;
import com.umpay.upweb.system.common.ObjectUtil;
import com.umpay.upweb.system.common.SessionThreadLocal;
import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.service.CheckService;

/**
 * ******************  类说明  *********************
 * class       :  DefaultCheckServiceImpl
 * @author     :  yangwr
 * @version    :  1.0  
 * description :  检验输入参数
 * @see        :                        
 * ***********************************************
 */

@Service("checkService")
public class DefaultCheckServiceImpl implements CheckService{
	public static Logger log = Logger.getLogger(DefaultCheckServiceImpl.class);
	
	private static final String Separator = new String("[,]");
	private static String[] reqArrays = null;
	
	@Override
	public MpspMessage doSignCheck(MpspMessage paraMessage){
		String funcode = (String) paraMessage.get(HFBusiDict.FUNCODE);

		ObjectUtil.logInfo(log, "sign check start", new Object[]{});
		MpspMessage rtnMap = new MpspMessage();
		
		rtnMap.setRetCode0000();
		
		// 判断这个FunCode是否需要做参数校验
		if("0".equals(FunCode.getFunCode(funcode).getSignFlag())){
			return rtnMap;
		}
		
		// 获取该业务的请求数据列表
		String keys = MessageUtil.getSign("FUNCODE." + funcode);
		ObjectUtil.logInfo(log, "doSign sign.properties key ：%s", keys);
		
		boolean isPass = false;
		if(ObjectUtil.isEmpty(keys)){
			isPass = true;
		}
		else{
			String[] keys2Check = keys.split(Separator);
			// 获取参数
			StringBuilder sb = new StringBuilder();
			for(String key : keys2Check){
				String value = paraMessage.getStr(StringUtil.trim(key));
				
				sb.append("&").append(key).append("=").append(value);
			}
			
			String signString = sb.substring(1);
			ObjectUtil.logInfo(log, "doSignCheck signString : %s", signString);
			// 加上博升分配的商户密钥
			String secrtykey =  MessageUtil.getSign("BS.UMPAY.SECRTYKEY");
			
			String sign = DigestUtils.md5Hex(signString + secrtykey);
			
			String gwSign = paraMessage.getStr(HFBusiDict.SIGN);
			
			if(!sign.equals(gwSign)){
				rtnMap.put(HFBusiDict.RETCODE, Constants.RETCODE_SIGN_ERR);
				rtnMap.put(HFBusiDict.RETMSG, funcode + "验签失败");
				return rtnMap;
			}
			isPass = true;
		}
		
		
		if (isPass){
			log.debug("验签通过！");
		} else {
			log.debug("验签通过！");
		}
		
		ObjectUtil.logInfo(log, "sign check result : %s", "success");
		return rtnMap;
	}
	
	private static Map<String, String> code4DictMap = new HashMap<String, String>();
	private static Map<String, String> getCode4DictMap() {
		// CHECK.SPEC = UPNETTYPE:86811012;MOBILEID:86811012
		String specCheckConf = StringUtil.trim(MessageUtil.getValidate("CHECK.SPEC"));
		
		String[] specConfigArr = specCheckConf.split(";");
		if(specConfigArr.length > 0){
			for(String specComp : specConfigArr){
				String[] entry = StringUtil.trim(specComp).split(":");
				String dict = StringUtil.trim(entry[0]);
				String code = StringUtil.trim(entry[1]);
				code4DictMap.put(dict, code);
			}
		}
		return code4DictMap;
	}
	
	/**
	 * ********************************************
	 * method name   : doCheck 
	 * modified      : yangwr ,  Nov 2, 2011
	 * @throws Exception 
	 * @see          : @see com.umpay.hfweb.service.CheckService#doCheck(com.umpay.hfweb.model.MpspMessage)
	 * *******************************************
	 */
	public MpspMessage doParamCheck(MpspMessage paraMessage) {
		MpspMessage rtnMap = new MpspMessage();
		rtnMap.setRetCode0000();
		String funcode = SessionThreadLocal.getFunCode();
		
		String bussinesstype = (String) paraMessage.get(HFBusiDict.BUSINESSTYPE);
		if(StringUtil.trim(bussinesstype).length() == 0){
			bussinesstype = "*";
		}
		
		String checkKey = "FUNCODE." + funcode + "." + bussinesstype;
		// 获取该业务的请求数据列表
		String keys = MessageUtil.getValidate(checkKey);
		ObjectUtil.logInfo(log, "doParamCheck查找的key：%s, 获得的配置为：%s", checkKey, keys);
		boolean isPass = false;
		if(ObjectUtil.isEmpty(keys)){
			// modify by fanxiangchi 20140924 如果没有配置校验字段  默认不校验      只有配置了才校验
			log.debug("doParamCheck 未找到请求数据keys配置文件！无需校验！");
			isPass = true;
		}
		else{
			
			reqArrays = keys.split(",");
			for (String key : reqArrays) { // 大写字典数组
				// 判断是否为必输参数，若参数使用[]扩住，说明为可选参数
				boolean isNeeded = true;
				String configKey = "";
				if (key.startsWith("[") && key.endsWith("]")) {
					// 非必须校验字段
					key = key.substring(1, key.length() - 1);
					isNeeded = false;
				}
				// 获取配置的key名称
				configKey = Constants.hfBusiDictMap.get(key); // 真是的参数key
				
				if (configKey == null) {
					ObjectUtil.logInfo(log, "数据字典无此参数配置,参数为:%s", key);
					
					String retCode = StringUtil.trim(getCode4DictMap().get(key));
					if(retCode.length() == 0){
						retCode = Constants.RETCODE_DICTERR;
					}
					
					//字典里查无此KEY
					rtnMap.put(HFBusiDict.RETCODE, retCode);
					rtnMap.put(HFBusiDict.RETMSG, Constants.DICTERRKEY + ":" + key );
					return rtnMap;
				}
				
				// 取得args中configKey对应的value值
				String value = paraMessage.getStr(configKey);
				
				if (value == null || value.equals("")) {//如果参数为null或者为空串
					// 如果是必须包含的值，抛出异常。否则continue
					if (isNeeded) {
						ObjectUtil.logInfo(log, "必输参数没有输入值,参数为:%s", configKey);

						String retCode = StringUtil.trim(getCode4DictMap().get(key));
						if(retCode.length() == 0){
							retCode = Constants.RETCODE_MISSINGKEY;
						}

						rtnMap.put(HFBusiDict.RETCODE, retCode);
						rtnMap.put(HFBusiDict.RETMSG, Constants.MISSINGKEY + ":" + key );
						return rtnMap;
					} else {
						continue;
					}
				}
				
				rtnMap.put(configKey, value);
				
				// args中包含该key，进行正则效验
				boolean isOk = false;

				// 取得configKey对应在配置文件中对应的正则表达式
				String regTag = MessageUtil.getValidate("CheckReq." + key);
				if ("".equals(regTag)) {
					// 没有找到配置的正则表达式
					continue;
				} else {			
					// 一个字段在业务服务器中可能用于多种含义的字段，每种规则都进行校验，必须匹配一种规则
					String regs[] = regTag.split("[@]");
					for (String reg : regs) {
						if (value.matches(reg)) {
							isOk = true;
							break;
						}
					}
				}
				if (!isOk) {
					ObjectUtil.logInfo(log, "参数校验未通过，参数为:%s", configKey);
					
					String retCode = StringUtil.trim(getCode4DictMap().get(key));
					if(retCode.length() == 0){
						retCode = Constants.RETCODE_REGEXKEY;
					}
					
					rtnMap.put(HFBusiDict.RETCODE, retCode);
					rtnMap.put(HFBusiDict.RETMSG, Constants.REGEXKEY + ":" + key );
					return rtnMap;
				}
			}
			isPass = true;
		}
		if (isPass){
			log.debug("数据校验通过！");
		} else {
			log.debug("数据校验未通过！");
		}
		return rtnMap;
	}
	
	public static void main(String[] args) {
		System.out.println("pBctP3A06Y10vgGsT/vsLuktcY3Vhihk5x4Xlr9yARTpk6HMgoVS/71hSw3/Fnk2SL7rJLHTNSeDikrsHdDjw7WYbj9rnxX4zk3RjAi7otrKEWdndhi8d7w28DLrexJG12XRg500Pc2O925z/u7enQVdo1wQHEf8xMn/vpFAuoY=".length());
	}

	/**
	 * 接口请求的来源IP校验
	 */
	@Override
	public MpspMessage doIPCheck(MpspMessage paraMessage) {
		MpspMessage rtnMap = new MpspMessage();
		rtnMap.setRetCode0000();
		return rtnMap;
	}

}
