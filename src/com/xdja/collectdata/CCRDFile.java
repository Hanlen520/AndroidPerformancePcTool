package com.xdja.collectdata;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xdja.util.CommonUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.xdja.collectdata.CollectDataUtil;

//�������ļ���Ŀ¼
public class CCRDFile {
	// ��֤�ַ����Ƿ�Ϊ��ȷ·������������ʽ
	private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";
	// ͨ�� sPath.matches(matches) �����ķ���ֵ�ж��Ƿ���ȷ
	// sPath Ϊ·���ַ���
	boolean flag = false;
	

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
	public static String createTempFile(String prefix, String suffix,
			String dirName) {
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
	//get_package_name_by_uid��ȡ��ǰӦ�ð���//
	public static String get_package_name_by_uid(String uid){
		String packagename ="";
		String cmd = "adb shell ps | findStr "+ uid;
		CommandResult data = CollectDataUtil.getInstance().execCmdCommand(cmd, false, true);
		String result = null ;
		if(data.successMsg!=null){
			result = data.successMsg;
//		System.out.println(result);	
		String pattern = "com.*";
		String resu = null;
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(result);
		if (m.find()) {
			resu = m.group(0);
		} else {
//			System.out.println("NO MATCH do");
		}
		result = packagename;
		}
		return result;
		
	}
	
	
	//@@get_battery_data����ȡ��������@@//
	public static CommandResult get_battery_data(String pkg_name){
        String cmd ;
        if (pkg_name ==null || pkg_name == ""){
              cmd = "shell dumpsys batterystats";}
            else{
             cmd ="shell dumpsys batterystats "+ pkg_name;}
        CommandResult data = CollectDataUtil.getInstance().execShellCommand(cmd);
        if( (data != null) ){
            return data;}
        else{
            return null;}
     }
	
	//@@writetofile��д��cmd������Ľ����demo�ļ���@@//
	public static void writetofile(String a) throws IOException{
		String oldName = System.getProperty("user.dir");
		String filepath =  oldName+"/powerresult/";
		File file = new File(filepath);
		if (!file.exists()) {
			   file.mkdir();//�����ļ���
			  }
//		CCRDFile.createDir(filepath);// ���÷�������Ŀ¼
		String filedemopath =  oldName+"/powerresult/demo.txt";
		File filedemo = new File(filedemopath);
		if (!filedemo.exists()){
		FileWriter fw=new FileWriter(filepath+"demo.txt",true);//�����ļ�
		fw.write(a);
        fw.close();
        }else{
        	new CCRDFile().deleteFile(filedemopath); 
        	FileWriter fw=new FileWriter(filepath+"demo.txt",true);//�����ļ�
    		fw.write(a);
            fw.close();
        }
	}
	
	//insert��̬��Ӷ�ά����Ԫ�صķ���//
	 private static String[][] insert(String[][] arr, String str,String str2,String str3)
     {
	  int size ;
	  if (arr != null){
	    size = arr.length;}
	  else{size =0;
	  }
		String[][] tmp = new String[size + 1][3];
		System.arraycopy(arr, 0, tmp, 0, size);
		tmp[size][0] = str;
		tmp[size][1] = str2;
		tmp[size][2] = str3;
		String[][] ary = new String[tmp.length-1][3];
		int index = 0;
		if (tmp[0].length ==0){
		System.arraycopy(tmp, 0, ary, 0, index);
		System.arraycopy(tmp, index+1, ary, index, ary.length-index);
		}else{
		ary = tmp ;
		}
		return ary;
	}
	
	 
	//������ʽ������//
	 private static String handlere(String powerdata) {
			String line = powerdata;
			String resu = null;
			String pattern = "\\(mAh\\):([\\s\\S]*)";
			// ���� Pattern ����
			Pattern r = Pattern.compile(pattern);
			// ���ڴ��� matcher ����
			Matcher m = r.matcher(line);
//			System.out.println(m.find());
			if (m.find()) {
				resu = (String) m.group(1);
			} else {
				System.out.println("NO MATCH");
			}
			return resu;

		}
	 
	//handlepowerdata�����������//
	@SuppressWarnings("null")
	public static String[][] handlepowerdata(String message){
//		System.out.println(message);
//		String resu = message.split("Estimated power use (mAh):")[1].trim();
		String[] powerdata;
		String[][] alldata={{}} ; 
		String resu = handlere(message);
//		System.out.println("resu:"+resu);
		String resus = resu.trim();
		if (CommonUtil.strIsNull(resus)) {
			return alldata;
		}
//		System.out.println("resus:"+resus);
		powerdata = resus.split("\n\n");
//		System.out.println(Arrays.toString(powerdata));//
		String pkg_name = "", compute_drain, actual_drain;
		// ��������������
		powerdata = powerdata[0].split("\n");
		for (int i=0 ;i< powerdata.length;i++){
			if (i==0){
				String battery_total_data;
				String[] battery_total_datas;
				battery_total_data = powerdata[i];
				battery_total_datas = battery_total_data.split(",");
				compute_drain = battery_total_datas[1];
			    actual_drain = battery_total_datas[2];
			}
			//������uid���ĵ��������
			String battery_str = powerdata[i];
	        if (battery_str == null || battery_str == ""){
	            break;
	        }
	        String[] battery_strs = battery_str.split(": ");
//	        System.out.println(Arrays.toString(battery_strs));//
	        if (battery_strs.length ==0){
	        	continue;
	        }
	        String uid;
	        uid= battery_strs[0].trim();
//	        System.out.println(uid);
	        //����uid�����ҳ�packagename
	        if (uid != null && uid.indexOf("Uid")!=-1){
	        	String uid_uid = uid.split(" ")[1];
	        	if (((String) uid_uid).indexOf("u0")!=-1){
	        		uid_uid = "u0_" + ((String) uid_uid).substring(2);
	        		pkg_name = CCRDFile.get_package_name_by_uid(uid_uid);
	        	}
	        	
	        }
	        String battery_str_data;
	        if (battery_strs.length >1){
	        battery_str_data = battery_strs[1];	
	        
	        }else{
	        	continue;
	        }
	        
	        
	        if(uid != null &&  isDouble(battery_str_data)){
	        	String[][] tmp= insert(alldata, uid,pkg_name, battery_str_data);
	        	alldata = tmp;
	        }
	        else{
//	        	uid = "";
//	        	String[][] tmp= insert(alldata, uid,pkg_name, battery_str_data);
//	        	alldata = tmp;
	        }
	        pkg_name = "";
	        
		}
		
		return alldata;
		
	}
	
	//�ж��Ƿ�ΪisDouble���͵�����//
	private static boolean isDouble(String str)
	{
	   try
	   {
	      Double.parseDouble(str);
	      return true;
	   }
	   catch(NumberFormatException ex){}
	   return false;
	}
	
	public static String[][] getpowerdata(String pac) throws IOException{
		CommandResult data = get_battery_data(pac);
		writetofile(data.successMsg);
		String[][] getpowerdata = handlepowerdata(data.successMsg);
		return getpowerdata;
	}
	
	public static void main(String[] args) throws IOException  {
        CommandResult data = get_battery_data("com.xdja.safekeyservice");
		writetofile(data.successMsg);
		System.out.println(Arrays.deepToString(handlepowerdata(data.successMsg)));
	}
}