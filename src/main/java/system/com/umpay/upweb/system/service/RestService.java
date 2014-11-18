package com.umpay.upweb.system.service;

import java.util.Map;

import com.umpay.upweb.system.model.MpspMessage;
import com.umpay.upweb.system.model.RequestMsg;


/** ******************  类说明  *********************
 * class       :  RestService
 * @author     :  fanxiangchi
 * @version    :  1.0  
 * description :  
 * @see        :                        
 * ************************************************/   
public interface RestService {
	
	/**
	 * ***************** 统一下单  *****************<br>
	 * method name   :  upOrder
	 * @param		 :  @param mpspMsg
	 * @param		 :  @return
	 * @return		 :  MpspMessage
	 * @author       :  fanxiangchi 2014年10月13日 下午2:45:53
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public MpspMessage upOrder(MpspMessage mpspMsg);

	/**
	 * ***************** 下单后的支付  *****************<br>
	 * method name   :  upRestPay
	 * @param		 :  @param mpspMsg
	 * @param		 :  @return
	 * @return		 :  MpspMessage
	 * @author       :  fanxiangchi 2014年10月13日 下午2:44:54
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public MpspMessage upRestPay(MpspMessage mpspMsg);
	
	/**
	 * ***************** 确认支付  *****************<br>
	 * method name   :  confirmRestPay
	 * @param		 :  @param mpspMsg
	 * @param		 :  @return
	 * @return		 :  MpspMessage
	 * @author       :  fanxiangchi 2014年10月13日 下午2:44:37
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public MpspMessage confirmRestPay(MpspMessage mpspMsg);
	
	/**
	 * ***************** 支付结果通知到Rest  *****************<br>
	 * method name   :  payNotifyRest
	 * @param		 :  @param qMap  队列里的Map  包含transeq等字段
	 * @param		 :  @return
	 * @return		 :  MpspMessage
	 * @author       :  fanxiangchi 2014年10月13日 下午2:44:17
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public MpspMessage payNotifyRest(Map<String, String> qMap);
	
	/**
	 * ***************** sdk查单请求  *****************<br>
	 * method name   :  sdkQueryOrder
	 * @param		 :  @param requestMsg
	 * @param		 :  @return
	 * @return		 :  MpspMessage
	 * @author       :  fanxiangchi 2014年10月13日 下午2:44:04
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public MpspMessage sdkQueryOrder(RequestMsg requestMsg);
	/**
	 * ***************** 商户查单请求  *****************<br>
	 * method name   :  merQueryOrder
	 * @param		 :  @param requestMsg
	 * @param		 :  @return
	 * @return		 :  MpspMessage
	 * @author       :  lizhiqiang 2014年11月4日 12:33:05
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public MpspMessage merQueryOrder(RequestMsg requestMsg);
	
	/**
	 * ***************** 转换Map泛型  *****************<br>
	 * method name   :  format2StringMap
	 * @param		 :  @param objectMap
	 * @param		 :  @return
	 * @return		 :  Map<String,String>
	 * @author       :  fanxiangchi 2014年10月13日 下午2:43:32
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public Map<String, String> format2StringMap(Map<String, Object> objectMap);
	
	/**
	 * ***************** WEB下单时的商户验签  *****************<br>
	 * method name   :  checkSign
	 * @return		 :  MpspMessage 参数值
	 * @author       :  fanxiangchi 2014年10月8日 下午5:02:58
	 * **********************************************
	 */
	public MpspMessage checkMerSign(MpspMessage mpspMsg);
	
	/**
		 * ***************** 商户验签  *****************<br>
		 * method name   :  checkMerSign
		 * @return		 :  MpspMessage
		 * @author       :  lizhiqiang 2014年11月5日下午1:48:01
		 * **********************************************
	 */
	public MpspMessage checkMerSign(MpspMessage mpspMsg, String unsignstring);
	
	/**
	 * ***************** WEB下单时的交易鉴权 *****************<br>
	 * method name   :  checkTransNoMo
	 * @return		 :  MpspMessage
	 * @author       :  fanxiangchi 2014年10月8日 下午5:02:58
	 * **********************************************
	 */
	public MpspMessage checkTrans(MpspMessage mpspMsg);
	
	/**
	 * ***************** 获取调用Rest商户验签服务时的加密原串  *****************<br>
	 * method name   :  getUnsignstring
	 * @param		 :  @param requestMsg 商户请求时的验签
	 * @return		 :  String 加密原串    key1=value1&key2=value2的形式
	 * @author       :  fanxiangchi 2014年10月13日 下午2:41:58
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public String getUnsignstring(MpspMessage requestMsg);
	
	/**
	 * ***************** 根据merid goodsid获取商品信息  *****************<br>
	 * method name   :  getGoodsInf
	 * @param		 :  @param merid 商户号
	 * @param		 :  @param goodsid 商品号
	 * @return		 :  MpspMessage 返回的对象   商品信息在这个对象的Map里  获取方式是get方法  key是字典里的  
	 * @author       :  fanxiangchi 2014年10月13日 下午2:39:24
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public MpspMessage getGoodsInf(String merid, String goodsid);
	
	/**
	 * *****************  获取商户信息  *****************<br>
	 * method name   :  getMerInf
	 * @param		 :  @param merid 商户号
	 * @param		 :  @return
	 * @return		 :  MpspMessage
	 * @author       :  fanxiangchi 2014年10月13日 下午8:42:50
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public MpspMessage getMerInf(String merid);
	
	
	/**
	 * *****************  看看是不是小额交易   小额的页面文案不一样  *****************<br>
	 * method name   :  isXEOrder
	 * @param		 :  @param bankid 银行号以XE开头的返回true
	 * @param		 :  @return
	 * @return		 :  boolean
	 * @author       :  fanxiangchi 2014年10月13日 下午2:56:13
	 * description   :  
	 * @see          :  
	 * **********************************************
	 */
	public boolean isXEOrder(String bankid);
	
	public MpspMessage queryUpTrans(String transeq, String funcode);

	

}
