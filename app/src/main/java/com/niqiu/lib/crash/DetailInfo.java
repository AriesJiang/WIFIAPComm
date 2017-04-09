package com.niqiu.lib.crash;

public class DetailInfo {
	String BOARD;
	String BRAND; // android系统定制商
	String CPU_ABI;
	String CPU_ABI2;
	
	String DEVICE; //设备参数
	String DISPLAY;
	String FINGERPRINT; // 硬件名称
	
	String ID; //修订版本列表
	String MANUFACTURER;
	
	String MODEL; // 版本
	String PRODUCT; // 手机制造商 
	String TAGS; // 描述build的标签
	
	String USER;
	String PRODUCTMODEL;  //设备名称 MX4 Pro
	String SDK_INT;  // 19
	String RELEASE;   // 4.4.4
	
	String DISPLAYMETRICS; //屏幕分辨率
	
	
	public String getBOARD() {
		return BOARD;
	}
	public void setBOARD(String bOARD) {
		BOARD = bOARD;
	}
	public String getBRAND() {
		return BRAND;
	}
	public void setBRAND(String bRAND) {
		BRAND = bRAND;
	}
	public String getCPU_ABI() {
		return CPU_ABI;
	}
	public void setCPU_ABI(String cPU_ABI) {
		CPU_ABI = cPU_ABI;
	}
	public String getCPU_ABI2() {
		return CPU_ABI2;
	}
	public void setCPU_ABI2(String cPU_ABI2) {
		CPU_ABI2 = cPU_ABI2;
	}
	public String getDEVICE() {
		return DEVICE;
	}
	public void setDEVICE(String dEVICE) {
		DEVICE = dEVICE;
	}
	public String getDISPLAY() {
		return DISPLAY;
	}
	public void setDISPLAY(String dISPLAY) {
		DISPLAY = dISPLAY;
	}
	public String getFINGERPRINT() {
		return FINGERPRINT;
	}
	public void setFINGERPRINT(String fINGERPRINT) {
		FINGERPRINT = fINGERPRINT;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getMANUFACTURER() {
		return MANUFACTURER;
	}
	public void setMANUFACTURER(String mANUFACTURER) {
		MANUFACTURER = mANUFACTURER;
	}
	public String getMODEL() {
		return MODEL;
	}
	public void setMODEL(String mODEL) {
		MODEL = mODEL;
	}
	public String getPRODUCT() {
		return PRODUCT;
	}
	public void setPRODUCT(String pRODUCT) {
		PRODUCT = pRODUCT;
	}
	public String getTAGS() {
		return TAGS;
	}
	public void setTAGS(String tAGS) {
		TAGS = tAGS;
	}
	public String getUSER() {
		return USER;
	}
	public void setUSER(String uSER) {
		USER = uSER;
	}
	
	public String getPRODUCTMODEL() {
		return PRODUCTMODEL;
	}
	public void setPRODUCTMODEL(String pRODUCTMODEL) {
		PRODUCTMODEL = pRODUCTMODEL;
	}
	public String getSDK_INT() {
		return SDK_INT;
	}
	public void setSDK_INT(String sDK_INT) {
		SDK_INT = sDK_INT;
	}
	public String getRELEASE() {
		return RELEASE;
	}
	public void setRELEASE(String rELEASE) {
		RELEASE = rELEASE;
	}
	
	public String getDISPLAYMETRICS() {
		return DISPLAYMETRICS;
	}
	public void setDISPLAYMETRICS(String dISPLAYMETRICS) {
		DISPLAYMETRICS = dISPLAYMETRICS;
	}
	
	
	@Override
	public String toString() {
		return BOARD+"\n"+BRAND+"\n"+CPU_ABI+"\n"+CPU_ABI2+"\n"+DEVICE+"\n"
				+DISPLAY+"\n"+FINGERPRINT+"\n"+ID+"\n"+MANUFACTURER+"\n"
				+MODEL+"\n"+PRODUCT+"\n"+TAGS+"\n"+USER+"\n";
	}
	
	
}
