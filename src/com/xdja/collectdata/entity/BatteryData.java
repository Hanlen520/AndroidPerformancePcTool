package com.xdja.collectdata.entity;

/**
 *  �����������
 * @author zlw
 *
 */
public class BatteryData {
	// 
	public int uid;
	// uid��Ӧ�ĵ���ֵ
	public float batteryValue;
	// ���ڵ�������ϸ��Ϣ
	public String detailInfo;
	public BatteryData(int uid, float batteryValue, String detailInfo) {
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
	public int getUid() {
		return uid;
	}
	public void setBatteryValue(float batteryValue) {
		this.batteryValue = batteryValue;
	}
	public void setDetailInfo(String detailInfo) {
		this.detailInfo = detailInfo;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	
	
}
