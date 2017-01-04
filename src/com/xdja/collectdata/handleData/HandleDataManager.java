package com.xdja.collectdata.handleData;

import com.xdja.collectdata.CpuData;
import com.xdja.collectdata.FlowData;
import com.xdja.collectdata.FpsData;
import com.xdja.collectdata.KpiData;
import com.xdja.collectdata.MemoryData;

/**
 *  ����ģ�ͣ����ڶ��ϱ������ݽ��д����ж��Ƿ�������⡣
 * @author zlw
 *
 */
public class HandleDataManager {
	public static HandleDataManager mInstance = null;
	
	private HandleDataManager(){}
	
	public static HandleDataManager getInstance() {
		if (mInstance == null) {
			synchronized (HandleDataManager.class) {
				if (mInstance == null) {
					mInstance = new HandleDataManager();
				}
			}
		}
		return mInstance;
	}
	
	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResult handleCpuData(CpuData cpuData){
		return null;
	}
	
	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResult handleFlowData(FlowData flowData){
		return null;
	}
	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResult handleFpsData(FpsData fpsData){
		return null;
	}
	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResult handleKpiData(KpiData kpiData){
		return null;
	}
	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResult handleMemoryData(MemoryData memoryData){
		return null;
	}
//	/**
//	 * 
//	 * @param cpuData
//	 * @return
//	 */
//	public HandleDataResult handleBatteryData( cpuData){
//		return null;
//	}
}
