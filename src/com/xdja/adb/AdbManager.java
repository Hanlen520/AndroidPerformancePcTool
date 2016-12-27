package com.xdja.adb;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IClientChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.ClientData;
import com.android.ddmlib.IDevice;
import com.github.cosysoft.device.android.AndroidDevice;
import com.github.cosysoft.device.android.impl.AndroidDeviceStore;
import com.xdja.util.CommonUtil;

/***
 * ����Adb��صĲ������õ���һ����Դ��https://github.com/cosysoft/device
 * 
 * @author zlw
 *
 */
public class AdbManager {
	private static AdbManager mInstance;
	private TreeSet<AndroidDevice> devices = null;

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
		setEventListener();
	}

	/**
	 * ��ȡ��ǰ���ӵ��ֻ���Devices
	 * 
	 * @return
	 */
	public TreeSet<AndroidDevice> getDevices() {
		devices = AndroidDeviceStore.getInstance().getDevices();
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
			devices = AndroidDeviceStore.getInstance().getDevices();
		}

		for (AndroidDevice device : devices) {
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
			devices = AndroidDeviceStore.getInstance().getDevices();
		}

		for (AndroidDevice device : devices) {
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
		client.enableAllocationTracker(true);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				client.requestAllocationDetails();
				client.enableAllocationTracker(false);
			}
		}, 3000);
	}

	/**
	 * ��ȡ��ӦӦ����ddms�е�һ��Clientʵ��
	 * 
	 * @param name
	 * @param packageName
	 * @return
	 */
	private Client getClient(String name, String packageName) {
		IDevice device = getIDevice(name);

		if (device != null) {
			Client client = device.getClient(packageName);
			client.executeGarbageCollector();
			return client;
		}
		return null;
	}

	/**
	 * �����¼�����
	 */
	private void setEventListener() {
		AndroidDebugBridge.addClientChangeListener(new IClientChangeListener() {

			@Override
			public void clientChanged(Client client, int changeMask) {
				// TODO Auto-generated method stub
				System.out.println("changeMask = " + changeMask);
				if ((changeMask & Client.CHANGE_HEAP_ALLOCATIONS) != 0) {
					if (client.isHeapUpdateEnabled()) {
						ClientData clientData = client.getClientData();
						if (clientData != null) {
							Iterator<Integer> heapIds = clientData.getVmHeapIds();
							while (heapIds.hasNext()) {
								Integer integer = (Integer) heapIds.next();
								Map<String, Long> vmData = clientData.getVmHeapInfo(integer);
								if (vmData != null && vmData.size() > 0) {
									for (String key : vmData.keySet()) {
										System.out.println("key = " + key + ", value=" + vmData.get(key));
									}
								}
							}
						}
					}
				}
			}

		});
	}
}
