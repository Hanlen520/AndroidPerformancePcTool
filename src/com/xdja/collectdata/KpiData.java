package com.xdja.collectdata;

/**
 *  ҳ�����ʱ��ķ�װ��
 * @author zlw
 *
 */
public class KpiData {
	public String currentPage;
	public float loadTime;
	public KpiData(String currentPage, float loadTime) {
		super();
		this.currentPage = currentPage;
		this.loadTime = loadTime;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "currentPage = " + currentPage + ", loadTime = " + this.loadTime;
	}
	
}
