package com.xdja.collectdata;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xdja.collectdata.entity.BaseTestInfo;
import com.xdja.collectdata.entity.BatteryData;
import com.xdja.collectdata.entity.CommandResult;
import com.xdja.collectdata.handleData.HandleDataManager;
import com.xdja.collectdata.handleData.entity.BatteryHandleResult;
import com.xdja.database.PerformanceDB;
import com.xdja.util.CommonUtil;
import com.xdja.util.ExecShellUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

//�������ļ���Ŀ¼
public class CCRDFile {
	// ��֤�ַ����Ƿ�Ϊ��ȷ·������������ʽ
	private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";
	// ͨ�� sPath.matches(matches) �����ķ���ֵ�ж��Ƿ���ȷ
	// sPath Ϊ·���ַ���
	boolean flag = false;
	private static BatteryData batteryData = null;
	private static String nowTestPackageName = "";
	
	public boolean DeleteFolder(String deletePath) {// ����·��ɾ��ָ����Ŀ¼���ļ������۴������
		flag = false;
		if (deletePath.matches(matches)) {
			File file = new File(deletePath);
			if (!file.exists()) {// �ж�Ŀ¼���ļ��Ƿ����
				return flag; // �����ڷ��� false
			} else {

				if (file.isFile()) {// �ж��Ƿ�Ϊ�ļ�
					return deleteFile(deletePath);// Ϊ�ļ�ʱ����ɾ���ļ�����
				} else {
					return deleteDirectory(deletePath);// ΪĿ¼ʱ����ɾ��Ŀ¼����
				}
			}
		} else {
			System.out.println("Ҫ������ȷ·����");
			return false;
		}
	}

	public boolean deleteFile(String filePath) {// ɾ�������ļ�
		flag = false;
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {// ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��
			file.delete();// �ļ�ɾ��
			flag = true;
		}
		return flag;
	}

	public boolean deleteDirectory(String dirPath) {// ɾ��Ŀ¼���ļ��У��Լ�Ŀ¼�µ��ļ�
		// ���sPath�����ļ��ָ�����β���Զ�����ļ��ָ���
		if (!dirPath.endsWith(File.separator)) {
			dirPath = dirPath + File.separator;
		}
		File dirFile = new File(dirPath);
		// ���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();// ��ô���·���µ������ļ�
		for (int i = 0; i < files.length; i++) {// ѭ������ɾ���ļ����µ������ļ�(������Ŀ¼)
			if (files[i].isFile()) {// ɾ�����ļ�
				flag = deleteFile(files[i].getAbsolutePath());
				System.out.println(files[i].getAbsolutePath() + " ɾ���ɹ�");
				if (!flag)
					break;// ���ɾ��ʧ�ܣ�������
			} else {// ���õݹ飬ɾ����Ŀ¼
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;// ���ɾ��ʧ�ܣ�������
			}
		}
		if (!flag)
			return false;
		if (dirFile.delete()) {// ɾ����ǰĿ¼
			return true;
		} else {
			return false;
		}
	}

