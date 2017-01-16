package com.xdja.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import com.xdja.collectdata.handleData.HandleDataResult;
import com.xdja.util.CommonUtil;

public abstract class BaseChartView extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Font FONT = new Font("����", Font.PLAIN, 12);
	protected Timer mTaskTimer;
	protected ActionListener mActionListener;
	protected List<HandleDataResult> mHandleDataList = new ArrayList<>();
	protected ShowMessageView mShowMessageView;
	private boolean isFirstShowError = true;  // �Ƿ��һ��չʾ������Ϣ
	
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
	
	/**
	 * ����JPanel�Ĳ���
	 * 
	 */
	protected void addJpanel(JPanel jpanel) {
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.add(jpanel);
		horizontalBox.add(Box.createHorizontalStrut(50));
		mShowMessageView = new ShowMessageView();
		horizontalBox.add(mShowMessageView);
		add(horizontalBox);
	}
	
    /**
     *  ��ȡ�����������
     * @return
     */
    public List<HandleDataResult> getHandleResult(){
    	return mHandleDataList;
    }
    
    
    /**
     *  ��ʽ��������Ϣ
     * @param result
     * @return
     */
    protected String formatErrorInfo(HandleDataResult result, String value){
    	StringBuilder sbBuilder = new StringBuilder("===================== \n");
    	sbBuilder.append("ActivityName = ").append(result.activityName).append("\n");
    	sbBuilder.append("��ǰ����ֵ= ").append(value).append("\n");
    	sbBuilder.append("Logfile= ").append(result.logPath).append("\n");
    	sbBuilder.append("����·��= ").append(result.screenshotsPath).append("\n");
    	sbBuilder.append("===================== \n\n\n\n");
    	return sbBuilder.toString();
    }
    
    protected void appendErrorInfo(String msg) {
		if (CommonUtil.strIsNull(msg)) {
			return;
		}
		
		if (mShowMessageView != null) {
			if (isFirstShowError) {
				mShowMessageView.setText("");
				isFirstShowError = false;
			}
			
			mShowMessageView.append(msg);
		}
	}
}
