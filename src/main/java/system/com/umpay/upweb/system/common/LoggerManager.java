package com.umpay.upweb.system.common;

import org.apache.log4j.Logger;

public class LoggerManager {

	/**简要日志*/
	private static final Logger mpspLog = Logger.getLogger("upwebMpspLog");
	
	/**获取简要日志*/
	public static Logger getMpspLogger(){
		return mpspLog;
	}
	
	
}
