package com.xdja.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/***
 * 公共的工具
 * 
 * @author zlw
 *
 */
public class CommonUtil {

	/**
	 * 保留两位小数
	 * 
	 * @param number
	 * @return
	 */
	public static float getTwoDots(float number) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return Float.parseFloat(df.format(number));
	}

	/**
	 * 将字符串中的多个空格，替换成一个空格
	 * 
	 * @param contents
	 * @return
	 */
	public static String formatBlanksToBlank(String contents) {
		Pattern p = Pattern.compile("\\s+");
		Matcher m = p.matcher(contents);
		contents = m.replaceAll(" ");
		return contents;
	}

	/**
	 * 判断字符串是否是null或者是“”
	 * 
	 * @param str
	 * @return
	 */
	public static boolean strIsNull(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}

		return false;
	}

	
	/**
	 *  将指定的数据写入到本地
	 * @param data
	 * @param path
	 * @param fileName
	 */
	public static void writeDataToLocal(byte[] data, String path, String fileName){
		if (data == null) {
			return;
		}
		
		if (strIsNull(path)) {
			path = "default";
		}
		
		if (strIsNull(fileName)) {
			path = "unkonw";
		}
		
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		String filePath = path + File.separator + fileName;
		FileOutputStream foStream = null;
		try {
			foStream = new FileOutputStream(filePath);
			foStream.write(data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if (foStream != null) {
				try {
					foStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 在java中执行cmd命令
	 * @param cmd
	 * @param needResult
	 */
	public static void execCmd(String cmd, boolean needResult){
		Runtime run = Runtime.getRuntime();
        try {
            // run.exec("cmd /k shutdown -s -t 3600");
            Process process = run.exec(cmd);
            if (needResult) {
            	InputStream in = process.getInputStream();
                while (in.read() != -1) {
                    System.out.println(in.read());
                }
                in.close();
			}
            
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
