package com.xdja.database;

import java.util.List;


/**
 *  ��װ���ݿ��ѯ�ӿڣ����ڷ�����ز�ѯ���
 * @author 
 *
 */
public class PerformanceDBSearch {
	public List<Float> cpuData;
	public List<String> pageData;
	public List<Integer> passData;
	public PerformanceDBSearch(List<Float> cpuData,List<String> pageData,List<Integer> passData){
		super();
		this.cpuData = cpuData;
		this.pageData = pageData;
		this.passData = passData;
	}
	
}