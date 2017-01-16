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
import com.xdja.collectdata.handleData.HandleDataResult;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;

/**
 * ����ģ�ͣ����ڶ��ϱ������ݽ��д����ж��Ƿ�������⡣
 * 
 * @author zlw
 *
 */
public class HandleDataManager {
	private static HandleDataManager mInstance = null;
	// �����ж��ڴ��Ƿ���ڶ�����
	private long memoryInterval = 10 * 1000;
	// ����Ƿ��ڲ����ڴ�
	private boolean memoryTestNow = false;
	// ���ڴ��10s���ռ���memory����
	private List<MemoryData> memoryList = new ArrayList<>(24);
	private HandleDataResult memoryResult = null;
	private List<Float> cpuList = new ArrayList<Float>();// ���ڴ��CPU����

	// CPU������ó���
	private final static int CPU_MAX = 50;
	private final static int CPU_CONTINUE_MAX = 30;

	// �ڴ�������ó���
	// Ĭ���ڴ����²���������2M
	private final static int MEMORY_STANDARD = 2;
	// �ڴ涶��������2��
	private final static int MEMORY_SHAKECOUNT = 2;
	
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
	public HandleDataResult handleCpusilence(CpuData cpuData) {
		// ��Ĭ�������ݵ��ж�//
		boolean result;
		HandleDataResult upcpu = null;
		if (cpuData.cpuUsage > 1) {
			upcpu = saveCpuEnvironment(false);
		} else {
			result = true;
			upcpu = new HandleDataResult(result);
		}

		return upcpu;
	}

	// handleCpu����Ǿ�Ĭ���Ե�����ģ��,ԭ����2�㣺��һ����50%����������������ݴ���30%//
	public HandleDataResult handleCpu(CpuData cpuData) {
		// ��Ĭ�������ݵ��ж�//
		boolean result;
		HandleDataResult upcpu = null;
		cpuList(cpuData);
		if (cpuData.cpuUsage > CPU_MAX) {
			upcpu = saveCpuEnvironment(false);
		} else {
			int i = cpuList.size();
			if (i == 3) {
				if (cpuList.get(0) > CPU_CONTINUE_MAX && cpuList.get(1) > CPU_CONTINUE_MAX
						&& cpuList.get(2) > CPU_CONTINUE_MAX) {
					upcpu = saveCpuEnvironment(false);
				} else {
					result = true;
					upcpu = new HandleDataResult(result);
				}
			}

		}

		return upcpu;
	}

	/***
	 * cpu���Ա�����Ի���
	 * 
	 * @param result
	 */
	private HandleDataResult saveCpuEnvironment(boolean result) {
		String activityName = CollectDataImpl.getCurActivity();
		String screenshotsPath = SaveEnvironmentManager.getInstance().screenShots(GlobalConfig.DeviceName,
				Constants.TYPE_CPU);
		String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
				GlobalConfig.PackageName, Constants.TYPE_CPU);
		String methodTrace = SaveEnvironmentManager.getInstance().methodTracing(GlobalConfig.DeviceName,
				GlobalConfig.PackageName, Constants.TYPE_CPU);
		HandleDataResult handleResult = new HandleDataResult(result);
		handleResult.setActivityName(activityName);
		handleResult.setLogPath(logPath);
		handleResult.setMethodTracePath(methodTrace);
		handleResult.setScreenshotsPath(screenshotsPath);
		return handleResult;
	}

	// cpuList�б����������Ԫ�أ�ֱ����ӳ���Ϊ3��Ԫ�غ�ÿ��ֻ�����б�Ԫ�أ�ɾ����һ����������һ�����б�������Ϊ3//
	public List<Float> cpuList(CpuData cpuData) {
		if (cpuData != null) {
			int i = cpuList.size();
			if (i < 3) {
				cpuList.add(cpuData.cpuUsage);
			} else {
				cpuList.remove(0);
				cpuList.add(cpuData.cpuUsage);
			}
		}
		return cpuList;
	}

	public HandleDataResult handleFlowData(FlowData flowData) {
		return null;
	}

	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResult handleFpsData(FpsData fpsData) {
		return null;
	}

	/**
	 * 
	 * @param cpuData
	 * @return
	 */
	public HandleDataResult handleKpiData(KpiData kpiData) {
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
	public HandleDataResult handleMemoryData(MemoryData memoryData) {
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
				memoryResult = saveMemoryEnvironment(false);
				return memoryResult;
			}
		} else {
			memoryList.add(memoryData);
		}
		memoryResult = new HandleDataResult(true);
		return memoryResult;
	}

	/**
	 * memory���Ա�����
	 * 
	 * @param result
	 * @return
	 */
	private HandleDataResult saveMemoryEnvironment(boolean result) {
		HandleDataResult memoryResult = new HandleDataResult(false);
		// dumpsys memory
		String filePath = SaveEnvironmentManager.getInstance().dumpMemory(GlobalConfig.DeviceName,
				GlobalConfig.PackageName, Constants.TYPE_MEMORY);
		System.out.println("filePath = " + filePath);
		String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
				GlobalConfig.PackageName, Constants.TYPE_MEMORY);
		System.out.println("logPath = " + logPath);
		String screenPath = SaveEnvironmentManager.getInstance().screenShots(GlobalConfig.DeviceName,
				Constants.TYPE_MEMORY);
		System.out.println("screenPath = " + screenPath);
		String activityName = CollectDataImpl.getCurActivity();
		System.out.println("save environment finish");
		
		memoryResult.setMemoryTracePath(filePath);
		memoryResult.setLogPath(logPath);
		memoryResult.setScreenshotsPath(screenPath);
		memoryResult.setActivityName(activityName);

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
