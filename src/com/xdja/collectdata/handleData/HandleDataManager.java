package com.xdja.collectdata.handleData;

import java.util.ArrayList;
import java.util.List;

import com.xdja.collectdata.entity.FlowData;
import com.xdja.collectdata.entity.FpsData;
import com.xdja.collectdata.entity.KpiData;
import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.SaveEnvironmentManager;
import com.xdja.collectdata.entity.CpuData;
import com.xdja.collectdata.entity.MemoryData;
import com.xdja.collectdata.handleData.entity.CpuHandleResult;
import com.xdja.collectdata.handleData.entity.HandleDataResultBase;
import com.xdja.collectdata.handleData.entity.MemoryHandleResult;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.util.CommonUtil;

/**
 * ����ģ�ͣ����ڶ��ϱ������ݽ��д����ж��Ƿ�������⡣
 * 
 * @author zlw
 *
 */
public class HandleDataManager {
	private static HandleDataManager mInstance = null;
	// �����ж��ڴ��Ƿ���ڶ�����
	private long memoryInterval = 30 * 1000;
	// ����Ƿ��ڲ����ڴ�
	private boolean memoryTestNow = false;
	// ���ڴ��10s���ռ���memory����
	private List<MemoryData> memoryList = new ArrayList<>(24);
	private MemoryHandleResult memoryResult = null;
	private List<Float> cpuList = new ArrayList<Float>();// ���ڴ��CPU����

	// CPU������ó���
	private final static int CPU_MAX = 50;
	private final static int CPU_CONTINUE_MAX = 30;
	private static int cpuCount = 0;
	
	// �ڴ�������ó���
	// Ĭ���ڴ����²���������2M
	private final static int MEMORY_STANDARD = 2;
	// �ڴ涶��������3��
	private final static int MEMORY_SHAKECOUNT = 4;
	
	// kpi�������
	// kpi���ݳ��������ж������⣬��λ��ms
	private final static int KPI_TIME = 2000;
	
	
	// �������ó���

	private long lastTime = 0;
	private long nowTime = 0;

	private HandleDataManager() {

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

	// ����ĬCPU���ݣ��쳣����ģ�ͣ����ش������Ϊ�����������,�޸Ĵ������;//
	// �����Ƕ�����ģ�͵Ĵ����򵥵�����ģ�ͣ�Ĭ��ֻҪCPU�����ݴ���1%������Ϊ���ܴ����쳣;//
	public HandleDataResultBase handleCpusilence(CpuData cpuData, double value) {
		// ��Ĭ�������ݵ��ж�//
		boolean result;
		HandleDataResultBase upcpu = null;
		if (cpuData.cpuUsage > 1) {
			upcpu = saveCpuEnvironment(false, value);
		} else {
			result = true;
			upcpu = new HandleDataResultBase(result);
		}

		return upcpu;
	}

	// handleCpu����Ǿ�Ĭ���Ե�����ģ��,ԭ����2�㣺��һ����50%����������������ݴ���30%//
	public CpuHandleResult handleCpu(CpuData cpuData, double cpu) {
		// ��Ĭ�������ݵ��ж�//
		boolean result;
		CpuHandleResult upcpu = null;
		cpuList(cpuData);
		if (cpuData.cpuUsage > CPU_MAX) {
			upcpu = saveCpuEnvironment(false, cpu);
		} else {
			int i = cpuList.size();
			if (i == 3) {
				if (cpuList.get(0) > CPU_CONTINUE_MAX && cpuList.get(1) > CPU_CONTINUE_MAX
						&& cpuList.get(2) > CPU_CONTINUE_MAX) {
					upcpu = saveCpuEnvironment(false, cpu);
				} else {
					result = true;
					upcpu = new CpuHandleResult(result);
					upcpu.setTestValue(String.valueOf(cpu));
					upcpu.setActivityName(CollectDataImpl.getCurActivity());
				}
			}else {
				result = true;
				upcpu = new CpuHandleResult(result);
				upcpu.setTestValue(String.valueOf(cpu));
				upcpu.setActivityName(CollectDataImpl.getCurActivity());
			}

		}
		
		return upcpu;
	}

	/***
	 * cpu���Ա�����Ի���
	 * 
	 * @param result
	 */
	private CpuHandleResult saveCpuEnvironment(boolean result, double value) {
		String activityName = CollectDataImpl.getCurActivity();
//		String screenshotsPath = SaveEnvironmentManager.getInstance().screenShots(GlobalConfig.DeviceName,
//				Constants.TYPE_CPU);
		String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
				GlobalConfig.PackageName, Constants.TYPE_CPU);
		// ��ʱ��ץȡmethod trace����Ӱ��ɼ�����
//		String methodTrace = SaveEnvironmentManager.getInstance().methodTracing(GlobalConfig.DeviceName,
//				GlobalConfig.PackageName, Constants.TYPE_CPU);
		CpuHandleResult handleResult = new CpuHandleResult(result);
		handleResult.setActivityName(activityName);
		handleResult.setLogPath(logPath);
		handleResult.setMethodTracePath("");
		handleResult.setTestValue(String.valueOf(value));
		return handleResult;
	}

