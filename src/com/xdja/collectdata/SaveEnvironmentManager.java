package com.xdja.collectdata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xdja.adb.AdbManager;
import com.xdja.adb.AndroidSdk;
import com.xdja.collectdata.thread.SaveLogThread;
import com.xdja.constant.Constants;
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
	 * 
	 * @param pathType �����������ݵ�path
	 * @param testtype ���е����ֲ���
	 * @param regx  �����ļ��ĺ�׺
	 * @return
	 */
	public String getSuggestedPath(String pathType,  String testtype, String regx){
		String seggestName = getSuggestedName(testtype);
		if (CommonUtil.strIsNull(pathType)) {
			pathType = "commonFile";
		}
		
		// Ĭ�������.zip��ʱ
		if (CommonUtil.strIsNull(regx)) {
			regx = ".zip";
		}
		
		// �ж�path�Ƿ���ڣ�������Ҫnew
		File file = new File(pathType);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		String filePath = pathType + File.separator + seggestName + regx;
		
		return filePath;
	}
	
	/**
	 * ץȡhprof
	 * @param data
	 *            ץȡ��hprof����
	 * @param type
	 *            ��������
	 */
	public String writeHprofToLocal(byte[] data, String filePath) {
		if (data == null) {
			return "";
		}

		CommonUtil.writeDataToLocal(data, filePath);
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
	public String writeTraceToLocal(byte[] data, String filePath) {
		if (data == null) {
			return "";
		}

		CommonUtil.writeDataToLocal(data, filePath);
		
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
	 * @return �ļ�·��  �����п��ܳ��ִ���֮���ļ�������null��
	 */
	public String saveCurrentLog(String deviceName, String packageName, String type){
		String filePath = getSuggestedPath(Constants.ANDROID_LOG, type, ".log");
		mSaveLogThread = new SaveLogThread(deviceName, packageName, type);
		mSaveLogThread.start();
		
		return filePath;
	}
	
	
	
	/**
	 * ��ͼ
	 * @param deviceName  ��ǰ�豸������
	 * @param testType
	 * @return �����ļ���·��
	 */
	public String screenShots(String deviceName, String testType){
		String filePath = getSuggestedPath(Constants.SCREEN_SHOTS, testType, ".png");
		AdbManager.getInstance().screenCapture(deviceName, filePath, false);
		
		return filePath;
	}
	
	/**
	 *  dump memory
	 * @param deviceName
	 * @param packageName
	 * @param type
	 */
	public String dumpMemory(String deviceName, String packageName,  String type){
		String filePath = getSuggestedPath(Constants.MEMORY_DUMP, type, ".hprof");
		AdbManager.getInstance().dumpMemory(deviceName, packageName, filePath);
		
		return filePath;
	}
	
	/**
	 *  ץȡ��ǰ�ķ���ջ
	 * @param deviceName
	 * @param packageName
	 * @param type
	 */
	public String methodTracing(String deviceName, String packageName, String type){
		String filePath = getSuggestedPath(Constants.METHOD_TRACE, type, ".trace");
		AdbManager.getInstance().memthodTracing(deviceName, packageName, filePath);
		
		return filePath;
	}
}