	// ���������ļ�
	public static boolean createFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {// �ж��ļ��Ƿ����
			System.out.println("Ŀ���ļ��Ѵ���" + filePath);
			return false;
		}
		if (filePath.endsWith(File.separator)) {// �ж��ļ��Ƿ�ΪĿ¼
			System.out.println("Ŀ���ļ�����ΪĿ¼��");
			return false;
		}
		if (!file.getParentFile().exists()) {// �ж�Ŀ���ļ����ڵ�Ŀ¼�Ƿ����
			// ���Ŀ���ļ����ڵ��ļ��в����ڣ��򴴽����ļ���
			System.out.println("Ŀ���ļ�����Ŀ¼�����ڣ�׼����������");
			if (!file.getParentFile().mkdirs()) {// �жϴ���Ŀ¼�Ƿ�ɹ�
				System.out.println("����Ŀ���ļ����ڵ�Ŀ¼ʧ�ܣ�");
				return false;
			}
		}
		try {
			if (file.createNewFile()) {// ����Ŀ���ļ�
				System.out.println("�����ļ��ɹ�:" + filePath);
				return true;
			} else {
				System.out.println("�����ļ�ʧ�ܣ�");
				return false;
			}
		} catch (IOException e) {// �����쳣
			e.printStackTrace();
			System.out.println("�����ļ�ʧ�ܣ�" + e.getMessage());
			return false;
		}
	}

	// ����Ŀ¼
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {// �ж�Ŀ¼�Ƿ����
			System.out.println("����Ŀ¼ʧ�ܣ�Ŀ��Ŀ¼�Ѵ��ڣ�");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {// ��β�Ƿ���"/"����
			destDirName = destDirName + File.separator;
		}
		if (dir.mkdirs()) {// ����Ŀ��Ŀ¼
			System.out.println("����Ŀ¼�ɹ���" + destDirName);
			return true;
		} else {
			System.out.println("����Ŀ¼ʧ�ܣ�");
			return false;
		}
	}

	// ������ʱ�ļ�
	public static String createTempFile(String prefix, String suffix, String dirName) {
		File tempFile = null;
		if (dirName == null) {// Ŀ¼���Ϊ��
			try {
				tempFile = File.createTempFile(prefix, suffix);// ��Ĭ���ļ����´�����ʱ�ļ�
				return tempFile.getCanonicalPath();// ������ʱ�ļ���·��
			} catch (IOException e) {// �����쳣
				e.printStackTrace();
				System.out.println("������ʱ�ļ�ʧ�ܣ�" + e.getMessage());
				return null;
			}
		} else {
			// ָ��Ŀ¼����
			File dir = new File(dirName);// ����Ŀ¼
			if (!dir.exists()) {
				// ���Ŀ¼�������򴴽�Ŀ¼
				if (CCRDFile.createDir(dirName)) {
					System.out.println("������ʱ�ļ�ʧ�ܣ����ܴ�����ʱ�ļ����ڵ�Ŀ¼��");
					return null;
				}
			}
			try {
				tempFile = File.createTempFile(prefix, suffix, dir);// ��ָ��Ŀ¼�´�����ʱ�ļ�
				return tempFile.getCanonicalPath();// ������ʱ�ļ���·��
			} catch (IOException e) {// �����쳣
				e.printStackTrace();
				System.out.println("������ʱ�ļ�ʧ��!" + e.getMessage());
				return null;
			}
		}
	}

	// get_package_name_by_uid��ȡ��ǰӦ�ð���//
	public static String get_package_name_by_uid(String uid) {
		if (CommonUtil.strIsNull(uid)) {
			return "";
		}

		String packagename = "";
		if (uid.contains("u0")) {
			uid = "u0_" + uid.substring(2);
		}
		String cmd = "adb shell ps | grep " + uid;
		CommandResult data = ExecShellUtil.getInstance().execCmdCommand(cmd, false, true);
		String result = null;
		if (data.successMsg != null) {
			result = data.successMsg;
			String pattern = "com.*";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(result);
			if (m.find()) {
				packagename = m.group(0);
			} else {
				// System.out.println("NO MATCH do");
			}
			result = packagename;
		}
		return result;

	}

	// @@get_battery_data����ȡ��������@@//
	public static CommandResult get_battery_data(String pkg_name) {
		String cmd;
		if (pkg_name == null || pkg_name == "") {
			cmd = "dumpsys batterystats";
		} else {
			cmd = "dumpsys batterystats " + pkg_name;
		}
		CommandResult data = ExecShellUtil.getInstance().execShellCommand(cmd);
		if ((data != null)) {
			return data;
		} else {
			return null;
		}
	}

	// @@writetofile��д��cmd������Ľ����demo�ļ���@@//
	public static void writetofile(String a) throws IOException {
		String oldName = System.getProperty("user.dir");
		String filepath = oldName + "/powerresult/";
		File file = new File(filepath);
		if (!file.exists()) {
			file.mkdir();// �����ļ���
		}
		// CCRDFile.createDir(filepath);// ���÷�������Ŀ¼
		String filedemopath = oldName + "/powerresult/batteryData.txt";
		File filedemo = new File(filedemopath);
		if (!filedemo.exists()) {
			FileWriter fw = new FileWriter(filepath + "batteryData.txt", true);// �����ļ�
			fw.write(a);
			fw.close();
		} else {
			new CCRDFile().deleteFile(filedemopath);
			FileWriter fw = new FileWriter(filepath + "batteryData.txt", true);// �����ļ�
			fw.write(a);
			fw.close();
		}
	}

	// ������ʽ������//
	private static String handlere(String powerdata) {
		String line = powerdata;
		String resu = null;
		String pattern = "\\(mAh\\):([\\s\\S]*)";
		// ���� Pattern ����
		Pattern r = Pattern.compile(pattern);
		// ���ڴ��� matcher ����
		Matcher m = r.matcher(line);
		// System.out.println(m.find());
		if (m.find()) {
			resu = (String) m.group(1);
		} else {
			System.out.println("NO MATCH");
		}
		return resu;

	}

	/**
	 * ����ɼ����ĵ�������
	 * 
	 * @param message
	 * @return
	 */
	public static List<BatteryData> handlePowerData(String message) {
		List<BatteryData> batteryDatas = new ArrayList<>(12);
		if (CommonUtil.strIsNull(message)) {
			return batteryDatas;
		}

		// ƥ�����ǹ��ĵ�����
		String result = handlere(message);
		if (CommonUtil.strIsNull(result)) {
			return batteryDatas;
		}

		String[] batterys = result.split("\n\n");
		float computerDrain = 0;
		String actualDrain = "";
		String[] captureValue = batterys[0].trim().split("\n");
		String detailInfo = batterys[1].trim();
		for (int i = 0; i < captureValue.length; i++) {
			batteryData = new BatteryData();
			if (i == 0) {
				String[] totalBatterys = captureValue[0].split(",");
				computerDrain = Float.parseFloat(totalBatterys[1].split(":")[1].trim());
				actualDrain = totalBatterys[2].split(":")[1].trim();
				if (computerDrain > 0) {
					batteryData.setBatteryValue(String.valueOf(computerDrain));
					batteryData.setAppPackageName("Computed drain");
					batteryDatas.add(batteryData);
				}

				if (!"".equals(actualDrain)) {
					batteryData = new BatteryData();
					batteryData.setBatteryValue(actualDrain);
					// ����һ��Ĳ�����uid��������uid���������
					batteryData.setAppPackageName("actual drain");
					batteryDatas.add(batteryData);
				}
				continue;
			}

			// �ж��ַ������Ƿ����Uid
			if (captureValue[i].contains("Uid")) {
				String tempStr = CommonUtil.formatBlanksToBlank(captureValue[i]);
				String[] Uidbatterys = tempStr.split(" ");
				String uid = Uidbatterys[1].substring(0, Uidbatterys[1].length() - 1);
				float value = Float.parseFloat(Uidbatterys[2]);
				batteryData.setUid(uid);
				batteryData.setBatteryValue(String.valueOf(value));
				String appPackageName = get_package_name_by_uid(uid);
				if (nowTestPackageName != null && nowTestPackageName.equals(appPackageName)) {
					if (detailInfo.length() > 1024) {
						detailInfo = detailInfo.substring(0, 1024);
					}
					batteryData.setDetailInfo(detailInfo);
				}
				batteryData.setAppPackageName(appPackageName);
				batteryDatas.add(batteryData);
			} else {
				String tempStr = CommonUtil.formatBlanksToBlank(captureValue[i]);
				String[] UidBatterys = tempStr.split(":");
				String uid = UidBatterys[0].trim();
				float value = Float.parseFloat(UidBatterys[1].trim());
				batteryData.setUid(uid);
				batteryData.setBatteryValue(String.valueOf(value));
				// ����һ��Ĳ�����uid��������uid���������
				batteryData.setAppPackageName(uid);
				batteryDatas.add(batteryData);
			}
		}

		return batteryDatas;
	}
	
	
	public static List<BatteryData> getpowerdata(String pac) throws IOException {
		if (CommonUtil.strIsNull(pac)) {
			return null;
		}
		
		CommandResult data = get_battery_data(pac);
		writetofile(data.successMsg);
		System.out.println(data.successMsg);
		List<BatteryData> batteryDatas = handlePowerData(data.successMsg);
		return batteryDatas;
	}

	public static void main(String[] args) throws IOException {
		// CommandResult data = get_battery_data("com.xdja.safekeyservice");
		// writetofile(data.successMsg);
		String oldName = System.getProperty("user.dir");
		String filedemopath = oldName + "/powerresult/batteryData.txt";
		FileInputStream fileInputStream = new FileInputStream(filedemopath);
		byte[] buffer = new byte[1024];
		StringBuffer sbBuffer = new StringBuffer();
		int length = 0;
		while((length = fileInputStream.read(buffer, 0, buffer.length))!= -1){
			String bu = new String(buffer, 0, length);
			sbBuffer.append(bu);
		}
		
//		System.out.println(handlePowerData(sbBuffer.toString()));
		nowTestPackageName = "com.xdja.HDSafeEMailClient";
		BaseTestInfo baseTestInfo = CollectDataImpl.getBaseTestInfo(nowTestPackageName);
		String testVersion = "";
		if (baseTestInfo != null) {
			testVersion = baseTestInfo.versionName;
		}
		List<BatteryHandleResult> handleResults = HandleDataManager.getInstance().handleBatteryData(handlePowerData(sbBuffer.toString()), nowTestPackageName, testVersion);
		PerformanceDB.getInstance().insertBatteryData(handleResults);
	}
}