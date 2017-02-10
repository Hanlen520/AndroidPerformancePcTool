package com.xdja.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.xdja.collectdata.handleData.entity.BatteryHandleResult;
import com.xdja.collectdata.handleData.entity.CpuHandleResult;
import com.xdja.collectdata.handleData.entity.FlowHandleResult;
import com.xdja.collectdata.handleData.entity.FpsHandleResult;
import com.xdja.collectdata.handleData.entity.KpiHandleResult;
import com.xdja.collectdata.handleData.entity.MemoryHandleResult;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.exception.SettingException;
import com.xdja.util.CommonUtil;
import com.xdja.util.ExcelUtil;
import com.xdja.util.ProPertiesUtil;

/**
 * ��дExcel���Ĺ�����
 * 
 * @author zlw
 */
public class SaveLocalManager {

	private static SaveLocalManager mInstance = null;
	
	private String mFilePath;
	private ExcelUtil mExcelUtil;
	// sheetName
	private final static String MEMORY_SHEET = "�ڴ�";
	private final static String CPU_SHEET = "cpu";
	private final static String kpi_SHEET = "ҳ�����ʱ��";
	private final static String FPS_SHEET = "ҳ��������";
	private final static String BATTERY_SHEET = "����ʹ��";
	private final static String FLOW_SHEET = "����";
	private final static String SILENT_FLOW_SHEET = "��Ĭ����";
	private final static String SILENT_CPU_SHEET = "��ĬCPU";
	
	// titles
	private final static String[] MEMORY_TITLES = {"ҳ������","�ڴ��Ѿ�����ֵ(MB)","��־·��", "�ڴ����·��", "���Խ��"};
	private final static String[] FLOW_TITLES = {"ҳ������","��������ֵ(KB)","��־·��", "���Խ��"};
	private final static String[] FPS_TITLES = {"ҳ������","ҳ��������(fps)","��֡��", "��֡��", "��־·��", "��������·��trace", "���Խ��"};
	private final static String[] KPI_TITLES = {"ҳ������","ҳ�����ʱ��(ms)","��־·��", "��������ջ", "���Խ��"};
	private final static String[] CPU_TITLES = {"ҳ������","cpuʹ����(%)","��־·��", "��������ջ", "���Խ��"};
	private final static String[] BATTERY_TITLES = {"������","���ĵ���ֵ(mAh)","��ϸ�ĵ���Ϣ"};
	private final static String[] CPU_SILENT_TITLES = {"ҳ������", "cpuʹ����(%)", "��־·��", "��������ջ"};
	private final static String[] FLOW_SILENT_TITLES = {"ҳ������", "��������ֵ(KB)", "��־·��"};
	
	private SaveLocalManager() {
		mFilePath = getFilePath(formatExcelFileName(GlobalConfig.TestPackageName, GlobalConfig.TestVersion));
		mExcelUtil = new ExcelUtil(mFilePath);
	}

	public static SaveLocalManager getInstance() {
		if (mInstance == null) {
			synchronized (SaveLocalManager.class) {
				if (mInstance == null) {
					mInstance = new SaveLocalManager();
				}
			}
		}

		return mInstance;
	}
	
	/**
	 * 
	 * @param packageName
	 * @param version
	 * @return
	 */
	private String formatExcelFileName(String packageName, String version){
		if (CommonUtil.strIsNull(packageName)) {
			packageName = "com.xdja.xxx";
		}
		
		if (CommonUtil.strIsNull(version)) {
			version = "0.0.0.0";
		}
		
		String fileName = packageName + "_" + version;
		
		return fileName;
	}
	
