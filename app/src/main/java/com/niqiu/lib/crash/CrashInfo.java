package com.niqiu.lib.crash;

public class CrashInfo {
	
	String userIdentityCode;
	String info;
	String level;
	String product;
	String supportEmail;
	String environment;
	String mtime;
	
	public String getUserIdentityCode() {
		return userIdentityCode;
	}
	public void setUserIdentityCode(String userIdentityCode) {
		this.userIdentityCode = userIdentityCode;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getSupportEmail() {
		return supportEmail;
	}
	public void setSupportEmail(String supportEmail) {
		this.supportEmail = supportEmail;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getMtime() {
		return mtime;
	}
	public void setMtime(String mtime) {
		this.mtime = mtime;
	}
}
