package com.xdja.collectdata.handleData.entity;

/**
 *  ����������
 * @author zlw
 *
 */
public class BatteryHandleResult extends HandleDataResultBase {
	
	public String uid;
	public String detailInfo;
	public String appPackageName; //uid��Ӧ��packageName
	
	public BatteryHandleResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BatteryHandleResult(boolean result) {
		super(result);
		// TODO Auto-generated constructor stub
	}

	public BatteryHandleResult(String testValue, boolean result, String activityName, String logPath) {
		super(testValue, result, activityName, logPath);
		// TODO Auto-generated constructor stub
	}

	public String getAppPackageName() {
		return appPackageName;
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

	public void setDetailInfo(String detailInfo) {
		this.detailInfo = detailInfo;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
}
