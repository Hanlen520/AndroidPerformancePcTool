package com.xdja.collectdata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.xdja.adb.AdbManager;
import com.xdja.adb.AndroidSdk;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.util.CommonUtil;

/***
 * ���ڱ�����Գ����Ĺ�����
 * 
 * @author zlw
 *
 */
public class SaveEnvironmentManager {
	private static SaveEnvironmentManager mInstance = null;
	private static Thread mSaveLogThread;
	private static LogRunnable mSaveLogRunnable;
	
	private SaveEnvironmentManager(){}
	
	public static SaveEnvironmentManager getInstance(){
		if (mInstance == null) {
			synchronized (SaveEnvironmentManager.class) {
				if (mInstance == null) {
					mInstance = new SaveEnvironmentManager();
				}
			}
		}
		
		return mInstance;
	}
	/**
	 * �����ļ�ʱ������
	 * 
	 * @param type
	 *            ���Ե�����
	 * @return
	 */
	public String getSuggestedName(String type) {
		String timestamp = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
		if (!CommonUtil.strIsNull(type)) {
			return type + "_" + timestamp;
		}
		return "default" + "_" + timestamp;
	}

	/**
	 * ץȡhprof
	 * @param data
	 *            ץȡ��hprof����
	 * @param type
	 *            ��������
	 */
	public String writeHprofToLocal(byte[] data, String type) {
		if (data == null) {
			return "";
		}

		String fileName = getSuggestedName(type);
		String filePath = Constants.MEMORY_DUMP + File.separator + fileName + ".hprof";
		CommonUtil.writeDataToLocal(data, Constants.MEMORY_DUMP, fileName + ".hprof");
		//ת��hprof�ĸ�ʽ
		covertHprof(filePath);
		
		return filePath;
	}
	
	
	/**
	 * ץȡhprof
	 * @param data
	 *            ץȡ��hprof����
	 * @param type
	 *            ��������
	 * @return �����ļ���ŵ�·��
	 */
	public String writeTraceToLocal(byte[] data, String type) {
		if (data == null) {
			return "";
		}

		String fileName = getSuggestedName(type);
		String filePath = Constants.METHOD_TRACE + File.separator + fileName + ".trace";
		CommonUtil.writeDataToLocal(data, Constants.METHOD_TRACE, fileName + ".trace");
		
		return filePath;
	}
	
	
	/**
	 * ��hprofPath��Ӧ��hprof�ļ�ת����MAT��ʶ��ĸ�ʽ 
	 * ��-z������Ϊ�˱���ϵͳ�����ĸ���
	 * @param hprofPath
	 */
	private void covertHprof(String hprofPath){
		String cmdPath = AndroidSdk.hprofConv().getAbsolutePath();
		if (!CommonUtil.strIsNull(cmdPath)) {
			String cmd = cmdPath + " -z " + hprofPath + " " + hprofPath + ".temp";
			CommonUtil.execCmd(cmd, false);
		}
		
		File file = new File(hprofPath);
		File tempFile = new File(hprofPath + ".temp");
		if (file.exists()) {
			file.delete();
		}
		
		if (tempFile.exists()) {
			tempFile.renameTo(file);
		}
	}
	
	/**
	 * ���浱ǰ����־��Ϣ
	 * @param type ���Ե�����
	 */
	public void saveCurrentLog(int pid, String type){
		
		if (mSaveLogThread != null && mSaveLogThread.isAlive()) {
			return;
		}
		mSaveLogRunnable = new LogRunnable(pid, Constants.ANDROID_LOG, type);
		mSaveLogThread = new Thread(mSaveLogRunnable);
		mSaveLogThread.start();
		startTimer();
	}
	/**
	 *  ����һ����ʱ������Ƿ�ֹͣ����
	 */
	private void startTimer(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ���ٽ���
				if (mSaveLogRunnable != null) {
					mSaveLogRunnable.destoryProcess();
				}
				
				// ֹͣ�߳�
				if (mSaveLogThread != null) {
					if (mSaveLogThread.isAlive()) {
						System.out.println("mSaveLogThread isAlive");
						mSaveLogThread.interrupt();
						mSaveLogThread = null;
					}
				}
			}
		}, 30*1000);
	}
	
	
	/**
	 *  ���ڱ��浱ǰ�Ĳ�����־
	 * @param packageName
	 * @param testType ���Ե�����
	 */
	public void saveCurrentLog(String packageName, String testType){
		int pid = 0;
		if (CommonUtil.strIsNull(packageName)) {
			pid = 0;
		}
		pid = CollectDataImpl.getPid(packageName);
		
		saveCurrentLog(pid, testType);
	}
	
	
	/**
	 * ��ͼ
	 * @param deviceName  ��ǰ�豸������
	 * @param testType
	 */
	public void screenShots(String deviceName, String testType){
		AdbManager.getInstance().screenCapture(deviceName, testType, false);
	}
	
	/**
	 *  dump memory
	 * @param deviceName
	 * @param packageName
	 * @param type
	 */
	public void dumpMemory(String deviceName, String packageName,  String type){
		AdbManager.getInstance().dumpMemory(deviceName, packageName, type);
	}
	
	/**
	 *  ץȡ��ǰ�ķ���ջ
	 * @param deviceName
	 * @param packageName
	 * @param type
	 */
	public void methodTracing(String deviceName, String packageName, String type){
		AdbManager.getInstance().memthodTracing(deviceName, packageName, type);
	}
	
	
	public static void main(String[] args) {
		int pid = CollectDataImpl.getPid(GlobalConfig.PackageName);
		SaveEnvironmentManager.getInstance().saveCurrentLog(pid, Constants.TYPE_BATTERY);
	}
}
