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
	// traceMethod Path
	public String methodTracePath;
	
	
	public HandleDataResult(boolean result) {
		super();
		this.result = result;
		// TODO Auto-generated constructor stub
	}
	
	public HandleDataResult(boolean result, String activityName, String screenshotsPath, String logPath,
			String methodTrace) {
		super();
		this.result = result;
		this.activityName = activityName;
		this.screenshotsPath = screenshotsPath;
		this.logPath = logPath;
		this.methodTracePath = methodTrace;
		// TODO Auto-generated constructor stub
	}

	public String getActivityName() {
		return activityName;
	}

	public String getLogPath() {
		return logPath;
	}
	
	public String getMemoryTracePath() {
		return memoryTracePath;
	}
	
	public String getMethodTracePath() {
		return methodTracePath;
	}
	public String getScreenshotsPath() {
		return screenshotsPath;
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
	public void setMemoryTracePath(String memoryTracePath) {
		this.memoryTracePath = memoryTracePath;
	}
	public void setMethodTracePath(String methodTracePath) {
		this.methodTracePath = methodTracePath;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public void setScreenshotsPath(String screenshotsPath) {
		this.screenshotsPath = screenshotsPath;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "result = " + this.result + ", activityName = " + this.activityName + ", screenshotsPath = " + this.screenshotsPath + ", logPath =" + this.logPath;
		
	}
	
}