	// cpuList�б����������Ԫ�أ�ֱ����ӳ���Ϊ3��Ԫ�غ�ÿ��ֻ�����б�Ԫ�أ�ɾ����һ����������һ�����б�������Ϊ3//
	public void cpuList(CpuData cpuData) {
		cpuCount = cpuCount + 1; // �ڲɼ�Ƶ�ʵķ���������߼��жϼ�����,��Ӽ�ʱ������;�ⲿ�ּ�����Ҳ���Է������߳�ѭ����
		if (cpuCount > 10) {
			cpuCount = 1;
		}
		if (cpuCount == 10) { // �����߳�������߼��жϣ���������ʱִ����ط���
			if (cpuData != null) {
				int i = cpuList.size();
				if (i < 3) {
					cpuList.add(cpuData.cpuUsage);
				} else {
					cpuList.remove(0);
					cpuList.add(cpuData.cpuUsage);
				}
			}
		}
	}

	public HandleDataResultBase handleFlowData(FlowData flowData) {
		return null;
	}

	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResultBase handleFpsData(FpsData fpsData) {
		return null;
	}

	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResultBase handleKpiData(KpiData kpiData) {
		if (kpiData == null) {
			return null;
		}
		
		// �ж��Ƿ�������
		if (kpiData.loadTime > KPI_TIME) {
			
		}
		
		return null;
	}

	/**
	 * ����õ����ڴ����� �ڴ��������ʱ�����¼��� ����չ�� һ���Ե�ǰ�汾���ڴ����ݽ����ж�
	 * 1.�ڴ涶�������ݶ��ı�׼��10s�ڳ���2���ڴ沨����������ֻ��¼�ж�Ϊ�ڴ涶��ʱ��ҳ�档
	 * 2.�ڴ�й¶��ͨ�����߲�̫���ж��ڴ�й¶��׼����LeakCanary + monkey��
	 * 
	 * �����汾��������ݶԱ� 1.ÿ������Ӧ�ú�Heap�ڴ����֮ǰ�汾�ȶ���������ͨ������Ϊ�������µĹ��ܻ��ߴ�����ɵġ�
	 * 2.�ԱȰ汾���ݣ�Heap Alloc�ı仯���󣬵����̵�Dalvik Heap pss
	 * �ڴ��������ӣ�����Ҫ����Ϊ�����˴���С������ɵ��ڴ���Ƭ�� ���������������ʱ�����ܳ���
	 * 
	 * �޸ģ��ɼ����ݵ�ʱ��������5s���ڴ�й¶���ݲɼ���Ϊ30s
	 * 
	 * @param cpuData
	 * @return null ���������������ݲ�����
	 */
	public MemoryHandleResult handleMemoryData(MemoryData memoryData, float value) {
		if (memoryData == null) {
			return null;
		}
		if (!memoryTestNow) {
			lastTime = System.currentTimeMillis();
			memoryTestNow = true;
		}

		nowTime = System.currentTimeMillis();
		/**
		 * �������30s��������������¿�ʼ��
		 */
		if (nowTime - lastTime > memoryInterval) {
			memoryTestNow = false;
			lastTime = 0;
			nowTime = 0;
			int shakeCount = getShakeCount();
			System.out.println("shakeCount = " + shakeCount);
			memoryList.clear();
			if (shakeCount > MEMORY_SHAKECOUNT) {
				memoryResult = saveMemoryEnvironment(false, CommonUtil.getTwoDots(value));
				return memoryResult;
			}
		} else {
			memoryList.add(memoryData);
		}
		
		memoryResult = new MemoryHandleResult(true);
		memoryResult.setTestValue(String.valueOf(CommonUtil.getTwoDots(value)));
		memoryResult.setActivityName(CollectDataImpl.getCurActivity());
		return memoryResult;
	}

	/**
	 * memory���Ա�����
	 * 
	 * @param result
	 * @return
	 */
	private MemoryHandleResult saveMemoryEnvironment(boolean result, float testValue) {
		MemoryHandleResult memoryResult = new MemoryHandleResult(false);
		// dumpsys memory
//		String filePath = SaveEnvironmentManager.getInstance().dumpMemory(GlobalConfig.DeviceName,
//				GlobalConfig.PackageName, Constants.TYPE_MEMORY);
		String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
				GlobalConfig.PackageName, Constants.TYPE_MEMORY);
//		String screenPath = SaveEnvironmentManager.getInstance().screenShots(GlobalConfig.DeviceName,
//				Constants.TYPE_MEMORY);
		String activityName = CollectDataImpl.getCurActivity();

		memoryResult.setMemoryHprofPath("");
		memoryResult.setMethodTracePath("");
		memoryResult.setLogPath(logPath);
		memoryResult.setActivityName(activityName);
		memoryResult.setTestValue(String.valueOf(CommonUtil.getTwoDots(testValue)));

		return memoryResult;
	}

	/**
	 * ��ȡshakeCount�Ĵ���
	 * 
	 * @return
	 */
	private int getShakeCount() {
		int num = 0;
		if (memoryList.size() > 1) {
			for (int i = 0; i < memoryList.size() - 1; i++) {
				float nowData = memoryList.get(i).memAlloc;
				float nextData = memoryList.get(i + 1).memAlloc;
				System.out.println("nextData - nowData= " + (nowData - nextData));
				// ����ĵ�λ����M
				if (Math.abs(nowData - nextData) > MEMORY_STANDARD) {
					num += 1;
				}
			}
		}
		return num;
	}

}
