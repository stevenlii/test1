package com.umpay.upweb.system.model;

import java.util.HashMap;
import java.util.Map;

import com.umpay.hfbusi.HFBusiDict;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.ObjectUtil;

public class MpspMessage {
	protected Map<String,Object> mpspMap;
	
	public MpspMessage(){
		if(mpspMap == null){
			mpspMap = new HashMap<String,Object>();
			mpspMap.put(HFBusiDict.RETCODE, Constants.RETCODE_SYSERR);
		}
	}
	public MpspMessage(Map<String,Object> map){
		mpspMap = map;
		if(map != null && map.get(HFBusiDict.RETCODE) == null){
			mpspMap.put(HFBusiDict.RETCODE, Constants.RETCODE_SYSERR);
		}
	}
	
	public String getStr(String key){
		Object value = get(key);
		if(value == null){
			return null;
		}else{
			if(value instanceof String){
				return (String)value;
			}
//			else if(value instanceof String[]){
//				String[] s = (String[]) value;
//				return s[0];
//			}
			else{
				return value.toString();
			}
		}
	}
	
	public void putAll(MpspMessage message){
		mpspMap.putAll(message.getWrappedMap());
	}
	
	public Object get(String key){
		return mpspMap.get(key);
	}
	
	public Map<String,Object> getWrappedMap(){
		return mpspMap;
	}
	
	public Object put(String key ,Object value){
		return mpspMap.put(key, value);
	}
	
	public String getRetCode(){
		return ObjectUtil.trim(mpspMap.get(HFBusiDict.RETCODE));
	}
	
	public boolean isRetCode0000(){
		return ObjectUtil.trim(mpspMap.get(HFBusiDict.RETCODE)).equals(Constants.RETCODE_SUCCESS);
	}
	
	public void setRetCode(String retCode){
		this.put(HFBusiDict.RETCODE, retCode);
	}
	public void setRetCode0000(){
		setRetCode(Constants.RETCODE_SUCCESS);
	}
	
	public boolean isRetCodeSysError(){
		return ObjectUtil.trim(mpspMap.get(HFBusiDict.RETCODE)).equals(Constants.RETCODE_SYSERR);
	}
	
	public void setRetCodeSysError(){
		setRetCode(Constants.RETCODE_SYSERR);
	}
	
	public String getRetMsg(){
		return ObjectUtil.trim(mpspMap.get(HFBusiDict.RETMSG));
	}
	
	public void setRetMsg(String retMsg){
		this.put(HFBusiDict.RETMSG, retMsg);
	}
}
