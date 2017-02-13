package com.xdja.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSlider;

import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.util.ProPertiesUtil;
import com.xdja.util.SwingUiUtil;
import com.xdja.view.custom.MyJSlider;

/**
 *  ����չʾ������ֵ���õ����ҳ��
 * @author zlw
 *
 */
public class PerformanceSettingDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5696207127438511575L;
	private MyJSlider memorySlider, cpuSlider, kpiSlider, flowSlider, fpsSlider;
	private MyJSlider silentFlowSlider, silentCpuSlider;
	private JButton mSaveBtn;
	
	public PerformanceSettingDialog(Frame owner, String title) {
		super(owner, title);
		// TODO Auto-generated constructor stub
		
		// ��ʼ������Ԫ��
		memorySlider = new MyJSlider(JSlider.HORIZONTAL, "�ڴ棨MB��");
		memorySlider.setSliderMinValue(0);
		memorySlider.setSliderMaxValue(100);
		//Ĭ��ֵ
		memorySlider.setSliderCurValue(GlobalConfig.MEMORY_MAX);
		
		// ҳ�����ʱ��
		kpiSlider = new MyJSlider(JSlider.HORIZONTAL, "ҳ�����ʱ�䣨s��");
		kpiSlider.setSliderMinValue(0);
		kpiSlider.setSliderMaxValue(10);
		kpiSlider.setMajorTickSpacing(5);
		kpiSlider.setMinTickSpacing(1);
		// Ĭ��ֵ
		kpiSlider.setSliderCurValue(GlobalConfig.MAX_KPI_DATA / 1000);
		
		// cpu��ֵ
		cpuSlider = new MyJSlider(JSlider.HORIZONTAL, "cpuռ���ʣ�%��");
		cpuSlider.setSliderMinValue(0);
		cpuSlider.setSliderMaxValue(100);
		cpuSlider.setSliderCurValue(GlobalConfig.CPU_MAX);
		
		// flow
		flowSlider = new MyJSlider(JSlider.HORIZONTAL, "�����������ֵ��MB��");
		flowSlider.setSliderMinValue(0);
		flowSlider.setSliderMaxValue(10);
		flowSlider.setMajorTickSpacing(5);
		flowSlider.setMinTickSpacing(1);
		flowSlider.setSliderCurValue(GlobalConfig.MAX_FLOW_DATA / 1024);
		
		// fps
		fpsSlider = new MyJSlider(JSlider.HORIZONTAL, "ҳ�����̶ȣ�fps��");
		fpsSlider.setSliderMinValue(0);
		fpsSlider.setSliderMaxValue(60);
		fpsSlider.setSliderCurValue(GlobalConfig.MIN_FPS_DATA);
		
		//silentFlowSlider
		silentFlowSlider = new MyJSlider(JSlider.HORIZONTAL, "��Ĭ������KB��");
		silentFlowSlider.setSliderMinValue(0);
		silentFlowSlider.setSliderMaxValue(100);
		silentFlowSlider.setSliderCurValue(GlobalConfig.SILENT_FLOW_DATA / 1024);
		
		// silentCpuSlider
		silentCpuSlider = new MyJSlider(JSlider.HORIZONTAL, "��ĬCPU��%��");
		silentCpuSlider.setSliderMinValue(0);
		silentCpuSlider.setSliderMaxValue(10);
		silentCpuSlider.setMajorTickSpacing(5);
		silentCpuSlider.setMinTickSpacing(1);
		silentCpuSlider.setSliderCurValue(GlobalConfig.SILENT_CPU_MAX);
		
		// ���水ť
		mSaveBtn = new JButton("��������");
		mSaveBtn.setPreferredSize(new Dimension(300, 50));
		mSaveBtn.addActionListener(this);
		
		add(memorySlider);
		add(kpiSlider);
		add(cpuSlider);
		add(flowSlider);
		add(fpsSlider);
		add(silentCpuSlider);
		add(silentFlowSlider);
		add(mSaveBtn);
		setLayout(new FlowLayout(FlowLayout.CENTER));
		setBounds(600, 260, 700, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String command = e.getActionCommand();
		switch (command) {
		case "��������":
			saveDataToProperties();
			break;

		default:
			break;
		}
	}
	
	/**
	 * �����ݱ��浽Properties��
	 * 		add(memorySlider);
		add(kpiSlider);
		add(cpuSlider);
		add(flowSlider);
		add(fpsSlider);
		add(silentCpuSlider);
		add(silentFlowSlider);
	 */
	private void saveDataToProperties(){
		if (memorySlider != null) {
			int value = memorySlider.getSliderValue();
			ProPertiesUtil.getInstance().writeProperties(Constants.MEMORY_SETTING, String.valueOf(value));
		}
		
		if (kpiSlider != null) {
			int value = kpiSlider.getSliderValue();
			ProPertiesUtil.getInstance().writeProperties(Constants.KPI_SETTING, String.valueOf(value));
		}
		
		if (cpuSlider != null) {
			int value = cpuSlider.getSliderValue();
			ProPertiesUtil.getInstance().writeProperties(Constants.CPU_SETTING, String.valueOf(value));
		}
		
		if (flowSlider != null) {
			int value = flowSlider.getSliderValue();
			ProPertiesUtil.getInstance().writeProperties(Constants.FLOW_SETTING, String.valueOf(value));
		}
		if (fpsSlider != null) {
			int value = fpsSlider.getSliderValue();
			ProPertiesUtil.getInstance().writeProperties(Constants.FPS_SETTING, String.valueOf(value));
		}
		if (silentCpuSlider != null) {
			int value = silentCpuSlider.getSliderValue();
			ProPertiesUtil.getInstance().writeProperties(Constants.SILENT_CPU_SETTING, String.valueOf(value));
		}
		if (silentFlowSlider != null) {
			int value = silentFlowSlider.getSliderValue();
			ProPertiesUtil.getInstance().writeProperties(Constants.SILENT_FLOW_SETTING, String.valueOf(value));
		}
		
		SwingUiUtil.getInstance().showTipsDialog(this, "��ʾ", "�����Ѿ�����ɹ�", "ȷ��", null);
	}
	
//	public static void main(String[] args) {
//		PerformanceSettingDialog performanceSettingDialog  = new PerformanceSettingDialog(this, "��������ָ����ֵ");
//		performanceSettingDialog.setVisible(true);
//	}
}
