package com.xdja.collectdata.handleData.entity;

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
	public HandleDataResultBase() {
		super();
		// TODO Auto-generated constructor stub
	}
	public HandleDataResultBase(boolean result){
		this.result = result;
	}
	public HandleDataResultBase(String testValue, boolean result, String activityName, String logPath) {
		super();
		this.testValue = testValue;
		this.result = result;
		this.activityName = activityName;
		this.logPath = logPath;
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
