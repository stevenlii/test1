package com.umpay.upweb.web.action;

import java.io.Serializable;

public class BSOrderVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8294707367253085400L;
	
	private String merid;
	private String goodsid;
	private String biz_id;
	private String product_id;
	private String order_id;
	private String account_id;
	private String client_type;
	private String order_type;
	private String mobile;
	private String secrtykey;
	private String sign;
	
	public static final String ORDER_GREEN = "1";
	public static final String ORDER_NOT_GREEN = "2";
	
	
	/**
	 * 鏄惁鏄豢鑹茬牬瑙ｇ増    鐮磋В1  闈炵牬瑙�2
	 * 榛樿鐮磋В
	 */
	private String green = "1";
	
	
	public String getGreen() {
		return green;
	}
	public void setGreen(String green) {
		this.green = green;
	}
	public String getMerid() {
		return merid;
	}
	public void setMerid(String merid) {
		this.merid = merid;
	}
	public String getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(String goodsid) {
		this.goodsid = goodsid;
	}
	public String getSecrtykey() {
		return secrtykey;
	}
	public void setSecrtykey(String secrtykey) {
		this.secrtykey = secrtykey;
	}
	public String getBiz_id() {
		return biz_id;
	}
	public void setBiz_id(String biz_id) {
		this.biz_id = biz_id;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getAccount_id() {
		return account_id;
	}
	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}
	public String getClient_type() {
		return client_type;
	}
	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}

	
}
