package com.xdja.collectdata.handleData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xdja.collectdata.entity.FlowData;
import com.xdja.collectdata.entity.FpsData;
import com.xdja.collectdata.entity.KpiData;
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
	
	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	//����ĬCPU���ݣ��쳣����ģ�ͣ����ش������Ϊ�����������//
	public HandleDataResult[] handleCpuData(float[] cpuData){
		//��Ĭ�������ݵ��ж�//
		boolean result;
		String activityName;
		String screenshotsPath;
		String logPath;
		HandleDataResult[] uploadcpu = {}; //�����쳣CPU���ݵ����飬�Լ���������������Ϊ����ֵ
		HandleDataResult upcpu ;
		float[] totalcpu = {};   //��¼�쳣CPU����
		float[] silenceCPU = cpuData ;  
		for(int i=0;i<silenceCPU.length;i++){
			if (silenceCPU[i]>0.01){
				//�ֻ���������ǰactivity����ǰCPU,traceview�ļ���ֵ����Ϣ��������ֵ/
				totalcpu = insert(totalcpu,silenceCPU[i]);
				//��Ӷ��쳣�����Ĳ���
				result =true;
				activityName = "";
				screenshotsPath= "";
				logPath= "";
				upcpu = new HandleDataResult(result,activityName,screenshotsPath,logPath);
				uploadcpu = inserthandle(uploadcpu, upcpu);
			}
		}
		if (uploadcpu.length==0){
			result = false;
			upcpu =new HandleDataResult(result);
			uploadcpu = inserthandle(uploadcpu, upcpu);
		}
		return uploadcpu;
	}
	
  //insert��̬��һάHandleDataResult�����в���Ԫ��//	
	private static HandleDataResult[] inserthandle(HandleDataResult[] arr, HandleDataResult str)
    {
		int size = arr.length;
		HandleDataResult[] tmp = new HandleDataResult[size + 1];
		System.arraycopy(arr, 0, tmp, 0, size);
		tmp[size] = str;
		arr = null;
		return tmp;
	}
	
	//insert��̬��һά�ַ��������в���Ԫ��//	
		private static float[] insert(float[] arr, float str)
	    {
			int size = arr.length;
			float[] tmp = new float[size + 1];
			System.arraycopy(arr, 0, tmp, 0, size);
			tmp[size] = str;
			arr = null;
			return tmp;
		}
		
	
	/**
	 * 
	 * @param handleCpuAll;���ͨ�ò��Ե�CPU���ж�(�Ǿ�Ĭ����CPU����);
	 * �쳣ģ�ͣ�����⵽������5��CPUռ�þӸߵ�ʱ�����ϱ����ݲ��������Ҳ��жϵ�ǰactivity�Ƿ���ͬһ���棬��Ϊ����̫���ˡ�
	 * @return
	 */
	public HandleDataResult[] handleCpuAll(float[] cpuData){
		boolean result;
		String activityName;
		String screenshotsPath;
		String logPath;
		float[] allcpu;
		allcpu = cpuData;
		HandleDataResult[] uploadcpu = {}; //�����쳣CPU���ݵ����飬�Լ���������������Ϊ����ֵ
		HandleDataResult upcpu ;
		float[] cpuerror ={};//�ռ��쳣CPU����
		for(int i=0;i<allcpu.length;i++){
			if (allcpu[i]>0.5){
				cpuerror = 	insert(cpuerror,allcpu[i]);
				result =true;
				activityName= "";
				screenshotsPath="";
				logPath="";
				upcpu = new HandleDataResult(result,activityName,screenshotsPath,logPath);
				uploadcpu = inserthandle(uploadcpu, upcpu);
			}else if (allcpu.length-i>4){
				if(allcpu[i]>0.2&&allcpu[i+1]>0.2&&allcpu[i+2]>0.2&&allcpu[i+3]>0.2&&allcpu[i+4]>0.2){
					cpuerror = 	insert(cpuerror,allcpu[i]);
					result =true;
					activityName= "";
					screenshotsPath="";
					logPath="";
					upcpu = new HandleDataResult(result,activityName,screenshotsPath,logPath);
					uploadcpu = inserthandle(uploadcpu, upcpu);
				}
			}else if (allcpu.length-i==4&&i>=1){
				int j =allcpu.length;
				if(allcpu[j-1]>0.2&&allcpu[j-2]>0.2&&allcpu[j-3]>0.2&&allcpu[j-4]>0.2&&allcpu[j-5]>0.2){
					cpuerror = 	insert(cpuerror,allcpu[i]);
					result =true;
					activityName= "";
					screenshotsPath="";
					logPath="";
					upcpu = new HandleDataResult(result,activityName,screenshotsPath,logPath);
					uploadcpu = inserthandle(uploadcpu, upcpu);
				}
			}else if (allcpu.length-i==3&&i>=2){
				int j =allcpu.length;
				if(allcpu[j-1]>0.2&&allcpu[j-2]>0.2&&allcpu[j-3]>0.2&&allcpu[j-4]>0.2&&allcpu[j-5]>0.2){
					cpuerror = 	insert(cpuerror,allcpu[i]);
					result =true;
					activityName= "";
					screenshotsPath="";
					logPath="";
					upcpu = new HandleDataResult(result,activityName,screenshotsPath,logPath);
					uploadcpu = inserthandle(uploadcpu, upcpu);
				}
			}else if (allcpu.length-i==2&&i>=3){
				int j =allcpu.length;
				if(allcpu[j-1]>0.2&&allcpu[j-2]>0.2&&allcpu[j-3]>0.2&&allcpu[j-4]>0.2&&allcpu[j-5]>0.2){
					cpuerror = 	insert(cpuerror,allcpu[i]);
					result =true;
					activityName= "";
					screenshotsPath="";
					logPath="";
					upcpu = new HandleDataResult(result,activityName,screenshotsPath,logPath);
					uploadcpu = inserthandle(uploadcpu, upcpu);
				}
			}else if (allcpu.length-i==1&&i>=4){
				int j =allcpu.length;
				if(allcpu[j-1]>0.2&&allcpu[j-2]>0.2&&allcpu[j-3]>0.2&&allcpu[j-4]>0.2&&allcpu[j-5]>0.2){
					cpuerror = 	insert(cpuerror,allcpu[i]);
					result =true;
					activityName= "";
					screenshotsPath="";
					logPath="";
					upcpu = new HandleDataResult(result,activityName,screenshotsPath,logPath);
					uploadcpu = inserthandle(uploadcpu, upcpu);
			    }
		    }  
		}
		if (uploadcpu.length==0){
			result = false;
			upcpu =new HandleDataResult(result);
			uploadcpu = inserthandle(uploadcpu, upcpu);
		}
		return uploadcpu;
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
			memoryList.clear();
			int shakeCount = getShakeCount();
			if (shakeCount > 5) {
				memoryResult = new HandleDataResult(false);
				//��ȡAllocation Info
				return memoryResult;
			}
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
