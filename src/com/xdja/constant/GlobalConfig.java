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
	
	// Ĭ�ϵ����ܱ�׼
	public static final int MEMORY_MAX = 40;
	// 10s�ڶ���4��
	public static final int MEMORY_SHAKE_COUNT = 4;
	// cpu���ֵ(Ĭ�ϲ�����40%)
	public static final int CPU_MAX = 40;
	// cpu��ĬĬ��ֵ
	public static final int SILENT_CPU_MAX = 0;
	// kpiĬ�����ֵ��ms��
	public static final int MAX_KPI_DATA = 2000;
	// fpsĬ�����ֵ��fps��
	public static final int MIN_FPS_DATA = 40;
	// ����Ĭ�����ֵ(KB)
	public static final int MAX_FLOW_DATA = 1024;
	public static final int SILENT_FLOW_DATA = 0;
	
	
//	/**
//	 *  ��ȡ��ǰ���Եİ���
//	 * @return
//	 */
	public static String getTestPackageName(){
		String packageName = ProPertiesUtil.getInstance().getProperties(Constants.CHOOSE_PACKAGE);
		return packageName;
	}
}
