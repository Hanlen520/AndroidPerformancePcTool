package com.xdja.collectdata.handleData;

/**
 *  处理数据的结果
 * @author zlw
 *
 */
public class HandleDataResult{
	// 性能数据是否符合要求 true 时其他元素不填充值，  false时  填充值
	public boolean result;
	// 出现问题时的页面
	public String activityName;
	
	// 保存截图的路径
	public String screenshotsPath;
	// 保存日志的路径
	public String logPath;
	// 保存dump memory trace的路径
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
