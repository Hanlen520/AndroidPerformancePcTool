package com.xdja.collectdata.handleData.entity;

// ҳ�����ʱ�䴦����
public class KpiHandleResult extends HandleDataResultBase{
	public String methodTracePath;
	
	public KpiHandleResult(boolean result){
		super(result);
	}
	
	
	public KpiHandleResult() {
		super();
		// TODO Auto-generated constructor stub
	}


	public KpiHandleResult(String testValue, boolean result, String activityName, String logPath) {
		super(testValue, result, activityName, logPath);
		// TODO Auto-generated constructor stub
	}


	public String getMethodTracePath() {
		return methodTracePath;
	}

	public void setMethodTracePath(String methodTracePath) {
		this.methodTracePath = methodTracePath;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		if (activityName != null) {
			return activityName.hashCode();
		}
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		
		return this.activityName.equals(((KpiHandleResult)obj).activityName);
	}
}
