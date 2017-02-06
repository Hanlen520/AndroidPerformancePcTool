package com.xdja.collectdata.handleData;

import java.util.ArrayList;
import java.util.List;

import com.xdja.collectdata.entity.FpsData;
import com.xdja.collectdata.entity.KpiData;
import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.SaveEnvironmentManager;
import com.xdja.collectdata.entity.BatteryData;
import com.xdja.collectdata.entity.CpuData;
import com.xdja.collectdata.entity.MemoryData;
import com.xdja.collectdata.handleData.entity.BatteryHandleResult;
import com.xdja.collectdata.handleData.entity.CpuHandleResult;
import com.xdja.collectdata.handleData.entity.FlowHandleResult;
import com.xdja.collectdata.handleData.entity.FpsHandleResult;
import com.xdja.collectdata.handleData.entity.KpiHandleResult;
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
	// CPU������ó���
	private final static int CPU_MAX = 50;
	private final static int CPU_CONTINUE_MAX = 30;
	private final static float CPU_SLIENT_VALUE = 0.5f;
	private static int cpuCount = 0;
	private static int slientCount = 0;
	// �ڴ�������ó���
	// Ĭ���ڴ����²���������2M
	private final static int MEMORY_STANDARD = 2;
	// �ڴ涶��������3��
	private final static int MEMORY_SHAKECOUNT = 4;
	// kpi�������
	// kpi���ݳ��������ж������⣬��λ��ms
	private final static int KPI_TIME = 2000;

	// flow������� ��λ��MB
	private final static int FLOW_VALUE = 1;
	private final static float FLOW_SLIENT_VALUE = 0.5f;

	// fps�������
	private final static int FPS_COUNT = 40;

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

	// �����ж��ڴ��Ƿ���ڶ�����
	private long memoryInterval = 30 * 1000;
	// ����Ƿ��ڲ����ڴ�
	private boolean memoryTestNow = false;
	// ���ڴ��10s���ռ���memory����
	private List<MemoryData> memoryList = new ArrayList<>(24);

	// ���ڴ��CPU����
	private List<Float> cpuList = new ArrayList<Float>();
	private List<CpuData> slientCpuList = new ArrayList<>(4);
	// ���ڴ��kpi����
	private List<KpiHandleResult> kpiList = new ArrayList<>(12);

	// ���ڴ��fps����
	private List<FpsHandleResult> fpsList = new ArrayList<>(12);

	// �����������ݽ���Ķ���
	private MemoryHandleResult memoryResult = null;
	private KpiHandleResult mKpiHandleResult = null;
	private FpsHandleResult mFpsHandleResult = null;
	private FlowHandleResult mFlowHandleSlientResult = null;
	private FlowHandleResult mFlowHandleResult = null;
	private String mCurTestPackage;
	// �������ó���
	private long lastTime = 0;

	private long nowTime = 0;

	private HandleDataManager() {
		mCurTestPackage = GlobalConfig.getTestPackageName();
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
				// ����ĵ�λ����M
				if (Math.abs(nowData - nextData) > MEMORY_STANDARD) {
					num += 1;
				}
			}
		}
		return num;
	}

	/**
	 * �������������� ����ģ�ͣ���ʱ��ûȷ�������������Ƚ϶࣬��׼���ö�
	 */
	public List<BatteryHandleResult> handleBatteryData(List<BatteryData> batteryDatas) {
		// ���ڴ��Battery����
		List<BatteryHandleResult> batteryList = new ArrayList<>(12);
		BatteryHandleResult batteryHandleResult = null;
		if (batteryDatas == null || batteryDatas.size() < 1) {
			return batteryList;
		}

		for (BatteryData batteryData : batteryDatas) {
			batteryHandleResult = new BatteryHandleResult();
			batteryHandleResult.setResult(true);
			batteryHandleResult.setUid(batteryData.uid);
			batteryHandleResult.setTestValue(String.valueOf(batteryData.batteryValue));
			batteryList.add(batteryHandleResult);
		}

		return batteryList;
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
			} else {
				result = true;
				upcpu = new CpuHandleResult(result);
				upcpu.setTestValue(String.valueOf(cpu));
				upcpu.setActivityName(CollectDataImpl.getCurActivity());
			}

		}

		return upcpu;
	}

	// ����ĬCPU���ݣ��쳣����ģ�ͣ����ش������Ϊ�����������,�޸Ĵ������;//
	// �����Ƕ�����ģ�͵Ĵ����򵥵�����ģ�ͣ�Ĭ��ֻҪCPU�����ݴ���1%������Ϊ���ܴ����쳣;//
	/**
	 *  �߼����Ż�����Ϊ�ж�cpu��Ĭ���߼���Ҫ�Ż�
	 * @param cpuData
	 * @param value
	 * @return
	 */
	public CpuHandleResult handleCpusilence(CpuData cpuData, float value) {
		if (cpuData == null) {
			return null;
		}

		// ��Ĭ�������ݵ��ж�//
		CpuHandleResult upcpu = new CpuHandleResult(true);
		upcpu.setTestValue(String.valueOf(value));

		slientCount += 1;
		if (slientCount == 5) {
			slientCpuList.add(cpuData);
			slientCount = 0;
		}

		int valueCount = 0;
		if (slientCpuList.size() == 4) {
			for (CpuData cpuData2 : slientCpuList) {
				if (cpuData2.cpuUsage > CPU_SLIENT_VALUE) {
					valueCount += 1;
				}
			}

			slientCpuList.clear();
		}

		/**
		 * �������ξ���Ϊ���к�ʱ��Ϊ
		 */
		if (valueCount >= 3) {
			upcpu.setResult(false);
		}else {
			upcpu.setResult(true);
		}

		return upcpu;
	}

	/**
	 * ������������
	 * 
	 * @param flowData
	 * @return
	 */
	public FlowHandleResult handleFlowData(float flowData) {
		mFlowHandleResult = new FlowHandleResult();

		if (flowData > FLOW_VALUE) {
			mFlowHandleResult.setActivityName(CollectDataImpl.getCurActivity());
			mFlowHandleResult.setResult(false);
			mFlowHandleResult.setTestValue(String.valueOf(flowData));
			return mFlowHandleResult;
		}

		mFlowHandleResult.setActivityName(CollectDataImpl.getCurActivity());
		mFlowHandleResult.setResult(true);
		mFlowHandleResult.setTestValue(String.valueOf(flowData));
		return mFlowHandleResult;
	}

	/**
	 * ���ھ�Ĭ����ʱ�������������� ���������Էֿ�������֮�����չ
	 * 
	 * @return
	 */
	public FlowHandleResult handleFlowSlientData(float flowData) {
		mFlowHandleSlientResult = new FlowHandleResult();

		if (flowData > FLOW_SLIENT_VALUE) {
			mFlowHandleSlientResult.setResult(false);
			mFlowHandleSlientResult.setTestValue(String.valueOf(flowData));
			String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
					mCurTestPackage, Constants.TYPE_FLOW);
			mFlowHandleSlientResult.setLogPath(logPath);

			return mFlowHandleSlientResult;
		}

		mFlowHandleSlientResult.setResult(true);
		mFlowHandleSlientResult.setTestValue(String.valueOf(flowData));
		return mFlowHandleSlientResult;
	}

	/**
	 * �Բɼ�����fps���ݽ��д��� �����ж���׼�� ��fps < 40
	 * 
	 * @param cpuData
	 * @return
	 */
	public List<FpsHandleResult> handleFpsData(List<FpsData> fpsDatas) {
		if (fpsDatas == null || fpsDatas.size() < 1) {
			return null;
		}
		for (FpsData fpsData : fpsDatas) {
			mFpsHandleResult = new FpsHandleResult();
			mFpsHandleResult.setActivityName(fpsData.activityName);
			mFpsHandleResult.setTestValue(String.valueOf(fpsData.fps));
			mFpsHandleResult.setDropcount(fpsData.dropcount);
			// �ж��Ƿ�������
			if (fpsData.fps < FPS_COUNT) {

				// ����log
				String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
						mCurTestPackage, Constants.TYPE_KPI);
				// ����method trace
				mFpsHandleResult.setLogPath(logPath);
				mFpsHandleResult.setResult(false);
				mFpsHandleResult.setMethodTracePath("");
				mFpsHandleResult.setMemoryHprofPath("");
				mFpsHandleResult.setPackageName(GlobalConfig.TestPackageName);
				mFpsHandleResult.setVersion(GlobalConfig.TestVersion);
				handleFpsDataInList(mFpsHandleResult);
			} else {
				mFpsHandleResult.setResult(true);
				mFpsHandleResult.setPackageName(GlobalConfig.TestPackageName);
				mFpsHandleResult.setVersion(GlobalConfig.TestVersion);
				handleFpsDataInList(mFpsHandleResult);
			}
		}

		return fpsList;
	}

	/**
	 * ����fps���б��е�����
	 * 
	 * @param handleFpsData
	 */
	private void handleFpsDataInList(FpsHandleResult handleFpsData) {
		if (handleFpsData == null) {
			return;
		}

		if (fpsList.contains(handleFpsData)) {
			int lastFps = Integer.parseInt(fpsList.get(fpsList.indexOf(handleFpsData)).testValue);
			int nowFps = Integer.parseInt(handleFpsData.testValue);
			int curFps = (lastFps + nowFps) / 2;
			if (curFps > FPS_COUNT) {
				handleFpsData.setResult(false);
			} else {
				handleFpsData.setResult(true);
				handleFpsData.setLogPath("");
				handleFpsData.setMemoryHprofPath("");
				handleFpsData.setMethodTracePath("");
			}

			handleFpsData.testValue = String.valueOf(curFps);
		}else {
			fpsList.add(handleFpsData);
		}
	}

	/**
	 * ����kpi��ص����� �ж�����ı�׼�� ҳ�����ʱ�����2s��������ʱ��û�������״�������������
	 * 
	 * @param cpuData
	 * @return
	 */
	public List<KpiHandleResult> handleKpiData(List<KpiData> KpiDatas) {
		if (KpiDatas == null || KpiDatas.size() < 1) {
			return null;
		}

		for (KpiData kpiData : KpiDatas) {
			mKpiHandleResult = new KpiHandleResult();
			mKpiHandleResult.setActivityName(kpiData.currentPage);
			mKpiHandleResult.setTestValue(String.valueOf(kpiData.loadTime));
			// �ж��Ƿ�������
			if (kpiData.loadTime > KPI_TIME) {

				// ����log
				String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
						mCurTestPackage, Constants.TYPE_KPI);
				// ����method trace
				mKpiHandleResult.setLogPath(logPath);
				mKpiHandleResult.setResult(false);
				mKpiHandleResult.setMethodTracePath("");
				handleKpiDataInList(mKpiHandleResult);
			} else {
				mKpiHandleResult.setResult(true);
				handleKpiDataInList(mKpiHandleResult);
			}
		}

		return kpiList;
	}

	/**
	 * ���б����Ѿ����ڵ����ݽ��кϲ�
	 * 
	 * @param kpiData
	 */
	private void handleKpiDataInList(KpiHandleResult kpiData) {
		if (kpiData == null) {
			return;
		}

		if (kpiList.contains(kpiData)) {
			int lastTime = Integer.parseInt(kpiList.get(kpiList.indexOf(kpiData)).testValue);
			int nowTime = Integer.parseInt(kpiData.testValue);
			int curTime = (lastTime + nowTime) / 2;
			if (curTime > KPI_TIME) {
				kpiData.setResult(false);
			} else {
				kpiData.setResult(true);
			}

			kpiData.testValue = String.valueOf(curTime);
		}else {
			kpiList.add(kpiData);
		}
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

	/***
	 * cpu���Ա�����Ի���
	 * 
	 * @param result
	 */
	private CpuHandleResult saveCpuEnvironment(boolean result, double value) {
		String activityName = CollectDataImpl.getCurActivity();
		// String screenshotsPath =
		// SaveEnvironmentManager.getInstance().screenShots(GlobalConfig.DeviceName,
		// Constants.TYPE_CPU);
		String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
				mCurTestPackage, Constants.TYPE_CPU);
		// ��ʱ��ץȡmethod trace����Ӱ��ɼ�����
		// String methodTrace =
		// SaveEnvironmentManager.getInstance().methodTracing(GlobalConfig.DeviceName,
		// GlobalConfig.PackageName, Constants.TYPE_CPU);
		CpuHandleResult handleResult = new CpuHandleResult(result);
		handleResult.setActivityName(activityName);
		handleResult.setLogPath(logPath);
		handleResult.setMethodTracePath("");
		handleResult.setTestValue(String.valueOf(value));
		return handleResult;
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
		// String filePath =
		// SaveEnvironmentManager.getInstance().dumpMemory(GlobalConfig.DeviceName,
		// GlobalConfig.PackageName, Constants.TYPE_MEMORY);
		String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
				mCurTestPackage, Constants.TYPE_MEMORY);
		// String screenPath =
		// SaveEnvironmentManager.getInstance().screenShots(GlobalConfig.DeviceName,
		// Constants.TYPE_MEMORY);
		String activityName = CollectDataImpl.getCurActivity();

		memoryResult.setMemoryHprofPath("");
		memoryResult.setMethodTracePath("");
		memoryResult.setLogPath(logPath);
		memoryResult.setActivityName(activityName);
		memoryResult.setTestValue(String.valueOf(CommonUtil.getTwoDots(testValue)));

		return memoryResult;
	}

}
