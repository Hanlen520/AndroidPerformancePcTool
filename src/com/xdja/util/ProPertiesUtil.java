package com.xdja.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * ���ڶ�ȡ�����ļ��Ĺ���
 * 
 * @author zlw
 *
 */
public class ProPertiesUtil {

	private static ProPertiesUtil mInstance;
	private final static String NAME = "tools.properties";
	private Properties mProperties;

	private ProPertiesUtil() {
		mProperties = new Properties();
	}

	public static ProPertiesUtil getInstance() {
		if (mInstance == null) {
			synchronized (ProPertiesUtil.class) {
				if (mInstance == null) {
					mInstance = new ProPertiesUtil();
				}
			}
		}

		return mInstance;
	}

	public void writeProperties(String key, String value) {
		FileOutputStream oFile;
		try {
			oFile = new FileOutputStream(NAME);
			mProperties.setProperty(key, value);
			mProperties.store(oFile, "Update '" + key + "' value  " + value);
			oFile.flush();
			oFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // true��ʾ׷�Ӵ�
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡkey��Ӧ��ֵ
	 * 
	 * @param key
	 * @return
	 */
	public String getProperties(String key) {
		if (CommonUtil.strIsNull(key)) {
			return "";
		}
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(NAME));
			mProperties.load(in); /// ���������б�
			Iterator<String> it = mProperties.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String ikey = it.next();
				if (ikey != null && ikey.equals(key)) {
					return mProperties.getProperty(ikey);
				}
			}
			in.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";
	}

	/**
	 * ���key��Ӧ��value
	 * 
	 * @param key
	 */
	public void removeValue(String key) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(NAME));
			mProperties.load(in); /// ���������б�
			Iterator<String> it = mProperties.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String ikey = it.next();
				if (ikey != null && ikey.equals(key)) {
					mProperties.remove(ikey);
				}
			}
			in.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
