package com.xdja.collectdata.entity;

/**
 *  �����������
 * @author zlw
 *
 */
public class BatteryData {
	// 
	public String uid;
	// uid��Ӧ�ĵ���ֵ
	public float batteryValue;
	// ���ڵ�������ϸ��Ϣ
	public String detailInfo;
	
	public BatteryData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BatteryData(String uid, float batteryValue, String detailInfo) {
		super();
		this.uid = uid;
		this.batteryValue = batteryValue;
		this.detailInfo = detailInfo;
	}
	public float getBatteryValue() {
		return batteryValue;
	}
	public String getDetailInfo() {
		return detailInfo;
	}
	public String getUid() {
		return uid;
	}
	public void setBatteryValue(float batteryValue) {
		this.batteryValue = batteryValue;
	}
	public void setDetailInfo(String detailInfo) {
		this.detailInfo = detailInfo;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
}
