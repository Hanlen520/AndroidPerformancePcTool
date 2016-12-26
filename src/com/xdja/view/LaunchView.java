package com.xdja.view;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import org.apache.log4j.Logger;
import com.android.ddmlib.IDevice;
import com.xdja.adb.AdbHelper;
import com.xdja.adb.AndroidDevice;
import com.xdja.adb.DeviceManager;
import com.xdja.collectdata.CollectDataImpl;
import com.xdja.adb.DeviceManager.DeviceStateListener;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.log.LoggerManager;
import com.xdja.monitor.ControllerMonitor;
import com.xdja.util.DialogUtil;
import com.xdja.util.DialogUtil.ClickDialogBtnListener;

public class LaunchView extends JFrame{
	
	/**
	 * serial Version UID is auto generated
	 */
	private static final long serialVersionUID = 7845872493970114091L;
	private Logger logger = Logger.getLogger(LaunchView.class);
	private String author;
	private JPanel frame;
	private JButton btnMonkey;
	private JButton btnLaunchCost;
	private JButton btnFps;
	private JButton btnStartTest;
	private JButton jb1, jb2;
	private MemoryView viewMemory;
	private FlowView viewFlow;
	private CPUView viewCpu;
	private BatteryView viewBattery;
	private KpiTestView kpiTestView;
	private FpsChartPanel chartFps;
	private JLabel labelPackage;
	private JTextField textPackage;
	private JComboBox<String> comboPackageList;
	private List<String> packageList = new ArrayList<String>();
	private DefaultComboBoxModel<String> model;
	private static JComboBox<String> comboDevices;
	private JComboBox<String> comboProcess;
	private Checkbox boxUSBPowered;
	private JTabbedPane jTabbedPane = new JTabbedPane();
	private String[] tabNames = {"   ��    ��   ", "     cpu    ", "   ��   ��   ", "    ����ʱ��     ", "   ֡   ��   ", "   ��   ��   "};
	
	//����һ�ݵ�ǰ���ӵ�pc���豸�б�
	private List<AndroidDevice> devices = new ArrayList<AndroidDevice>(12);
	/**
	 * constructor to init a LaunchView instance
	 * create a JPanel instance to put other controller parts
	 * @param name: author name
	 * */
	public LaunchView(String name) {
		this.author = name;
		this.frame = new JPanel();
		setTitle(String.format("%s v1.0", author));
		setBounds(100, 50, 1249, 760);
		add(frame);
		setVisible(true);
	}
	
