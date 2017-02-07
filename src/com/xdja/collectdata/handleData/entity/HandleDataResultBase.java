package com.xdja.collectdata.handleData.entity;

import com.xdja.constant.GlobalConfig;

/**
 * �������ݵĽ��
 * ��������
 * 
 * @author zlw
 *
 */
public class HandleDataResultBase {
	// ���Ե�ֵ
	public String testValue;

	// ���������Ƿ����Ҫ�� true ʱ����Ԫ�ز����ֵ�� falseʱ ���ֵ
	public boolean result;
	// ��������ʱ��ҳ��
	public String activityName;
	// ������־��·��
	public String logPath;
	// ����
	public String packageName;
	// �Ƿ���ʾ������Ϣ
	public boolean isShowErrorMsg;
	
	public boolean isShowErrorMsg() {
		return isShowErrorMsg;
	}
	public void setShowErrorMsg(boolean isShowErrorMsg) {
		this.isShowErrorMsg = isShowErrorMsg;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	// �汾��
	public String version;
	
	public HandleDataResultBase() {
		super();
		setPackageName(GlobalConfig.TestPackageName);
		setVersion(GlobalConfig.TestVersion);
		// TODO Auto-generated constructor stub
	}
	public HandleDataResultBase(boolean result){
		this.result = result;
		setPackageName(GlobalConfig.TestPackageName);
		setVersion(GlobalConfig.TestVersion);
	}
	public HandleDataResultBase(String testValue, boolean result, String activityName, String logPath) {
		super();
		this.testValue = testValue;
		this.result = result;
		this.activityName = activityName;
		this.logPath = logPath;
		setPackageName(GlobalConfig.TestPackageName);
		setVersion(GlobalConfig.TestVersion);
	}
	public String getActivityName() {
		return activityName;
	}
	public String getLogPath() {
		return logPath;
	}
	public String getTestValue() {
		return testValue;
	}
	public boolean isResult() {
		return result;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	
	
	public void setResult(boolean result) {
		this.result = result;
	}
	
	public void setTestValue(String testValue) {
		this.testValue = testValue;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "result = " + result + ", testValue = " + testValue + ", activityName = " + activityName + ",logPath =" + logPath;
	}

	
}
