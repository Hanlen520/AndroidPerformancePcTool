package com.xdja.collectdata.handleData.entity;

public class CpuHandleResult extends HandleDataResultBase {
	
	// ץȡmethod trace ��path
	public String methodTracePath;
	
	public CpuHandleResult(){
		super();
	}
	
	public CpuHandleResult(boolean result){
		super(result);
	}
	
	public String getMethodTracePath() {
		return methodTracePath;
	}

	public void setMethodTracePath(String methodTracePath) {
		this.methodTracePath = methodTracePath;
	}
	
}
