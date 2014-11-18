package test.com.umpay.upweb.web.action;

import java.io.Serializable;

public class BSRDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String productid;
	private String linkid;
	private String time;
	private String redurl;
	private String vt;
	private String biz_productid;
	private String fee;
	public String getProductid() {
		return productid;
	}
	public void setProductid(String productid) {
		this.productid = productid;
	}
	public String getLinkid() {
		return linkid;
	}
	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRedurl() {
		return redurl;
	}
	public void setRedurl(String redurl) {
		this.redurl = redurl;
	}
	public String getVt() {
		return vt;
	}
	public void setVt(String vt) {
		this.vt = vt;
	}
	public String getBiz_productid() {
		return biz_productid;
	}
	public void setBiz_productid(String biz_productid) {
		this.biz_productid = biz_productid;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}

	
}
