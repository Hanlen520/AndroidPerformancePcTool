package com.xdja.collectdata.handleData;

/**
 *  �������ݵĽ��
 * @author zlw
 *
 */
public class HandleDataResult{
	// ���������Ƿ����Ҫ�� true ʱ����Ԫ�ز����ֵ��  falseʱ  ���ֵ
	public boolean result;
	// ��������ʱ��ҳ��
	public String activityName;
	
	// �����ͼ��·��
	public String screenshotsPath;
	// ������־��·��
	public String logPath;
	// ����dump memory trace��·��
	public String memoryTracePath;
	
	
	public HandleDataResult(boolean result, String activityName, String screenshotsPath, String logPath) {
		super();
		this.result = result;
		this.activityName = activityName;
		this.screenshotsPath = screenshotsPath;
		this.logPath = logPath;
		// TODO Auto-generated constructor stub
	}
	
	public HandleDataResult(boolean result) {
		super();
		this.result = result;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "result = " + this.result + ", activityName = " + this.activityName + ", screenshotsPath = " + this.screenshotsPath + ", logPath =" + this.logPath;
		
	}
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getScreenshotsPath() {
		return screenshotsPath;
	}
	public void setScreenshotsPath(String screenshotsPath) {
		this.screenshotsPath = screenshotsPath;
	}
	public String getLogPath() {
		return logPath;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	public String getMemoryTracePath() {
		return memoryTracePath;
	}
	public void setMemoryTracePath(String memoryTracePath) {
		this.memoryTracePath = memoryTracePath;
	}
	
}
