package com.xdja.collectdata.handleData.entity;

/**
 *  ����������
 * @author zlw
 *
 */
public class BatteryHandleResult extends HandleDataResultBase {
	
	public int uid;
	public String detailInfo;
	
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

	public String getDetailInfo() {
		return detailInfo;
	}

	public int getUid() {
		return uid;
	}

	public void setDetailInfo(String detailInfo) {
		this.detailInfo = detailInfo;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
	
	
}