	/**
	 * constructor to init a LaunchView instance
	 * create a JPanel instance to put other controller parts
	 * ���ڴ���һ�����ؼ����ڷ������Ŀؼ���������������ʾ�����Ĳ�����
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public void createParts() {
		//������ʽ���ò��ָ�ʽΪ�գ����򲻻ᰴ���������úõĸ�ʽ����
		frame.setLayout(null);
		// combo box to select device sn
		comboDevices = new JComboBox<String>();
		frame.add(comboDevices);
		Rectangle rect = new Rectangle(0, 0, 300, 30);//�趨����λ��
		comboDevices.setBounds(rect);//���λ��
		
		comboDevices.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e) {
//			  comboProcess.removeAllItems();
			  Object selected = getdevice();
			  if (selected!=null){
				    String devicesid = CollectDataImpl.devicesdo(selected);
					List<String> respack = CollectDataImpl.getRunningProcess(devicesid);
					for (String sn : respack) {
						comboProcess.addItem(sn);
					}
				}
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
				String packageName = (String) comboProcess.getSelectedItem();
				if (packageName != null) {
					GlobalConfig.PackageName = packageName;
				}
			}
		});
		
		jb1 = new JButton("��ʼ���");
		Rectangle rectjb1 = new Rectangle(860, 0, 100, 30);
		frame.add(jb1);
		jb1.setBounds(rectjb1);
		
		jb1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"start run performance!");
				jb2.setEnabled(true);
				jb1.setEnabled(false);
				comboDevices.setEnabled (false);
				startTest();
			}
				
		});
		
		jb2 = new JButton("ֹͣ���");
		Rectangle rectjb2 = new Rectangle(980, 0, 100, 30);
		frame.add(jb2);
		jb2.setBounds(rectjb2);
		jb2.setEnabled(false);
		
		jb2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"stop run performance!");
				jb1.setEnabled(true);
				jb2.setEnabled(false);
				comboDevices.setEnabled (true);
				stopTest();
			}
		});
		
		layoutTabComponents();
	}
	
	/**
	 * �����ڷ�tab�����
	 * tabNames = {"�ڴ�", "cpu", "����", "kpitest", "֡��", "����"};
	 */
	private void layoutTabComponents(){
		Rectangle rect = new Rectangle(100, 100, 800, 200);
		// 1.�ڴ�
	    //memory chart view
	    viewMemory = new MemoryView(Constants.MEMORY, Constants.MEMORYContent, Constants.MEMORY_UNIT);
	    viewMemory.setBounds(rect);
	    jTabbedPane.addTab(tabNames[0], viewMemory);
	    
	    // 2.cpu
	    viewCpu = new CPUView(Constants.CPU, Constants.CPU, Constants.CPU_UNIT);
	    viewCpu.setBounds(rect);
	    jTabbedPane.addTab(tabNames[1], viewCpu);
	    
	    // 3.����
	    viewBattery = new BatteryView(Constants.BATTERY, Constants.BATTERY, Constants.BATTERY_UNIT);
	    viewBattery.setBounds(rect);
	    jTabbedPane.addTab(tabNames[2], viewBattery);
	    
	    // 4.kpiTest
	    kpiTestView = new KpiTestView(Constants.KPITITLE, Constants.KPI);
	    viewBattery.setBounds(rect);
	    jTabbedPane.addTab(tabNames[3], kpiTestView);
	    
	    // 5.֡��
	    
	    chartFps = new FpsChartPanel(Constants.FPS, Constants.FPS, Constants.FPS_UNIT);
	    chartFps.setBounds(rect);
	    jTabbedPane.addTab(tabNames[4], chartFps);
	    
	    // 6.����
	    viewFlow = new FlowView(Constants.FLOW , Constants.FLOW, Constants.FLOW_UNIT);
	    viewFlow.setBounds(rect);
	    jTabbedPane.addTab(tabNames[5], viewFlow);
	    
	    frame.add(jTabbedPane);
	    rect = new Rectangle(20, 100, 1100, 600);
	    jTabbedPane.setBounds(rect);
	}
	
	
	/**
	 * add action listener for all the controller parts
	 * */
	public void addActionListener() {
		//initial Logger Manager to use log4j
		if (!LoggerManager.isInited()) {
			LoggerManager.initLogger();
		}
		logger.info("LoggerManager is inited successfully!");
		
		//initial android debug bridge
		List<String> snList = AdbHelper.getInstance().getDevices();
		System.out.println("ѡ���豸�ĵط�");//
//		System.out.println(snList);//

		for (String sn : snList) {
			comboDevices.addItem(sn);
		}
		System.out.println(getdevice());
		if (getdevice()!=null){
			Object selected = getdevice();
			String devicesid = CollectDataImpl.devicesdo(selected);
			List<String> respack = CollectDataImpl.getRunningProcess(devicesid);
			for (String sn : respack) {
				comboProcess.addItem(sn);
			}
		}
		
		
		
		
		//��������ӹرռ�����
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				showExitDialog();
			}
		});
		
		//Ϊ�����ɲ����Ŀؼ���Ӽ�����
		addDeviceChangeListener();
//		addStartListener();
		addPackageListener();
//		addstartrunlistener();
	}
	
	

	/**
	 * ����豸�仯�ļ���
	 */
	private void addDeviceChangeListener(){
		DeviceManager.getInstance().setDeviceStateListener(new DeviceStateListener() {
			
			@Override
			public void deviceDisconnected(AndroidDevice idevice) {
				// TODO Auto-generated method stub
				devices = DeviceManager.getInstance().getAndroidDevice();
				String comboDevicesName = DeviceManager.getInstance().getDeviceName(idevice);
				System.out.println("���ѡ���豸");//
				
				if (devices.size() > 0 && !devices.contains(idevice)) {
					devices.add(idevice);
					comboDevices.addItem(comboDevicesName);
				} else {
					comboDevices.removeAllItems();
					devices.add(idevice);
					//���ƹ��ɣ��豸����_�豸���к�
					comboDevices.addItem(comboDevicesName);
				}
			}
			
			@Override
			public void deviceConnected(AndroidDevice idevice) {
				// TODO Auto-generated method stub
				String comboDevicesName = DeviceManager.getInstance().getDeviceName(idevice);
				
				comboDevices.removeItem(comboDevicesName);
				devices.remove(idevice);
			}
		});
	}
	
