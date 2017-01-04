package com.xdja.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xdja.adb.AndroidSdk;
import com.xdja.constant.Constants;

/***
 * ���ڱ�����Գ����Ĺ�����
 * 
 * @author zlw
 *
 */
public class SaveEnvironmentUtil {
	private static SaveEnvironmentUtil mInstance = null;
	
	private SaveEnvironmentUtil(){}
	
	public static SaveEnvironmentUtil getInstance(){
		if (mInstance == null) {
			synchronized (SaveEnvironmentUtil.class) {
				if (mInstance == null) {
					mInstance = new SaveEnvironmentUtil();
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
		return "unkonw" + "_" + timestamp;
	}

	/**
	 * ץȡhprof
	 * @param data
	 *            ץȡ��hprof����
	 * @param type
	 *            ��������
	 */
	public void writeHprofToLocal(byte[] data, String type) {
		if (data == null) {
			return;
		}

		String fileName = getSuggestedName(type);
		CommonUtil.writeDataToLocal(data, Constants.MEMORY_DUMP, fileName + ".hprof");
		//ת��hprof�ĸ�ʽ
		covertHprof(Constants.MEMORY_DUMP + File.separator + fileName + ".hprof");
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
	
	public static void main(String[] args) {
		SaveEnvironmentUtil.getInstance().covertHprof("memorydump/battery_2017.01.04_15.25.53.hprof");
	}
}
