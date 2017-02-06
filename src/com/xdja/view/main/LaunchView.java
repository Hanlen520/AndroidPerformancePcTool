package com.xdja.view.main;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;
import com.xdja.adb.AdbManager;
import com.xdja.adb.AndroidDevice;
import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.entity.BaseTestInfo;
import com.xdja.collectdata.handleData.entity.CpuHandleResult;
import com.xdja.collectdata.handleData.entity.FlowHandleResult;
import com.xdja.collectdata.handleData.entity.KpiHandleResult;
import com.xdja.collectdata.handleData.entity.MemoryHandleResult;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.database.PerformanceDB;
import com.xdja.log.LoggerManager;
import com.xdja.util.CommonUtil;
import com.xdja.util.ExecShellUtil;
import com.xdja.util.ProPertiesUtil;
import com.xdja.util.SwingUiUtil;
import com.xdja.util.SwingUiUtil.ClickDialogBtnListener;
import com.xdja.view.ToolsView;
import com.xdja.view.chart.BatteryView;
import com.xdja.view.chart.CpuView;
import com.xdja.view.chart.FlowView;
import com.xdja.view.chart.FpsView;
import com.xdja.view.chart.KpiTestView;
import com.xdja.view.chart.MemoryView;

public class LaunchView extends JFrame implements IDeviceChangeListener {

	/**
	 * serial Version UID is auto generated
	 */
	private static final long serialVersionUID = 7845872493970114091L;
	private Logger logger = Logger.getLogger(LaunchView.class);
	private String author;
	private JPanel frame;
	private JButton jb1, jb2,slientBtn;
	private MemoryView viewMemory;
	private FlowView viewFlow;
	private CpuView viewCpu;
	private KpiTestView kpiTestView;
	private FpsView viewFps;
	private BatteryView viewBattery;
	private ToolsView toolsView;
	private static JComboBox<String> comboDevices;
	private static JComboBox<String> comboProcess;
	private JTabbedPane jTabbedPane = new JTabbedPane();
	private String[] tabNames = { "   ��    ��   ", "     cpu    ", "   ��   ��   ", "    ����ʱ��     ", "   ֡   ��   ",
			"   ��   ��   ", "    ʵ�ù���     " };
	private final static int WIDTH = 1248;
	private final static int HEIGHT = 760;
	// ��Ĭ����ʱ����ʮ����֮���ٲɼ�����
	private final static int SLIENT_TIME_INTERVAL = 10 * 1000;
	private Timer mSlientWaitTimer = new Timer();
	
	// ��ǰѡ��Ĳ��԰���
	private String mCurTestPackageName;
	// �����ڸ����豸��ʱ�򣬲���������б�ĵ���¼�
	public boolean myIgnoreActionEvents = true;
	/**
	 * constructor to init a LaunchView instance create a JPanel instance to put
	 * other controller parts
	 * 
	 * @param name:
	 *            author name
	 */
	public LaunchView() {
		this.author = Constants.PRODUCT_NAME;
		this.frame = new JPanel();
		setTitle(String.format("%s v1.0", author));
		setBounds(100, 50, WIDTH, HEIGHT);
		createTopMenu();
		add(frame);
		AndroidDebugBridge.addDeviceChangeListener(this);
	}

