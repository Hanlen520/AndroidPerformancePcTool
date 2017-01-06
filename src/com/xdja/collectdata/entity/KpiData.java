package com.xdja.collectdata.entity;

/**
 *  ҳ�����ʱ��ķ�װ��
 * @author zlw
 *
 */
public class KpiData {
	public String currentPage;
	public int loadTime;
	public KpiData(String currentPage, int loadTime) {
		super();
		this.currentPage = currentPage;
		this.loadTime = loadTime;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "currentPage = " + this.currentPage + ", loadTime = " + this.loadTime;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.currentPage.equals(((KpiData)obj).currentPage);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.currentPage.hashCode();
	}

	public int getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(int loadTime) {
		this.loadTime = loadTime;
	}
}
