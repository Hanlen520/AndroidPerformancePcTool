package com.xdja.collectdata.handleData.entity;

public class MemoryHandleResult extends HandleDataResultBase {
	
	// ץȡ���ڴ���յ�path
	public String memoryHprofPath;
	// ץȡ�ķ���trace ��path
	public String methodTracePath;
	// �ڴ�ֵ�Ƿ񳬹����ֵ
	public boolean mIsErrorMemory;
	
	public boolean ismIsErrorMemory() {
		return mIsErrorMemory;
	}

	public void setmIsErrorMemory(boolean mIsErrorMemory) {
		this.mIsErrorMemory = mIsErrorMemory;
	}

	public MemoryHandleResult(){
		super();
	}
	
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
	
	@Override
	public String[] formatDataToArray() {
		// TODO Auto-generated method stub
		String resulutStr = result ? "true" : "false";
		String[] dataArray = new String[]{activityName, testValue, logPath, memoryHprofPath, resulutStr};
		return dataArray;
	}
}
