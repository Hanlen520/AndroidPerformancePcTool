package com.xdja.adb;

import java.util.TreeSet;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDebugBridgeChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.IDevice;
import com.xdja.constant.GlobalConfig;
import com.xdja.util.CommonUtil;

/***
 * ����Adb��صĲ������õ���ddmlib
 * 
 * @author zlw
 *
 */
public class AdbManager implements IDebugBridgeChangeListener {
	private static AdbManager mInstance;
	private TreeSet<AndroidDevice> devices = new TreeSet<>();
	AndroidDebugBridge myBridge = null;

	public static AdbManager getInstance() {
		if (mInstance == null) {
			synchronized (AdbManager.class) {
				if (mInstance == null) {
					mInstance = new AdbManager();
				}
			}
		}

		return mInstance;
	}

	private AdbManager() {
	}
	
	/**
	 * ��Adb���г�ʼ��
	 */
	public void init(){
		setDefaultSetting();
		createBridge();
		AndroidDebugBridge.addDebugBridgeChangeListener(this);
	}
	
	/**
	 * ����AndroidBridge
	 */
	private void createBridge() {
		try {
			AndroidDebugBridge.init(true);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		myBridge = AndroidDebugBridge.getBridge();
		if (myBridge == null) {
			myBridge = AndroidDebugBridge.createBridge(AndroidSdk.adb().getAbsolutePath(), false);
		}
		long timeout = System.currentTimeMillis() + 60000;
		while (!myBridge.hasInitialDeviceList() && System.currentTimeMillis() < timeout) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * ��ȡ��ǰ���ӵ��ֻ���Devices
	 * 
	 * @return
	 */
	public TreeSet<AndroidDevice> getDevices() {
		if (myBridge != null) {
			IDevice[] origindevices = myBridge.getDevices();
			AndroidDevice device = null;
			if (devices.size() > 0) {
				devices.clear();
			}
			System.out.println("origindevices.length = " + origindevices.length);
			for (int i = 0; i < origindevices.length; i++) {
				device = new AndroidDevice(origindevices[i]);
				devices.add(device);
			}

		}
		return devices;
	}

	/***
	 * ͨ���豸���������Ҷ�Ӧ��IDevice
	 * 
	 * @param sn
	 * @return
	 */
	public IDevice getIDevice(String name) {

		if (CommonUtil.strIsNull(name)) {
			return null;
		}

		if (devices == null) {
			devices = getDevices();
		}

		for (IAndroidDevice device : devices) {
			if (name.equals(device.getName())) {
				return device.getDevice();
			}
		}
		return null;
	}

	/***
	 * ͨ���豸��������ȡ�豸���к�
	 * 
	 * @param name
	 * @return
	 */
	public String getSerialNumber(String name) {
		if (CommonUtil.strIsNull(name)) {
			return null;
		}

		if (devices == null) {
			devices = getDevices();
		}

		for (IAndroidDevice device : devices) {
			if (name.equals(device.getName())) {
				return device.getSerialNumber();
			}
		}
		return null;
	}

	/**
	 * ����GC
	 * 
	 * @param name
	 *            �豸������
	 */
	public void causeGC(String name, String packageName) {
		Client client = getClient(name, packageName);
		if (client != null) {
			client.executeGarbageCollector();
		}
	}

	/**
	 * ��ȡ�ڴ�������Ϣ public static final String HEAP_OBJECTS_ALLOCATED =
	 * "objectsAllocated"; //$NON-NLS-1$ public static final String
	 * HEAP_MAX_SIZE_BYTES = "maxSizeInBytes"; //$NON-NLS-1$ public static final
	 * String HEAP_BYTES_ALLOCATED = "bytesAllocated"; //$NON-NLS-1$ public
	 * static final String HEAP_SIZE_BYTES = "sizeInBytes"; //$NON-NLS-1$
	 * 
	 * @param name
	 * @param packageName
	 */
	public void getAllocInfo(String name, String packageName) {
		Client client = getClient(name, packageName);
		if (client != null) {
			client.updateHeapInfo();
		}
	}

	/**
	 * ��ȡ��ӦӦ����ddms�е�һ��Clientʵ��
	 * 
	 * @param name
	 * @param packageName
	 * @return
	 */
	public Client getClient(String name, String packageName) {
		IDevice device = getIDevice(name);

		if (device != null) {
			Client client = device.getClient(packageName);
			return client;
		}
		return null;
	}

	/**
	 * ����Ĭ�ϵ�debug��port
	 * 
	 * @param port
	 */
	public void setDefaultSetting() {
		DdmPreferences.setDebugPortBase(GlobalConfig.BASEPORT);
		DdmPreferences.setSelectedDebugPort(GlobalConfig.DEBUGPORT);
		DdmPreferences.setUseAdbHost(true);
	    DdmPreferences.setInitialThreadUpdate(true);
	    DdmPreferences.setInitialHeapUpdate(true);
	}

	@Override
	public void bridgeChanged(AndroidDebugBridge bridge) {
		// TODO Auto-generated method stub
		myBridge = bridge;
	}
	
	/***
	 * �ͷ���Դ
	 */
	public void release() {
		AndroidDebugBridge.removeDebugBridgeChangeListener(this);
		
	}
}
