package com.xdja.constant;

import java.io.File;

public class Constants {
	// �����ռ��Ŀ¼
	public static final String RESOURCES = "resources";
	public static final String LOCAL_PATH = System.getProperty("user.dir");
	public static final String LOG4J_PATH = Constants.LOCAL_PATH + File.separator + RESOURCES + "\\log4j.properties";
	
	// resĿ¼
	public static final String RES_PATH = System.getProperty("user.dir") + File.separator + RESOURCES;
	public static final String ADB_PATH = LOCAL_PATH + File.separator + RESOURCES + "\\bin\\adb.exe";
	
	public static final String IMG_PATH = LOCAL_PATH + File.separator + RESOURCES + File.separator + "image" + File.separator;
	//log_xDevice
	public final static String COLON_BLANK = ": ";
	public final static String COMMA_BLANK = ", ";
	public final static String COLOM = ":";
	public final static String BLANK = "";
	public final static String SPACE = " ";
	public final static String KB = "kB:";
	public final static String BRACKET = "[";
	public final static String SEPERATOR = "/";
	
	//new
	public static final String AUTHOR = "xdja";
	public static final String PRODUCT_NAME = "Android���ܲ��Թ���";
	public static final String MONKEY = "monkey test";
	public static final String LAUNCH_COST = "launch cost";
	public static final String FPS_VIEW = "fps view";
	public static final String LAUNCH_COST_TEST = "launch cost test";
	public static final String START_TEST = "start test";
	public static final String STOP_TEST = "stop test";
	public static final String CONFIRM = "ȷ��";
	public static final String CANCEL = "ȡ��";
	public static final String TIME_UNIT = "ʱ��(��)";
	public static final String PACKAGE_NAME = "package name:";
	public static final String ACTIVITY_NAME = "activity name:";
	public static final String LOOPS = "loops:";
	public static final String SELECT_PACKAGE = "please select package name";
	public static final String PACKAGE_NAME_NULL = "package name can't be null!";
	public static final String DEVICE_NULL = "device is not selected!";
	public static final String PACKAGE = "package:";
	public static final String TOTAL_TIME = "TotalTime:";
	public static final String WAIT_TIME = "WaitTime:";
	public static final String ADJUSTING = "is_adjusting";
	
	public static final String MEMORY = "Memory";
	public static final String MEMORYContent = "�ڴ�ֵ�仯";
	public static final String MEMORY_UNIT = "kb";
	
	public static final String FLOW = "Flow";
	public static final String FLOW_UNIT = "kb/s";
	public static final String FLOW_KEYWORD = "wlan0:";
	
	public static final String CPU = "Cpu";
	public static final String CPU_UNIT = "%";
	
	public static final String BATTERY = "Battery";
	public static final String BATTERY_UNIT = "mAh";
	public static final String USB_POWERED = "Usb Powered";
	public static final String LEVEL = "level:";
	public static final String SCALE = "scale:";
	
	public static final String FPSTITLE = "֡�ʲ��Խ��";
	public static final String FPS = "FPS";
	public static final String FPS_UNIT = "����";
	
	public static final String KPITITLE = "kpi���Խ��";
	public static final String KPI = "ҳ�����ʱ��(ms)";
	public static final String KPI_UNIT = "ms";
	
	public static final String ABOUT = "���ڲ���Android����";
	public static final String HELP = "��ʹ�ù���֮ǰ����Ҫ�ص������Ĺ��߱��磺AndroidStudio��Eclipse�� \n ��Ȼ��Թ��ߵ�ʹ�����Ӱ��";
	
	// ProPerties ����
	public static final String LAST_PACKAGENAME = "lastPackageName";
	public static final String CHOOSE_PACKAGE = "choosePackage";
	
	//�洢��ͼ��dump memory��·��
	public static final String SCREEN_SHOTS = "screenshots";
	public static final String MEMORY_DUMP  = "memorydump";
	public static final String ANDROID_LOG  = "androidLog";
	public static final String METHOD_TRACE = "methodTrace";
	
	// �����ͼ��ʱ��Ҫָ���Ĳ�������
	public static final String TYPE_FPS = "fps";
	public static final String TYPE_FLOW = "flow";
	public static final String TYPE_SLIENT_FLOW = "flowSlient";
	public static final String TYPE_CPU = "cpu";
	public static final String TYPE_SLIENT_CPU = "cpuSlient";
	public static final String TYPE_KPI = "kpi";
	public static final String TYPE_MEMORY = "memory";
	public static final String TYPE_BATTERY = "battery";
	
	// �������ݿ������
	public static final String FPS_TABLE  = "performance_fpsdata";
	public static final String FLOW_TABLE = "performance_flowdata";
	public static final String SLIENT_FLOW_TABLE = "performance_flowsilentdata";
	public static final String CPU_TABLE  = "performance_cpudata";
	public static final String SLIENT_CPU_TABLE = "performance_cpusilentdata";
	public static final String KPI_TABLE  = "performance_kpidata";
	public static final String MEMORY_TABLE  = "performance_memorydata";
	public static final String BATTERY_TABLE = "performance_batterydata";
	public static final String COMMON_TABLE  = "performance_commondata";
	
	// Properties ��������
	public static final String TABLEURL = "tableUrl";
	public static final String DBURL = "dbUrl";
	public static final String DBUSERNAME = "dbuserName";
	public static final String DBPASSWD = "dbpasswd";
	
}
