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
	
	
	public HandleDataResult() {
		super();
		// TODO Auto-generated constructor stub
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