	/**
	 * constructor to init a LaunchView instance create a JPanel instance to put
	 * other controller parts ���ڴ���һ�����ؼ����ڷ������Ŀؼ���������������ʾ�����Ĳ�����
	 */
	public void createParts() {
		// ������ʽ���ò��ָ�ʽΪ�գ����򲻻ᰴ���������úõĸ�ʽ����
		frame.setLayout(null);
		// combo box to select device sn
		comboDevices = new JComboBox<String>();
		frame.add(comboDevices);
		Rectangle rect = new Rectangle(0, 0, 300, 30);// �趨����λ��
		comboDevices.setBounds(rect);// ���λ��
		comboDevices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// comboProcess.removeAllItems();
				updateClientList();
			}
		});

		// ����չʾ�豸�Ľ���
		comboProcess = new JComboBox<String>();
		frame.add(comboProcess);
		Rectangle rectProcess = new Rectangle(320, 0, 420, 30);
		comboProcess.setBounds(rectProcess);
		comboProcess.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (myIgnoreActionEvents) {
					return;
				}
				// TODO Auto-generated method stub
				String packageNameold = GlobalConfig.getTestPackageName();
				String packageName = (String) comboProcess.getSelectedItem();
				
				System.out.println("I am select, packageName = " +packageName );
				
				if (!CommonUtil.strIsNull(packageName)) {
					mCurTestPackageName = packageName;
					// ��ѡ��İ�����¼������
					ProPertiesUtil.getInstance().writeProperties(Constants.CHOOSE_PACKAGE, packageName);
				}
				
				if (packageNameold != packageName) {
					kpiTestView.clear();
				}
				
			}
		});
		// ��ʼ��ذ�ť
		jb1 = new JButton("��ʼ���");
		Rectangle rectjb1 = new Rectangle(800, 0, 100, 30);
		frame.add(jb1);
		jb1.setBounds(rectjb1);
		jb1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						jb2.setEnabled(true);
						jb1.setEnabled(false);
						slientBtn.setEnabled(false);
						if (viewFps != null) {
							viewFps.setBtnEnable(false);
						}
						comboDevices.setEnabled(false);
						comboProcess.setEnabled(false);
						startTest();
					}
				});

				thread.start();
			}

		});

		// ֹͣ��ذ�ť
		jb2 = new JButton("ֹͣ���");
		Rectangle rectjb2 = new Rectangle(920, 0, 100, 30);
		frame.add(jb2);
		jb2.setBounds(rectjb2);
		jb2.setEnabled(false);

		jb2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						jb1.setEnabled(true);
						jb2.setEnabled(false);
						slientBtn.setEnabled(true);
						comboDevices.setEnabled(true);
						comboProcess.setEnabled(true);
						stopTest();
						if (viewFps != null) {
							viewFps.setBtnEnable(true);
						}
					}
				});

				thread.start();
			}
		});
		
		// ��Ĭ����
		slientBtn = new JButton("��ʼ��Ĭ����");
		Rectangle slientRect = new Rectangle(1040, 0, 120, 30);
		frame.add(slientBtn);
		slientBtn.setBounds(slientRect);
		slientBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String cmd = e.getActionCommand();
				switch (cmd) {
				case "��ʼ��Ĭ����":
					Thread startThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							jb1.setEnabled(false);
							jb2.setEnabled(false);
							comboDevices.setEnabled(false);
							comboProcess.setEnabled(false);
							slientBtn.setText("ֹͣ��Ĭ����");
							startSlientTest();
							if (viewFps != null) {
								viewFps.setBtnEnable(false);
							}
							
							SwingUiUtil.getInstance().showTipsDialog(LaunchView.this, "��ʾ", "��Ĭ���ԣ�5���Ӻ�ʼ�ɼ�����", "��֪����", null);
						}
					});
					
					startThread.start();
					break;
				case "ֹͣ��Ĭ����":
					Thread stopThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							jb1.setEnabled(true);
							jb2.setEnabled(true);
							comboDevices.setEnabled(true);
							comboProcess.setEnabled(true);
							slientBtn.setText("��ʼ��Ĭ����");
							stopSlientTest();
							if (viewFps != null) {
								viewFps.setBtnEnable(true);
							}
						}
					});
					
					stopThread.start();
					break;
				default:
					break;
				}
			}
		});
		
		layoutTabComponents();
	}
	
	/**
	 *  ������Ĭ����
	 *  ��Ĭ����Ŀǰֻ���CPU �� Flow �������͵�����
	 */
	private void startSlientTest(){
		GlobalConfig.TestPackageName = mCurTestPackageName;
		BaseTestInfo baseTestInfo = CollectDataImpl.getBaseTestInfo(mCurTestPackageName);
		if (baseTestInfo != null) {
			GlobalConfig.TestVersion = baseTestInfo.versionName;
		}
		mSlientWaitTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//���豸���뾲Ĭ״̬
				AdbManager.getInstance().makeDeviceSlient();
				
				if (viewCpu != null) {
					viewCpu.setSlientTest(true);
					viewCpu.start(mCurTestPackageName);
				}
				
				if (viewFlow != null) {
					viewFlow.setSlient(true);
					viewFlow.start(mCurTestPackageName);
				}
			}
		}, SLIENT_TIME_INTERVAL);
		
		mSlientWaitTimer = null;
	}
	
	/**
	 * ֹͣ��Ĭ����
	 */
	private void stopSlientTest(){
		if (mSlientWaitTimer != null) {
			mSlientWaitTimer.cancel();
			mSlientWaitTimer = null;
		}
		
		if (viewCpu != null) {
			viewCpu.stop();
		}
		
		if (viewFlow != null) {
			viewFlow.stop();
		}
		
		saveSlientDataToDb();
		
		// �ر����ݿ�
		PerformanceDB.getInstance().closeDB();
	}
	
	/**
	 * ����̬���ݱ��浽���ݿ���
	 */
	private void saveSlientDataToDb(){
		saveCommonData(mCurTestPackageName);
		String selectProcess = (String) comboProcess.getSelectedItem();
		if (!selectProcess.equals(mCurTestPackageName)) {
			return;
		}
		
		if (CommonUtil.strIsNull(mCurTestPackageName)) {
			return;
		}
		if (viewCpu != null) {
			List<CpuHandleResult> handleSlientList = viewCpu.getHandleResult();
			PerformanceDB.getInstance().insertSlientCpuData(handleSlientList);
		}
		
		if (viewFlow != null) {
			List<FlowHandleResult> handleFlowList = viewFlow.getHandleResultList();
			PerformanceDB.getInstance().insertSlientFlowData(handleFlowList);
		}	
	}
	
	/**
	 * �����ڷ�tab����� tabNames = {"�ڴ�", "cpu", "����", "kpitest", "֡��", "����"};
	 */
	private void layoutTabComponents() {
		Rectangle rect = new Rectangle(100, 100, 600, 200);
		// 1.�ڴ�
		// memory chart view
		viewMemory = new MemoryView(Constants.MEMORY, Constants.MEMORYContent, Constants.MEMORY_UNIT);
		viewMemory.setBounds(rect);
		jTabbedPane.addTab(tabNames[0], viewMemory);

		// 2.cpu
		viewCpu = new CpuView(Constants.CPU, Constants.CPU, Constants.CPU_UNIT);
		viewCpu.setBounds(rect);
		jTabbedPane.addTab(tabNames[1], viewCpu);

		// 3.kpiTest
		kpiTestView = new KpiTestView(Constants.KPI, Constants.KPITITLE, Constants.KPI);
		kpiTestView.setBounds(rect);
		jTabbedPane.addTab(tabNames[3], kpiTestView);

		// 4.����
		viewFlow = new FlowView(Constants.FLOW, Constants.FLOW, Constants.FLOW_UNIT);
		viewFlow.setBounds(rect);
		jTabbedPane.addTab(tabNames[5], viewFlow);

		// 5.֡��
		viewFps = new FpsView(Constants.FPS, Constants.FPSTITLE, Constants.FPS_UNIT);
		viewFps.setBounds(rect);
		jTabbedPane.addTab(tabNames[4], viewFps);

		// 6.����
		viewBattery = new BatteryView(Constants.BATTERY, Constants.BATTERY, Constants.BATTERY_UNIT);
		viewBattery.setBounds(rect);
		jTabbedPane.addTab(tabNames[2], viewBattery);

		// ʵ�ù���
		toolsView = new ToolsView();
		toolsView.setBounds(rect);
		jTabbedPane.addTab(tabNames[6], toolsView);

		frame.add(jTabbedPane);

		rect = new Rectangle(20, 100, WIDTH - 50, 550);
		jTabbedPane.setBounds(rect);
	}

	/**
	 * add action listener for all the controller parts
	 */
	public void addActionListener() {

		// ��������ӹرռ�����
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				showExitDialog();
			}
		});
	}

	/**
	 * ��ʼ��logger
	 */
	private void initLogger() {
		// initial Logger Manager to use log4j
		if (!LoggerManager.isInited()) {
			LoggerManager.initLogger();
		}
		logger.info("LoggerManager is inited successfully!");
	}

	/**
	 * ��ȡѡ�е�device
	 * 
	 * @return
	 */
	public static String getdevice() {
		String devicename = "";
		if (comboDevices != null) {
			devicename = (String) comboDevices.getSelectedItem();
		}
		GlobalConfig.DeviceName = devicename;
		return devicename;

	}

	private void showExitDialog() {
		SwingUiUtil.getInstance().showOkAndCancelDialog(this, "��ʾ", "�����˳���", "ȷ��", "ȡ��", new ClickDialogBtnListener() {

			@Override
			public void clickOkBtn() {
				// TODO Auto-generated method stub
				logger.info("program is exited!");
				setDefaultCloseOperation(EXIT_ON_CLOSE);
				destoryViewData();
				handleExitSaveData();
			}

			@Override
			public void clickCancelBtn() {
				// TODO Auto-generated method stub
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ����ǹؼ�
			}
		});
	}
	
	/**
	 *  ���˳���������ֻ��豸�Ͽ�����ʱ��
	 *  ����������ݣ�����ײ���������
	 */
	private void handleExitSaveData(){
		if (isNormalTestNow()) {
			stopTest();
		}
		
		if (isSlientTestNow()) {
			stopSlientTest();
		}
		
		if (isFpsTestNow()) {
			if (viewFps != null) {
				viewFps.stop();
				viewFps.saveDataToDb();
			}
		}
	}
	
	
	private void startTest() {
		GlobalConfig.TestPackageName = mCurTestPackageName;
		BaseTestInfo baseTestInfo = CollectDataImpl.getBaseTestInfo(mCurTestPackageName);
		if (baseTestInfo != null) {
			GlobalConfig.TestVersion = baseTestInfo.versionName;
		}
		if (viewCpu != null) {
			viewCpu.setSlientTest(false);
			viewCpu.start(mCurTestPackageName);
		}

		if (viewMemory != null) {
			viewMemory.start(mCurTestPackageName);
		}

		if (viewFlow != null) {
			viewFlow.setSlient(false);
			viewFlow.start(mCurTestPackageName);
		}

		if (kpiTestView != null) {
			try {
				kpiTestView.start(mCurTestPackageName);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��������
	 */
	private void stopTest() {
		if (viewCpu != null) {
			viewCpu.stop();
		}

		if (viewMemory != null) {
			viewMemory.stop();
		}

		if (viewFlow != null) {
			viewFlow.stop();
		}

		if (kpiTestView != null) {
			kpiTestView.stop();
		}
		
		// �����ݱ��浽���ݿ���
		saveDataToDB();
		
		PerformanceDB.getInstance().closeDB();
	}
	
	/**
	 * �����еĲ������ݱ��浽���ݿ���
	 * 
	 */
	private void saveDataToDB(){
		saveCommonData(mCurTestPackageName);
		String selectProcess = (String) comboProcess.getSelectedItem();
		if (!selectProcess.equals(mCurTestPackageName)) {
			mCurTestPackageName = selectProcess;//����mCurTestPackageNameΪnullʱ����¼���ݵ����ݿ�
//			return;
		}
		if (CommonUtil.strIsNull(mCurTestPackageName)) {
			mCurTestPackageName = selectProcess;
//			return;
		}
		// cpu
		if (viewCpu != null) {
			List<CpuHandleResult> cpuList = viewCpu.getHandleResult();
			if (cpuList != null && cpuList.size() > 0) {
				PerformanceDB.getInstance().insertCpuData(cpuList);
			}
		}
		
		// memory
		if (viewMemory != null) {
			List<MemoryHandleResult> memoryList = viewMemory.getHandleResult();
			if (memoryList != null && memoryList.size() > 0) {
				PerformanceDB.getInstance().insertMemoryData(memoryList);
			}
		}
		
		// ����KPi����
		if (kpiTestView != null) {
			List<KpiHandleResult> kpiHandleResults = kpiTestView.getHandleKpiList();
			if (kpiHandleResults != null && kpiHandleResults.size() > 0) {
				PerformanceDB.getInstance().insertKpiData(kpiHandleResults);
			}
			
		}
		if (viewFlow != null) {
			List<FlowHandleResult> flowHandleResults = viewFlow.getHanResultList();
			if (flowHandleResults != null && flowHandleResults.size() > 0) {
				PerformanceDB.getInstance().insertFlowData(flowHandleResults);
			}
		}
	}
	
	
	@Override
	public void deviceConnected(IDevice device) {
		// TODO Auto-generated method stub
		updateDeviceList();
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		// TODO Auto-generated method stub
		updateDeviceList();
		//
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		// TODO Auto-generated method stub
		if ((changeMask & IDevice.CHANGE_CLIENT_LIST) != 0) {
			updateClientList();
		} else if ((changeMask & IDevice.CHANGE_STATE) != 0) {
			updateDeviceList();
		}
	}
	
	/**
	 * �����Եİ�����������
	 */
	private void saveCommonData(String packageName){
		if (CommonUtil.strIsNull(packageName)) {
			return;
		}
		
		String version = "";
		BaseTestInfo baseTestInfo = CollectDataImpl.getBaseTestInfo(packageName);
		if (baseTestInfo != null) {
			version = baseTestInfo.versionName;
		}
		
		// ������ԵĻ�����Ϣ
		PerformanceDB.getInstance().saveCommonData(packageName, version);
	}
	
	/***
	 * ����DeviceList
	 */
	private void updateDeviceList() {
		myIgnoreActionEvents = true;
		if (comboDevices != null && !comboDevices.isEnabled()) {
			return;
		}
		TreeSet<AndroidDevice> devices = AdbManager.getInstance().getDevices();
		System.out.println("updateDeviceList = " + devices.size());
		List<String> snList = new ArrayList<>(2);
		for (AndroidDevice device : devices) {
			snList.add(device.getName());
		}

		if (comboDevices != null) {
			comboDevices.removeAllItems();
		}
		for (String sn : snList) {
			comboDevices.addItem(sn);
		}
		
		myIgnoreActionEvents = false;
	}

	/**
	 * ����Device����ȡ����
	 */
	private void updateClientList() {
		myIgnoreActionEvents = true;
		if (comboProcess != null && !comboProcess.isEnabled()) {
			return;
		}
		String selectDevice = getdevice();
		if (!CommonUtil.strIsNull(selectDevice)) {
			String devicesid = AdbManager.getInstance().getSerialNumber(selectDevice);
			IDevice dev = AdbManager.getInstance().getIDevice(selectDevice);
			ExecShellUtil.getInstance().setDevice(dev);
			List<String> respack = CollectDataImpl.getRunningProcess(devicesid);
			System.out.println("I am update");
			if (respack.size() > 0 && comboProcess != null) {
				comboProcess.removeAllItems();
			}
			// �Եõ����б�������ĸ����
			Collections.sort(respack, latterComparator);
			for (String sn : respack) {
				comboProcess.addItem(sn);
			}
			
			// ����߼�����ȷ����Ӧ����ʾ��һ�γ��ֵ�packageName
//			String packageName = ProPertiesUtil.getInstance().getProperties(Constants.CHOOSE_PACKAGE);
//			if (!CommonUtil.strIsNull(packageName)) {
//				comboProcess.setSelectedItem(packageName);
//			}
		}
		
		myIgnoreActionEvents = false;
	}
	
	/**
	 * �������Ƶ�����ĸ����
	 */
	private Comparator<String> latterComparator = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			// TODO Auto-generated method stub
			return o1.compareTo(o2);
		}
	};
	/**
	 * ��ʼ��AdbManager
	 */
	private void initAdbManager() {
		AdbManager.getInstance().init();
	}

	private void createTopMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// ��Ӳ˵�ѡ��
		JMenu aboutMenu = new JMenu("Help");

		JMenuItem aboutItem = new JMenuItem("����");
		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SwingUiUtil.getInstance().showTipsDialog(LaunchView.this, "����", Constants.ABOUT, "��֪����", null);
			}
		});
		JMenuItem helpItem = new JMenuItem("ʹ�ð���");
		helpItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SwingUiUtil.getInstance().showTipsDialog(LaunchView.this, "ʹ�ð���", Constants.HELP, "��֪����", null);
			}
		});

		aboutMenu.add(aboutItem);
		aboutMenu.add(helpItem);

		menuBar.add(aboutMenu);
	}
	
	/**
	 *  ����view�е�����
	 */
	private void destoryViewData(){
		if (viewFlow != null) {
			viewFlow.destoryData();
		}
		
		if (viewMemory != null) {
			viewMemory.destoryData();
		}
		
		if (viewCpu != null) {
			viewCpu.destoryData();
		}
		
		if (viewBattery != null) {
			viewBattery.destoryData();
		}
		
		if (viewFps != null) {
			viewFps.destoryData();
		}
		
		if (kpiTestView != null) {
			kpiTestView.destoryData();
		}
		
		// ���ѡ��İ�������
		ProPertiesUtil.getInstance().removeValue(Constants.CHOOSE_PACKAGE);
		GlobalConfig.TestPackageName = "";
		GlobalConfig.TestVersion = "";
	}
	
	/**
	 *  �ж��Ƿ��������ڽ���
	 *  
	 * @return
	 */
	private boolean isNormalTestNow(){
		if (viewMemory.isRunning && viewCpu.isRunning && viewFlow.isRunning && kpiTestView.isRunning) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * �жϾ�̬�����Ƿ��ڽ���
	 * @return
	 */
	private boolean isSlientTestNow(){
		if (viewCpu.isRunning && viewFlow.isRunning && viewCpu.slient && viewFlow.isRunning) {
			return true;
		}
		
		return false;
	}
	
	/**
	 *  �ж�Fps�Ƿ��ڲ���
	 * @return
	 */
	private boolean isFpsTestNow(){
		if (viewFps.isRunning) {
			return true;
		}
		
		return false;
	}
	
	
	public static void main(String[] args) {
		LaunchView launch = new LaunchView();
		launch.createParts();
		launch.initLogger();
		launch.initAdbManager();
		launch.addActionListener();
		launch.setVisible(true);
	}
	
	public static String getSelectPkg(){
		String pkg = comboProcess.getSelectedItem().toString();
		return pkg;
		
	}

}
