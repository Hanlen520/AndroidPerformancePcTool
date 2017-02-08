package com.xdja.constant;

import com.xdja.util.ProPertiesUtil;

/***
 *  ȫ�ֵ�һЩ������Ϣ����Constants���ֿ���
 * @author zlw
 *
 */
public class GlobalConfig {
	// �ɼ����ݵĴ���
	public static final int collectDataCount = 20;
	
	// �ɼ����ݵ�ʱ��
	public static final int collectDataTime  = 20;
	
	// �ɼ����ݵ�ʱ����
	public static final int collectInterval  = 1000;
	public static final int collectMIDDLEInterval  = 1000;
	public static final int collectLONGInterval  = 1500;
	
	public static final int DEBUGPORT = 1111;
	public static final int BASEPORT = 7500;
	
	// ���Եİ汾�źͰ���
	public static String TestVersion = "";
	public static String TestPackageName = "";
	
	// ѡ�е��豸����
	public static String DeviceName = "";
	
//	/**
//	 *  ��ȡ��ǰ���Եİ���
//	 * @return
//	 */
	public static String getTestPackageName(){
		String packageName = ProPertiesUtil.getInstance().getProperties(Constants.CHOOSE_PACKAGE);
		return packageName;
	}
}
