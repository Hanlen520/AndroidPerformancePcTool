package com.xdja.collectdata.handleData.entity;

public class MemoryHandleResult extends HandleDataResultBase {
	
	// ץȡ���ڴ���յ�path
	public String memoryHprofPath;
	// ץȡ�ķ���trace ��path
	public String methodTracePath;
	
	public MemoryHandleResult(boolean result){
		super(result);
	}
	
	public String getMemoryHprofPath() {
		return memoryHprofPath;
	}
	public void setMemoryHprofPath(String memoryHprofPath) {
		this.memoryHprofPath = memoryHprofPath;
	}
	public String getMethodTracePath() {
		return methodTracePath;
	}
	public void setMethodTracePath(String methodTracePath) {
		this.methodTracePath = methodTracePath;
	}
	
}
