package com.xdja.collectdata;

/**
 *  ��װ���ڴ������
 * @author zlw
 *
 */
public class MemoryData {
	public long memAlloc;
	public long memFree;
	
	public MemoryData() {
		// TODO Auto-generated constructor stub
	}

	public MemoryData(long memAlloc, long memFree) {
		super();
		this.memAlloc = memAlloc;
		this.memFree = memFree;
	}
}
