package com.xdja.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/***
 * �����Ĺ���
 * 
 * @author zlw
 *
 */
public class CommonUtil {

	/**
	 * ������λС��
	 * 
	 * @param number
	 * @return
	 */
	public static float getTwoDots(float number) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return Float.parseFloat(df.format(number));
	}

	/**
	 *  ���������������λС����ת��%��ʽ
	 * @param a
	 * @param b
	 * @return
	 */
	public static float twoIntDivision(int a, int b){
		DecimalFormat df = new DecimalFormat("0.00");
		return Float.valueOf(df.format((float)a/b * 100));
	}


	/**
	 * ���ַ����еĶ���ո��滻��һ���ո�
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
	 * �ж��ַ����Ƿ���null�����ǡ���
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
	 *  ��ָ��������д�뵽����
	 * @param data
	 * @param filePath
	 *
	 */
	public static void writeDataToLocal(byte[] data, String filePath){
		if (data == null) {
			return;
		}
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
	 * ��java��ִ��cmd����
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

	public static void main(String[] args){
		System.out.println(twoIntDivision(8, 2230));

		// TODO �Զ����ɵķ������
		int a=9;
		int b=7;
		DecimalFormat df=new DecimalFormat("0.00");

		System.out.println(df.format((float)a/b));
		System.out.println(df.format(a/(float)b));
		System.out.println(df.format((float)a/(float)b));
		System.out.println(df.format((float)(a/b)));
	}
}
