package com.xdja.collectdata.entity;

/**
 *  ��װ���ڴ������
 * @author zlw
 *
 */
public class MemoryData {
	public float memAlloc;
	public float memFree;
	
	public MemoryData() {
		// TODO Auto-generated constructor stub
	}

	public MemoryData(float memAlloc, float memFree) {
		super();
		this.memAlloc = memAlloc;
		this.memFree = memFree;
	}
}
