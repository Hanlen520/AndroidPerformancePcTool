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
	
	
	// ѡ�е��豸����
	public static String DeviceName = "";
	
	// ���ݿ�����
//	public static String DBUSERNAME = "xdja";
//	public static String DBUSERPWD  = "123456";
	public static String DBUSERNAME = "root";
	public static String DBUSERPWD  = "";
//	
	/**
	 *  ��ȡ��ǰ���Եİ���
	 * @return
	 */
	public static String getTestPackageName(){
		String packageName = ProPertiesUtil.getInstance().getProperties(Constants.CHOOSE_PACKAGE);
		return packageName;
	}
}