//	private void addStartListener() {
//		btnStartTest.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				
//				Thread thread = new Thread(new Runnable() {
//					@Override
//					public void run() {
//						if (btnStartTest.getText().equals(Constants.START_TEST)) {
//							logger.info("click start test!");
//							String packageName = textPackage.getText();
//							if (packageName.equals(Constants.BLANK)) {
//								logger.info(Constants.PACKAGE_NAME_NULL);
//								JOptionPane.showMessageDialog(new JFrame(), Constants.PACKAGE_NAME_NULL);
//							} else {
//								if (null == comboDevices.getSelectedItem()) {
//									logger.info(Constants.DEVICE_NULL);
//									JOptionPane.showMessageDialog(new JFrame(), Constants.DEVICE_NULL);
//								} else {
//									IDevice dev = AdbHelper.getInstance().getDevice((String) comboDevices.getSelectedItem());
//									ControllerMonitor.getInstance().setDevice(dev);
//									ControllerMonitor.getInstance().getBatteryController().setUsbPowered(boxUSBPowered.getState());
//									viewMemory.start(packageName);
//									viewFlow.start(packageName);
//									viewCpu.start(packageName);
//									viewBattery.start(packageName);
//									btnStartTest.setText(Constants.STOP_TEST);
//								}
//							}
//						} else {
//							viewMemory.stop();
//							viewFlow.stop();
//							viewCpu.stop();
//							viewBattery.stop();
//							btnStartTest.setText(Constants.START_TEST);
//						}
//					}
//				});
//				thread.start();
//			}
//		});
//	}
	
	private boolean isAdjusting(JComboBox<String> cbInput) {
		if (cbInput.getClientProperty(Constants.ADJUSTING) instanceof Boolean) {
			return (Boolean) cbInput.getClientProperty(Constants.ADJUSTING);
		}
		return false;
	}

	private void setAdjusting(JComboBox<String> cbInput, boolean adjusting) {
		cbInput.putClientProperty(Constants.ADJUSTING, adjusting);
		
	}
	
	private void updateList(List<String> list) {
		setAdjusting(comboPackageList, true);
		model.removeAllElements();
		String input = textPackage.getText();
		if (!input.isEmpty()) {
			for (String item : list) {
				if (item.toLowerCase().startsWith(input.toLowerCase())) {
					model.addElement(item);
				}
			}
		} else {
			for (String item : list) {
				model.addElement(item);
			}
		}
		comboPackageList.setPopupVisible(model.getSize() > 0);
		setAdjusting(comboPackageList, false);
	}
	
	private void addPackageListener() {
		if (comboPackageList != null) {
			comboPackageList.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!isAdjusting(comboPackageList) && comboPackageList.getSelectedItem() != null) {
						textPackage.setText(comboPackageList.getSelectedItem().toString());
					}
				}
			});
		}
		if (textPackage != null) {
			textPackage.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					IDevice dev = AdbHelper.getInstance().getDevice((String) comboDevices.getSelectedItem());
					ControllerMonitor.getInstance().setDevice(dev);
					List<String> ret = ControllerMonitor.getInstance().getPackageController().getInfo();
					Iterator<String> iterator = ret.iterator();
					while(iterator.hasNext()) {
						logger.info(iterator.next());
					}
					packageList = ret;
					//refresh package list
					updateList(packageList);
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (comboPackageList.isPopupVisible()) {
						comboPackageList.setPopupVisible(false);
					}
				}
			});
			
			textPackage.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					updateList(packageList);
				}

				public void removeUpdate(DocumentEvent e) {
					updateList(packageList);
				}

				public void changedUpdate(DocumentEvent e) {
					updateList(packageList);
				}
				
			});
			
			textPackage.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent event) {
					setAdjusting(comboPackageList, true);
					if (event.getKeyCode() == KeyEvent.VK_SPACE && comboPackageList.isPopupVisible()) {
						event.setKeyCode(KeyEvent.VK_ENTER);
					}
					if (event.getKeyCode() == KeyEvent.VK_ENTER
							|| event.getKeyCode() == KeyEvent.VK_UP
							|| event.getKeyCode() == KeyEvent.VK_DOWN) {
						event.setSource(comboPackageList);
						comboPackageList.dispatchEvent(event);
						if (event.getKeyCode() == KeyEvent.VK_ENTER) {
							textPackage.setText(comboPackageList.getSelectedItem().toString());
							comboPackageList.setPopupVisible(false);
						}
					}
					if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
						comboPackageList.setPopupVisible(false);
					}
					setAdjusting(comboPackageList, false);
				}
			});
		}
		
	}
	
	public static  String getdevice()
    {
        String devicename = (String) comboDevices.getSelectedItem();
		return devicename;
         
    }
	
	
	private void showExitDialog(){
		DialogUtil.getInstance().showOkAndCancelDialog(this, "��ʾ", "�����˳���", "ȷ��", "ȡ��", new ClickDialogBtnListener() {
			
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

	/**
	 *  ��ʼ����
	 */
	private void startTest(){
		if(viewMemory != null){
			new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					kpiTestView.startTest();
					viewMemory.refreshData();
					viewFlow.refreshData();
					viewCpu.refreshData();
				}
			}.start();
			
		}
	}
	
	/**
	 *  ��������
	 */
	private void stopTest(){
		if (viewMemory != null) {
			viewMemory.stopRefresh();
			viewFlow.stopRefresh();
			viewCpu.stopRefresh();
//			kpiTestView.stopTest();
		}
	}
	
	
	public static void main(String[] args) {
		LaunchView launch = new LaunchView(Constants.PRODUCT_NAME);
		launch.createParts();
		launch.addActionListener();
		launch.setVisible(true);
		
	}
}
