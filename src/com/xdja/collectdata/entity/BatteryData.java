package com.xdja.collectdata.entity;

/**
 *  �����������
 * @author zlw
 *
 */
public class BatteryData {
	// uid������������
	public String uid;
	// uid��Ӧ�ĵ���ֵ
	public String batteryValue;
	// ���ڵ�������ϸ��Ϣ
	public String detailInfo;
	
	public String appPackageName; //uid��Ӧ�İ���
	public BatteryData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BatteryData(String uid, String batteryValue, String detailInfo) {
		super();
		this.uid = uid;
		this.batteryValue = batteryValue;
		this.detailInfo = detailInfo;
	}
	
	public String getAppPackageName() {
		return appPackageName;
	}
	public String getBatteryValue() {
		return batteryValue;
	}
	public String getDetailInfo() {
		return detailInfo;
	}
	public String getUid() {
		return uid;
	}
	public void setAppPackageName(String appPackageName) {
		this.appPackageName = appPackageName;
	}
	public void setBatteryValue(String batteryValue) {
		this.batteryValue = batteryValue;
	}
	public void setDetailInfo(String detailInfo) {
		this.detailInfo = detailInfo;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
}
