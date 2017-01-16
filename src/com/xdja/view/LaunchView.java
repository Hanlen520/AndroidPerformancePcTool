package com.xdja.view;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
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
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.log.LoggerManager;
import com.xdja.util.CommonUtil;
import com.xdja.util.ExecShellUtil;
import com.xdja.util.SwingUiUtil;
import com.xdja.util.SwingUiUtil.ClickDialogBtnListener;

public class LaunchView extends JFrame implements IDeviceChangeListener {

	/**
	 * serial Version UID is auto generated
	 */
	private static final long serialVersionUID = 7845872493970114091L;
	private Logger logger = Logger.getLogger(LaunchView.class);
	private String author;
	private JPanel frame;
	private JButton jb1, jb2;
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
		setVisible(true);
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
				// TODO Auto-generated method stub
				String packageNameold = GlobalConfig.PackageName ;
				String packageName = (String) comboProcess.getSelectedItem();
				if (packageName != null) {
					GlobalConfig.PackageName = packageName;
				}
				if (packageNameold !=GlobalConfig.PackageName){
					kpiTestView.clear();
				}
			}
		});

		// ��ʼ��ذ�ť
		jb1 = new JButton("��ʼ���");
		Rectangle rectjb1 = new Rectangle(860, 0, 100, 30);
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
		Rectangle rectjb2 = new Rectangle(980, 0, 100, 30);
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
						comboDevices.setEnabled(true);
						comboProcess.setEnabled(true);
						stopTest();
					}
				});

				thread.start();
			}
		});

		layoutTabComponents();
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
		kpiTestView = new KpiTestView(Constants.KPI,Constants.KPITITLE, Constants.KPI);
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
			}

			@Override
			public void clickCancelBtn() {
				// TODO Auto-generated method stub
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ����ǹؼ�
			}
		});
	}
	private void startTest() {
		if (viewCpu != null) {
			viewCpu.start(GlobalConfig.PackageName);
		}

		if (viewMemory != null) {
			viewMemory.start(GlobalConfig.PackageName);
		}

		if (viewFlow != null) {
			viewFlow.start(GlobalConfig.PackageName);
		}
		
		if (kpiTestView != null) {
			try {
				kpiTestView.start(GlobalConfig.PackageName);
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

	/***
	 * ����DeviceList
	 */
	private void updateDeviceList() {
		if (comboDevices != null && !comboDevices.isEnabled()) {
			return ;
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
		
		if (kpiTestView != null) {
			kpiTestView.stop();
		}
	}

	/**
	 * ����Device����ȡ����
	 */
	private void updateClientList() {
		if (comboProcess != null && !comboProcess.isEnabled()) {
			return ;
		}
		String selectDevice = getdevice();
		if (!CommonUtil.strIsNull(selectDevice)) {
			String devicesid = AdbManager.getInstance().getSerialNumber(selectDevice);
			IDevice dev = AdbManager.getInstance().getIDevice(selectDevice);
			ExecShellUtil.getInstance().setDevice(dev);
			List<String> respack = CollectDataImpl.getRunningProcess(devicesid);
			if (respack.size() > 0 && comboProcess != null) {
				comboProcess.removeAllItems();
			}
			for (String sn : respack) {
				comboProcess.addItem(sn);
			}
		}
	}
	
	/**
	 *  ��ʼ��AdbManager
	 */
	private void initAdbManager(){
		AdbManager.getInstance().init();
	}
	
	private void createTopMenu(){
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
	
	
	public static void main(String[] args) {
		LaunchView launch = new LaunchView();
		launch.createParts();
		launch.initLogger();
		launch.initAdbManager();
		launch.addActionListener();
		launch.setVisible(true);

	}

}
