package com.xdja.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

public abstract class BaseChartView extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Font FONT = new Font("����", Font.PLAIN, 12);
	protected Timer mTaskTimer;
	protected ActionListener mActionListener;

	public BaseChartView() {
		super(new BorderLayout());
		// TODO Auto-generated constructor stub
		initCommonChart();
	}
	
	/**
	 * ��ʼ��chart�����Ĳ���
	 */
	protected void initCommonChart(){
		
		// ��������������ʽ �������
		StandardChartTheme chartTheme = new StandardChartTheme("CN");
		// ���ñ�������
		chartTheme.setExtraLargeFont(FONT);
		// ����ͼ��������
		chartTheme.setRegularFont(FONT);
		// ������������
		chartTheme.setLargeFont(new Font("����", Font.PLAIN, 15));
		//Paint �������Ϊ������ɫ������������ɫ
		chartTheme.setTitlePaint(new Color(51, 51, 51));
		// ���ñ�ע����ɫ
		chartTheme.setLegendBackgroundPaint(Color.WHITE);
		//����������ɫ
		chartTheme.setLegendItemPaint(Color.BLACK);
		//ͼ����ɫ
		chartTheme.setChartBackgroundPaint(Color.WHITE);
		// �������򱳾�ɫ
		chartTheme.setPlotBackgroundPaint(Color.gray);
		// ����������߿�
		chartTheme.setPlotOutlinePaint(Color.WHITE);
		
		ChartFactory.setChartTheme(chartTheme);
	}
}
