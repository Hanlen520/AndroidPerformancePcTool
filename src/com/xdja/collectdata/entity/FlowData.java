package com.xdja.collectdata.entity;

/**
 *  ���ڷ�װ��������
 * @author zlw
 *
 */
public class FlowData {
	public float flowTotal;
	public float flowRecv;
	public float flowSend;
	public FlowData(float flowTotal, float flowRecv, float flowSend) {
		super();
		this.flowTotal = flowTotal;
		this.flowRecv = flowRecv;
		this.flowSend = flowSend;
	} 
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "flowTotal = " + this.flowTotal + ", flowRecv = " + this.flowRecv + ", flowSend = " + flowSend;
		
	}
}
