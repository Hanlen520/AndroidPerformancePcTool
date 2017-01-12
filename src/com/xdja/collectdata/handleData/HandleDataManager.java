package com.xdja.collectdata.handleData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xdja.collectdata.entity.FlowData;
import com.xdja.collectdata.entity.FpsData;
import com.xdja.collectdata.entity.KpiData;
import com.xdja.collectdata.entity.CpuData;
import com.xdja.collectdata.entity.MemoryData;
import com.xdja.collectdata.handleData.HandleDataResult;

/**
 *  ����ģ�ͣ����ڶ��ϱ������ݽ��д����ж��Ƿ�������⡣
 * @author zlw
 *
 */
public class HandleDataManager {
	private static HandleDataManager mInstance = null;
	// �����ж��ڴ��Ƿ���ڶ�����
	private long memoryInterval = 10 * 1000;
	// ��¼�ڴ�䶯�Ĵ���
	private int memoryShakeCount = 0;
	// ����Ƿ��ڲ����ڴ�
	private boolean memoryTestNow = false;
	//���ڴ��10s���ռ���memory����
	private List<MemoryData> memoryList = new ArrayList<>(24);
	private HandleDataResult memoryResult = null;
	private List<Float> cpuList = new ArrayList<Float>();//���ڴ��CPU����
    private HandleDataManager(){
		
	}
	
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
	
	//����ĬCPU���ݣ��쳣����ģ�ͣ����ش������Ϊ�����������,�޸Ĵ������;//
	//�����Ƕ�����ģ�͵Ĵ����򵥵�����ģ�ͣ�Ĭ��ֻҪCPU�����ݴ���1%������Ϊ���ܴ����쳣;//
	public HandleDataResult handleCpusilence(CpuData cpuData){
		//��Ĭ�������ݵ��ж�//
		boolean result;
		String activityName;
		String screenshotsPath;
		String logPath;
		HandleDataResult upcpu = null;
		if (cpuData.cpuUsage>1){
				result =false;
				activityName = "";
				screenshotsPath= "";
				logPath= "";
				upcpu =new HandleDataResult(result, activityName, screenshotsPath,logPath);
			}else{
				result =true;
				upcpu =new HandleDataResult(result);
			}
	
		return upcpu;
	}
	
	//handleCpu����Ǿ�Ĭ���Ե�����ģ��,ԭ����2�㣺��һ����50%����������������ݴ���30%//
	public HandleDataResult handleCpu(CpuData cpuData){
		//��Ĭ�������ݵ��ж�//
		boolean result;
		String activityName;
		String screenshotsPath;
		String logPath;
		HandleDataResult upcpu = null;
		if (cpuData.cpuUsage>50){
				result =false;
				activityName = "";
				screenshotsPath= "";
				logPath= "";
				upcpu =new HandleDataResult(result, activityName, screenshotsPath,logPath);
			}else{
				int i= cpuList.size();
				if (i==3){
					if(cpuList.get(0)>30&&cpuList.get(1)>30&&cpuList.get(2)>30){
						result =false;
						activityName = "";
						screenshotsPath= "";
						logPath= "";
						upcpu =new HandleDataResult(result, activityName, screenshotsPath,logPath);
					}else{
						result =true;
				        upcpu =new HandleDataResult(result);
					}
				}
				
			}
	
		return upcpu;
	}
	
	//cpuList�б����������Ԫ�أ�ֱ����ӳ���Ϊ3��Ԫ�غ�ÿ��ֻ�����б�Ԫ�أ�ɾ����һ����������һ�����б�������Ϊ3//
	public List<Float> cpuList (CpuData cpuData){
		if (cpuData!=null){
			int i= cpuList.size();
			if (i<3){
				cpuList.add(cpuData.cpuUsage);
			}else{
				cpuList.remove(0);
				cpuList.add(cpuData.cpuUsage);
			}
		}
		return cpuList;
	}
	
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
	 * ����õ����ڴ�����
	 * �ڴ��������ʱ�����¼��� ����չ��
	 * һ���Ե�ǰ�汾���ڴ����ݽ����ж�
	 * 1.�ڴ涶�������ݶ��ı�׼��10s�ڳ���5���ڴ沨����������ֻ��¼�ж�Ϊ�ڴ涶��ʱ��ҳ�档
	 * 2.�ڴ�й¶��ͨ�����߲�̫���ж��ڴ�й¶��׼����LeakCanary + monkey��
	 * 
	 * �����汾��������ݶԱ�
	 * 1.ÿ������Ӧ�ú�Heap�ڴ����֮ǰ�汾�ȶ���������ͨ������Ϊ�������µĹ��ܻ��ߴ�����ɵġ�
	 * 2.�ԱȰ汾���ݣ�Heap Alloc�ı仯���󣬵����̵�Dalvik Heap pss �ڴ��������ӣ�����Ҫ����Ϊ�����˴���С������ɵ��ڴ���Ƭ��
	 * ���������������ʱ�����ܳ���
	 * 
	 * 
	 * @param cpuData
	 * @return null ���������������ݲ�����
	 */
	public HandleDataResult handleMemoryData(MemoryData memoryData){
		if (memoryData == null) {
			return null;
		}
		long lastTime = 0;
		long nowTime = 0;
		if (!memoryTestNow) {
			lastTime = System.currentTimeMillis();
			memoryTestNow = true;
		}
		
		nowTime = System.currentTimeMillis();
		/**
		 * �������10s��������������¿�ʼ��
		 */
		if (nowTime - lastTime > memoryInterval) {
			memoryTestNow = false;
			lastTime = 0;
			nowTime = 0;
			int shakeCount = getShakeCount();
			if (shakeCount > 5) {
				memoryResult = new HandleDataResult(false);
				//��ȡAllocation Info
				return memoryResult;
			}
			memoryList.clear();
		}else {
			memoryList.add(memoryData);
		}
		memoryResult = new HandleDataResult(true);
		return memoryResult;
	}
	
	/**
	 *  ��ȡshakeCount�Ĵ���
	 * @return
	 */
	private int getShakeCount(){
		int num = 0;
		if (memoryList.size() > 1) {
			for(int i=0; i< memoryList.size() -1; i++){
				float nowData = memoryList.get(i).memAlloc;
				float nextData = memoryList.get(i + 1).memAlloc;
				// ����ĵ�λ����M
				if (Math.abs(nowData - nextData) > 2) {
					num += 1;
				}
			}
		}
		return num;
	}
	
}
