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
	private final static int FLOW_VALUE = 1024;
	private final static float FLOW_SLIENT_VALUE = 512;

	// fps�������
	private final static int FPS_COUNT = 40;

	// ��Ŵ��������б�
	private List<KpiHandleResult> kpiErrorList = new ArrayList<>(12);
	private List<CpuHandleResult> cpuErrorList = new ArrayList<>(12);
	private List<FlowHandleResult> flowErrorList = new ArrayList<>(12);
	private List<MemoryHandleResult> memoryErrorList = new ArrayList<>(12);

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
	private List<KpiData> kpidataList = new ArrayList<>(12);

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
	public List<BatteryHandleResult> handleBatteryData(List<BatteryData> batteryDatas, String packageName,
			String version) {
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
			batteryHandleResult.setPackageName(packageName);
			batteryHandleResult.setVersion(version);
			batteryHandleResult.setAppPackageName(batteryData.appPackageName);
			batteryHandleResult.setDetailInfo(batteryData.detailInfo);
			batteryList.add(batteryHandleResult);
		}

		return batteryList;
	}

	/**
	 * handleCpu����Ǿ�Ĭ���Ե�����ģ��,ԭ����2�㣺��һ����50%����������������ݴ���30%
	 * 
	 * @param cpuData
	 * @param cpu
	 * @return
	 */
	public CpuHandleResult handleCpu(CpuData cpuData, double cpu) {
		cpuList(cpuData);
		CpuHandleResult handleResult = new CpuHandleResult();
		handleResult.setTestValue(String.valueOf(cpu));
		handleResult.setActivityName(CollectDataImpl.getCurActivity());
		if (cpuData.cpuUsage > CPU_MAX) {

			// ���ò��Խ��
			handleResult.setResult(false);
			// �жϴ����б����Ƿ����
			if (cpuErrorList.contains(handleResult)) {
				return handleResult;
			}

			// ������Ҫ��ʾ������Ϣ
			handleResult.setShowErrorMsg(true);
			// ������Ի���
			handleResult = saveCpuEnvironment(handleResult);

			// �����ھ���ӵ�ErrorList��
			cpuErrorList.add(handleResult);

		} else {
			int i = cpuList.size();
			if (i == 3) {
				if (cpuList.get(0) > CPU_CONTINUE_MAX && cpuList.get(1) > CPU_CONTINUE_MAX
						&& cpuList.get(2) > CPU_CONTINUE_MAX) {
					// ���ò��Խ��
					handleResult.setResult(false);
					// �жϴ����б����Ƿ����
					if (cpuErrorList.contains(handleResult)) {
						return handleResult;
					}

					// ������Ҫ��ʾ������Ϣ
					handleResult.setShowErrorMsg(true);
					// ������Ի���
					handleResult = saveCpuEnvironment(handleResult);

					// �����ھ���ӵ�ErrorList��
					cpuErrorList.add(handleResult);
				} else {
					// ���ò��Խ��
					handleResult.setResult(true);
					handleResult.setShowErrorMsg(false);
				}
			} else {
				// ���ò��Խ��
				handleResult.setResult(true);
				handleResult.setShowErrorMsg(false);
			}
		}
		return handleResult;
	}

	// ����ĬCPU���ݣ��쳣����ģ�ͣ����ش������Ϊ�����������,�޸Ĵ������;//
	// �����Ƕ�����ģ�͵Ĵ����򵥵�����ģ�ͣ�Ĭ��ֻҪCPU�����ݴ���1%������Ϊ���ܴ����쳣;//
	/**
	 * �߼����Ż�����Ϊ�ж�cpu��Ĭ���߼���Ҫ�Ż�
	 * 
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
			// ���ò��Խ��
			upcpu.setResult(false);
			// �жϴ����б����Ƿ����
			if (cpuErrorList.contains(upcpu)) {
				return upcpu;
			}

			// ������Ҫ��ʾ������Ϣ
			upcpu.setShowErrorMsg(true);
			// ������Ի���
			upcpu = saveCpuEnvironment(upcpu);

			// �����ھ���ӵ�ErrorList��
			cpuErrorList.add(upcpu);
		} else {
			// ���ò��Խ��
			upcpu.setResult(true);
			upcpu.setShowErrorMsg(false);
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
		// ���ù�����ֵ
		mFlowHandleResult.setActivityName(CollectDataImpl.getCurActivity());
		mFlowHandleResult.setTestValue(String.valueOf(flowData));
		if (flowData > FLOW_VALUE) {

			mFlowHandleResult.setResult(false);
			if (flowErrorList.contains(mFlowHandleResult)) {
				return mFlowHandleResult;
			}

			// ������Ի���
			String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
					mCurTestPackage, Constants.TYPE_FLOW);
			mFlowHandleSlientResult.setLogPath(logPath);

			// ������ʾ������Ϣ
			mFlowHandleResult.setShowErrorMsg(true);
			flowErrorList.add(mFlowHandleResult);

			return mFlowHandleResult;
		}

		mFlowHandleResult.setResult(true);
		return mFlowHandleResult;
	}

	/**
	 * ���ھ�Ĭ����ʱ�������������� ���������Էֿ�������֮�����չ
	 * 
	 * @return
	 */
	public FlowHandleResult handleFlowSlientData(float flowData) {
		mFlowHandleResult = new FlowHandleResult();
		// ���ù�����ֵ
		mFlowHandleResult.setActivityName(CollectDataImpl.getCurActivity());
		mFlowHandleResult.setTestValue(String.valueOf(flowData));
		if (flowData > FLOW_SLIENT_VALUE) {

			mFlowHandleResult.setResult(false);
			if (flowErrorList.contains(mFlowHandleResult)) {
				return mFlowHandleResult;
			}

			// ������Ի���
			String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
					mCurTestPackage, Constants.TYPE_FLOW);
			mFlowHandleSlientResult.setLogPath(logPath);

			// ������ʾ������Ϣ
			mFlowHandleResult.setShowErrorMsg(true);
			flowErrorList.add(mFlowHandleResult);

			return mFlowHandleResult;
		}

		mFlowHandleResult.setResult(true);
		return mFlowHandleResult;
	}

	/**
	 * �Բɼ�����fps���ݽ��д��� �����ж���׼�� ��fps < 40
	 * 
	 * @param cpuData
	 * @return
	 */
	public FpsHandleResult handleFpsData(FpsData fpsData) {
		if (fpsData == null) {
			return null;
		}
		// ����һ���������
		mFpsHandleResult = new FpsHandleResult();
		mFpsHandleResult.setActivityName(fpsData.activityName);
		mFpsHandleResult.setTestValue(String.valueOf(fpsData.fps));
		mFpsHandleResult.setDropCount(fpsData.dropcount);
		mFpsHandleResult.setFrameCount(fpsData.framecount);
		mFpsHandleResult.setPackageName(GlobalConfig.TestPackageName);
		mFpsHandleResult.setVersion(GlobalConfig.TestVersion);

		// �ж��Ƿ�������
		if (fpsData.fps < FPS_COUNT) {
			mFpsHandleResult.setResult(false);
		} else {
			mFpsHandleResult.setResult(true);
		}
		return mFpsHandleResult;
	}

	/**
	 * ����kpi��ص����� �ж�����ı�׼�� ҳ�����ʱ�����2s��������ʱ��û�������״�������������
	 * �����������Ŀǰ�ķ���Ч�ʵ͡���ò��û�õĽ�������ˡ�
	 * @param cpuData
	 * @return
	 */
	public List<KpiHandleResult> handleKpiData(List<KpiData> KpiDatas) {
		// ���Ƚ����е�ֵ����¼����
		kpidataList.addAll(KpiDatas);
		// �����ظ�����
		KpiDatas = handleKpiHandleList(kpidataList);
		
		// ���Klist
		kpiList.clear();
		if (KpiDatas == null || KpiDatas.size() < 1) {
			return kpiList;
		}

		for (KpiData kpiData : KpiDatas) {
			mKpiHandleResult = new KpiHandleResult();
			mKpiHandleResult.setActivityName(kpiData.currentPage);
			mKpiHandleResult.setTestValue(String.valueOf(kpiData.loadTime));
			// �ж��Ƿ�������
			if (kpiData.loadTime > KPI_TIME) {
				// ��¼�Ƿ�չʾ������Ϣ
				if (kpiErrorList.contains(mKpiHandleResult)) {
					continue;
				}
				mKpiHandleResult.setShowErrorMsg(true);
				kpiErrorList.add(mKpiHandleResult);

				// ����log
				String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
						mCurTestPackage, Constants.TYPE_KPI);
				// ����method trace
				mKpiHandleResult.setLogPath(logPath);
				mKpiHandleResult.setResult(false);
				mKpiHandleResult.setMethodTracePath("");

			} else {
				mKpiHandleResult.setResult(true);
			}

			kpiList.add(mKpiHandleResult);
		}

		return kpiList;
	}

	/**
	 * �������б����ظ���Ԫ�أ�ȡƽ��ֵ
	 * 
	 */
	private List<KpiData> handleKpiHandleList(List<KpiData> KpiDatas) {
		if (KpiDatas == null || KpiDatas.size() < 1) {
			return null;
		}
		KpiData kpiData = null, kpiData2 = null;
		List<KpiData> tempKpiData = new ArrayList<>(12);
		int count = 1;
		int kpi = 0;
		for (int i = 0; i < KpiDatas.size(); i++) {
			kpiData = KpiDatas.get(i);
			if (kpiData == null) {
				continue;
			}
			kpi += kpiData.loadTime;

			for (int j = i + 1; j < KpiDatas.size(); j++) {
				kpiData2 = KpiDatas.get(j);
				if (kpiData2 == null) {
					continue;
				}

				if (kpiData.equals(kpiData2)) {
					count += 1;
					kpi += kpiData2.loadTime;
					KpiDatas.remove(kpiData2);
				}
				
			}
			kpi = kpi / count;
			kpiData.setLoadTime(kpi);
			if (!tempKpiData.contains(kpiData)) {
				tempKpiData.add(kpiData);
			}
			kpi = 0;
			count = 1;
		}
		
		
		return tempKpiData;
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
		memoryResult = new MemoryHandleResult();
		// ���ù�����ֵ
		memoryResult.setTestValue(String.valueOf(CommonUtil.getTwoDots(value)));
		memoryResult.setActivityName(CollectDataImpl.getCurActivity());
		/**
		 * �������30s��������������¿�ʼ��
		 */
		if (nowTime - lastTime > memoryInterval) {
			memoryTestNow = false;
			lastTime = 0;
			nowTime = 0;
			int shakeCount = getShakeCount();
			memoryList.clear();
			if (shakeCount > MEMORY_SHAKECOUNT) {

				// ���ý��Ϊfalse
				memoryResult.setResult(false);
				if (memoryErrorList.contains(memoryResult)) {
					return memoryResult;
				}
				
				// ������ʾ������Ϣ
				memoryResult.setShowErrorMsg(true);
				// ������Ի���
				memoryResult = saveMemoryEnvironment(memoryResult);
				// ������ŵ������б���
				memoryErrorList.add(memoryResult);
				
				return memoryResult;
			}
		} else {
			// ������ʾ������Ϣ
			memoryResult.setShowErrorMsg(false);
			memoryResult.setResult(true);
			memoryList.add(memoryData);
		}

		return memoryResult;
	}

	/***
	 * cpu���Ա�����Ի���
	 * 
	 * @param result
	 */
	private CpuHandleResult saveCpuEnvironment(CpuHandleResult handleResult) {
		// String screenshotsPath =
		// SaveEnvironmentManager.getInstance().screenShots(GlobalConfig.DeviceName,
		// Constants.TYPE_CPU);
		String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName, mCurTestPackage,
				Constants.TYPE_CPU);
		// ��ʱ��ץȡmethod trace����Ӱ��ɼ�����
		// String methodTrace =
		// SaveEnvironmentManager.getInstance().methodTracing(GlobalConfig.DeviceName,
		// GlobalConfig.PackageName, Constants.TYPE_CPU);
		handleResult.setLogPath(logPath);
		handleResult.setMethodTracePath("");
		return handleResult;
	}

	/**
	 * memory���Ա�����
	 * 
	 * @param result
	 * @return
	 */
	private MemoryHandleResult saveMemoryEnvironment(MemoryHandleResult memoryResult) {
		// dumpsys memory
		// String filePath =
		// SaveEnvironmentManager.getInstance().dumpMemory(GlobalConfig.DeviceName,
		// GlobalConfig.PackageName, Constants.TYPE_MEMORY);
		String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName, mCurTestPackage,
				Constants.TYPE_MEMORY);
		// String screenPath =
		// SaveEnvironmentManager.getInstance().screenShots(GlobalConfig.DeviceName,
		// Constants.TYPE_MEMORY);

		memoryResult.setMemoryHprofPath("");
		memoryResult.setMethodTracePath("");
		memoryResult.setLogPath(logPath);

		return memoryResult;
	}
	
	/**
	 *  ��ղ�������
	 */
	public void destoryData(){
		if (kpiErrorList != null) {
			kpiErrorList.clear();
		}
		
		if (cpuErrorList != null) {
			cpuErrorList.clear();
		}
		
		if (flowErrorList != null) {
			flowErrorList.clear();
		}
		if (memoryList != null) {
			memoryErrorList.clear();
		}
		if (memoryErrorList != null) {
			memoryErrorList.clear();
		}
		
		if (cpuList != null) {
			cpuList.clear();
		}
		
		if (kpiList != null) {
			kpiList.clear();
		}
		
		if (slientCpuList != null) {
			slientCpuList.clear();
		}
		
		if (kpidataList != null) {
			kpidataList.clear();
		}
	}
}