	/**
	 *  ��ȡ�洢�ļ���·��
	 * @param fileName
	 * @return
	 */
	private String getFilePath(String fileName){
		String settingPath = ProPertiesUtil.getInstance().getProperties(Constants.LOCALSAVE_SETTING);
		if (CommonUtil.strIsNull(settingPath)) {
			settingPath = System.getProperty("user.home");
			settingPath = settingPath + "\\performance\\";
		}
		
		if (CommonUtil.strIsNull(fileName)) {
			return settingPath + "\\" +"default.xlsx";
		}
		
		File file = new File(settingPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		return settingPath +"\\"+ fileName + ".xlsx";
	}
	
	/**
	 * ������԰����Ͱ汾��
	 * @param packageName
	 * @param version
	 */
	public void setTestPackageAndVersion(String packageName, String version){
//		mFilePath = getFilePath(formatExcelFileName(packageName, version));
//		mExcelUtil = new ExcelUtil(mFilePath);
	}
	
	/**
	 *  ���ڴ���Խ�����浽����
	 * @param handleResults
	 * @throws SettingException 
	 */
	public void saveMemoryDataToLocal(List<MemoryHandleResult> handleResults) throws SettingException{
		if (CommonUtil.strIsNull(mFilePath)) {
			throw new SettingException("���ȵ���setTestPackageAndVersion�������ñ�������Ͱ汾��");
		}
		if (handleResults == null || handleResults.size() < 1) {
			return;
		}
		
		List<String[]> tempList = new ArrayList<>(handleResults.size());
		for(MemoryHandleResult handleResult : handleResults){
			tempList.add(handleResult.formatDataToArray());
		}
		
		mExcelUtil.writeDataToExcel(MEMORY_SHEET, MEMORY_TITLES, tempList);
	}
	
	/**
	 *  �洢ҳ�����ʱ������
	 * @param handleResults
	 * @throws SettingException 
	 */
	public void saveKpiDataToLocal(List<KpiHandleResult> handleResults) throws SettingException{
		if (CommonUtil.strIsNull(mFilePath)) {
			throw new SettingException("���ȵ���setTestPackageAndVersion�������ñ�������Ͱ汾��");
		}
		if (handleResults == null || handleResults.size() < 1) {
			return;
		}
		
		List<String[]> tempList = new ArrayList<>(handleResults.size());
		for(KpiHandleResult handleResult : handleResults){
			tempList.add(handleResult.formatDataToArray());
		}
		
		mExcelUtil.writeDataToExcel(kpi_SHEET, KPI_TITLES, tempList);
	}
	
	/**
	 *  ����cpu���ݵĽ��
	 * @param handleResults
	 * @throws SettingException 
	 */
	public void saveCpuDataToLocal(List<CpuHandleResult> handleResults) throws SettingException{
		if (CommonUtil.strIsNull(mFilePath)) {
			throw new SettingException("���ȵ���setTestPackageAndVersion�������ñ�������Ͱ汾��");
		}
		
		if (handleResults == null || handleResults.size() < 1) {
			return;
		}
		
		List<String[]> tempList = new ArrayList<>(handleResults.size());
		for(CpuHandleResult handleResult : handleResults){
			tempList.add(handleResult.formatDataToArray());
		}
		
		mExcelUtil.writeDataToExcel(CPU_SHEET, CPU_TITLES, tempList);
	}
	
	/**
	 *  ����cpu���ݵĽ��
	 * @param handleResults
	 * @throws SettingException 
	 */
	public void saveFpsDataToLocal(List<FpsHandleResult> handleResults) throws SettingException{
		if (CommonUtil.strIsNull(mFilePath)) {
			throw new SettingException("���ȵ���setTestPackageAndVersion�������ñ�������Ͱ汾��");
		}
		
		if (handleResults == null || handleResults.size() < 1) {
			return;
		}
		
		List<String[]> tempList = new ArrayList<>(handleResults.size());
		for(FpsHandleResult handleResult : handleResults){
			tempList.add(handleResult.formatDataToArray());
		}
		
		mExcelUtil.writeDataToExcel(FPS_SHEET, FPS_TITLES, tempList);
	}
	
	/**
	 *  ����cpu���ݵĽ��
	 * @param handleResults
	 * @throws SettingException 
	 */
	public void saveFlowDataToLocal(List<FlowHandleResult> handleResults) throws SettingException{
		if (CommonUtil.strIsNull(mFilePath)) {
			throw new SettingException("���ȵ���setTestPackageAndVersion�������ñ�������Ͱ汾��");
		}
		if (handleResults == null || handleResults.size() < 1) {
			return;
		}
		
		List<String[]> tempList = new ArrayList<>(handleResults.size());
		for(FlowHandleResult handleResult : handleResults){
			tempList.add(handleResult.formatDataToArray());
		}
		
		mExcelUtil.writeDataToExcel(FLOW_SHEET, FLOW_TITLES, tempList);
	}
	
	/**
	 *  ����cpu���ݵĽ��
	 * @param handleResults
	 * @throws SettingException 
	 */
	public void saveBatteryDataToLocal(List<BatteryHandleResult> handleResults) throws SettingException{
		if (CommonUtil.strIsNull(mFilePath)) {
			throw new SettingException("���ȵ���setTestPackageAndVersion�������ñ�������Ͱ汾��");
		}
		
		if (handleResults == null || handleResults.size() < 1) {
			return;
		}
		
		List<String[]> tempList = new ArrayList<>(handleResults.size());
		for(BatteryHandleResult handleResult : handleResults){
			tempList.add(handleResult.formatDataToArray());
		}
		
		mExcelUtil.writeDataToExcel(BATTERY_SHEET, BATTERY_TITLES, tempList);
	}
	
	
	/**
	 *  ���澲Ĭ�����Ĳ��Խ��
	 * @param handleResults
	 * @throws SettingException 
	 */
	public void saveSilentFlowDataToLocal(List<FlowHandleResult> handleResults) throws SettingException{
		if (CommonUtil.strIsNull(mFilePath)) {
			throw new SettingException("���ȵ���setTestPackageAndVersion�������ñ�������Ͱ汾��");
		}
		
		if (handleResults == null || handleResults.size() < 1) {
			return;
		}
		
		List<String[]> tempList = new ArrayList<>(handleResults.size());
		String[] tempArray = null;
		for(FlowHandleResult handleResult : handleResults){
			tempArray = new String[]{handleResult.activityName, handleResult.testValue, handleResult.logPath};
			tempList.add(tempArray);
		}
		
		mExcelUtil.writeDataToExcel(SILENT_FLOW_SHEET, FLOW_SILENT_TITLES, tempList);
	}
	
	/**
	 *  ���澲Ĭ�����Ĳ��Խ��
	 * @param handleResults
	 * @throws SettingException 
	 */
	public void saveSilentCpuDataToLocal(List<CpuHandleResult> handleResults) throws SettingException{
		if (CommonUtil.strIsNull(mFilePath)) {
			throw new SettingException("���ȵ���setTestPackageAndVersion�������ñ�������Ͱ汾��");
		}
		
		if (handleResults == null || handleResults.size() < 1) {
			return;
		}
		
		List<String[]> tempList = new ArrayList<>(handleResults.size());
		String[] tempArray = null;
		for(CpuHandleResult handleResult : handleResults){
			tempArray = new String[]{handleResult.activityName, handleResult.testValue, handleResult.logPath, handleResult.methodTracePath};
			tempList.add(tempArray);
		}
		
		mExcelUtil.writeDataToExcel(SILENT_CPU_SHEET, CPU_SILENT_TITLES, tempList);
	}
}
