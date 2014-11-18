package com.umpay.upweb.gateway.vo;

import java.io.Serializable;

public class GMIDORequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8333584393174396820L;

	private String userId;
	private String contentId;
	private String consumeCode;
	private String cpid;
	private String hRet;
	private String versionId;
	private String cpparam;
	private String packageID;
	
	public String toString(){
		String obj = "GMIDORequest：userId[%s], contentId[%s], consumeCode[%s], cpid[%s], hRet[%s], versionId[%s], cpparam[%s], packageID[%s]";
		return String.format(obj, userId, contentId, consumeCode, cpid, hRet, versionId, cpparam, packageID);
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getConsumeCode() {
		return consumeCode;
	}
	public void setConsumeCode(String consumeCode) {
		this.consumeCode = consumeCode;
	}
	public String getCpid() {
		return cpid;
	}
	public void setCpid(String cpid) {
		this.cpid = cpid;
	}
	public String gethRet() {
		return hRet;
	}
	public void sethRet(String hRet) {
		this.hRet = hRet;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public String getCpparam() {
		return cpparam;
	}
	public void setCpparam(String cpparam) {
		this.cpparam = cpparam;
	}
	public String getPackageID() {
		return packageID;
	}
	public void setPackageID(String packageID) {
		this.packageID = packageID;
	}
}
