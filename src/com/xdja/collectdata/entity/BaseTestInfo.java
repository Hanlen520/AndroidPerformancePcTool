package com.xdja.collectdata.entity;

/**
 *  �����Ĳ�����Ϣ
 * @author zlw
 *
 */
public class BaseTestInfo {
	// ��������
	public String packageName;
	
	// ���԰��İ汾��
	public String versionName;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public BaseTestInfo(String packageName, String versionName) {
		super();
		this.packageName = packageName;
		this.versionName = versionName;
	}
	
	
}
