package com.xdja.view.chart;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.SaveEnvironmentManager;
import com.xdja.collectdata.entity.BaseTestInfo;
import com.xdja.collectdata.entity.FpsData;
import com.xdja.collectdata.handleData.HandleDataManager;
import com.xdja.collectdata.handleData.entity.FpsHandleResult;
import com.xdja.collectdata.handleData.entity.HandleDataResultBase;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.database.PerformanceDB;
import com.xdja.database.SaveLocalManager;
import com.xdja.exception.SettingException;
import com.xdja.util.CommonUtil;
import com.xdja.util.ProPertiesUtil;
import com.xdja.util.SwingUiUtil;
import com.xdja.view.main.LaunchView;

public class FpsView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private JButton startBtn, pauseBtn;
	private Thread fpsThread;
	private List<FpsHandleResult> fpsHandleList = new ArrayList<>(12);
	private List<FpsHandleResult> tempHandleList = new ArrayList<>(12);
	private List<FpsHandleResult> errorList = new ArrayList<>(12);
	private CategoryPlot mPlot;
	private DefaultCategoryDataset mDataset  = null;
	private FpsData mFpsData;
	private FpsHandleResult mFpsHandleResult;
	private final static String  NOMESSGE = "����֡�ʣ����ڿ�����ѡ�����ҵ���GPU����ģʽ���������򿪡���adb shell dumpsys gfxinfo�С�ѡ��";
	
	
	public FpsView(String chartContent, String title, String yaxisName) {
		super();
//		setLayout(new FlowLayout(FlowLayout.LEADING));
		mDataset = new DefaultCategoryDataset();
		JFreeChart mBarchart = ChartFactory.createBarChart(title, chartContent, yaxisName, mDataset,
				PlotOrientation.VERTICAL, // ͼ����
				true, // �Ƿ�����ͼ��
				true, // �Ƿ�������ʾ����
				false // �Ƿ�����url����
		);
		// ͼ���������
		TextTitle mTextTitle = mBarchart.getTitle();
		mTextTitle.setFont(new Font("����", Font.BOLD, 20));
		// ͼ��ͼ������
		LegendTitle mLegend = mBarchart.getLegend();
		if (mLegend != null) {
			mLegend.setItemFont(new Font("����", Font.CENTER_BASELINE, 15));
		}

		// ������״ͼ��
		CategoryPlot mPlot = mBarchart.getCategoryPlot();
		mPlot.setNoDataMessage(NOMESSGE);
		mPlot.setNoDataMessageFont(new Font("����", Font.BOLD, 17));
		
		// x��
		CategoryAxis mDomainAxis = mPlot.getDomainAxis();
		mDomainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		mDomainAxis.setLabelFont(new Font("����", Font.PLAIN, 15));
		// ����x����������
		mDomainAxis.setTickLabelFont(new Font("����", Font.PLAIN, 15));

		// y��
		ValueAxis mValueAxis = mPlot.getRangeAxis();
		mValueAxis.setLabelFont(new Font("����", Font.PLAIN, 15));
		mValueAxis.setTickLabelFont(new Font("����", Font.PLAIN, 15));

		// ������ʾ��ֵ
		BarRenderer mRenderer = new BarRenderer();
		mRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		mRenderer.setBaseItemLabelsVisible(true);
		
		mPlot.setRenderer(mRenderer);
		
		//��freechart��ӵ������
		ChartPanel chartPanel = new ChartPanel(mBarchart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
//		addSigleSwitch();
		chartPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		//��ӵ�����ͼ��
		startBtn = SwingUiUtil.getInstance().createBtnWithColor("��ʼ", Color.green);
		pauseBtn = SwingUiUtil.getInstance().createBtnWithColor("����", Color.RED);
		pauseBtn.setEnabled(false);
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startBtn.setEnabled(false);
				pauseBtn.setEnabled(true);
				LaunchView.setComboxEnable(false);
				LaunchView.setBtnEnable(false);
				start();
			}
		});
		
		pauseBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startBtn.setEnabled(true);
				pauseBtn.setEnabled(false);
				LaunchView.setComboxEnable(true);
				LaunchView.setBtnEnable(true);
				stop();
				//��������
				try {
					saveData();
				} catch (SettingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		startBtn.setLocation(0, 30);
		pauseBtn.setLocation(30, 30);
		
		chartPanel.add(startBtn);
		chartPanel.add(pauseBtn);
		addJpanel(chartPanel);
	}
	
	private void saveData() throws SettingException{
		String useDbStr = ProPertiesUtil.getInstance().getProperties(Constants.DBSAVE_CHOOSE);
		if ("true".equals(useDbStr)) {
			saveDataToDb();
		}else {
			BaseTestInfo baseTestInfo = CollectDataImpl.getBaseTestInfo(nowTestPackage);
			SaveLocalManager.getInstance().setTestPackageAndVersion(nowTestPackage, baseTestInfo.versionName);
			SaveLocalManager.getInstance().saveFpsDataToLocal(fpsHandleList);
		}
	}
	
	/**
	 * ��ʼ����
	 * 
	 * @param packageName
	 */
	public void start() {
		nowTestPackage = ProPertiesUtil.getInstance().getProperties(Constants.CHOOSE_PACKAGE);
		fpsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopFlag = false;
				isRunning = true;
				while (true) {
					if (stopFlag) {
						isRunning = false;
						break;
					}
					mFpsData = CollectDataImpl.getFpsData(nowTestPackage);
					handleFpsData(mFpsData);
					handleFpsHandleList();
					if (mFpsData != null ) {
						for(FpsHandleResult fpsdata : fpsHandleList){
//							mDataset = new DefaultCategoryDataset();
							mDataset.addValue(Integer.parseInt(fpsdata.testValue), "֡��", fpsdata.activityName);
							mDataset.addValue(fpsdata.dropCount, "��֡��", fpsdata.activityName);
							mDataset.addValue(fpsdata.frameCount, "��֡��", fpsdata.activityName);
							if (mPlot != null) {
								mPlot.setDataset(mDataset);
							}
						}
					}
					
					try {
						Thread.sleep(GlobalConfig.collectMIDDLEInterval);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		fpsThread.start();
	}
	
	public void stop() {
		stopFlag = true;
	}
	
	/**
	 * �������б����ظ���Ԫ�أ�ȡƽ��ֵ
	 * ������Ҫע�⣬ֱ����List��removeԪ���ǲ���ȫ�ģ���ΪremoveԪ�ػ�ı�ԭ�еĽṹ��
	 * ���ﻹ�������ַ�ʽ��Ч�ʲ���
	 */
	private void handleFpsHandleList(){
		if (fpsHandleList == null || fpsHandleList.size() < 1) {
			return ;
		}
		FpsHandleResult handleResult = null, handleResult2 = null;
		int count = 1;
		int fps = 0;
		for(int i = 0; i < fpsHandleList.size(); i++){
			handleResult = fpsHandleList.get(i);
			if (tempHandleList.contains(handleResult)) {
				continue;
			}
			if (handleResult == null) {
				continue;
			}
			fps += Integer.parseInt(handleResult.testValue);
			
			for(int j = i+1; j < fpsHandleList.size(); j++){
				handleResult2 = fpsHandleList.get(j);
				if (handleResult2 == null) {
					continue;
				}
				
				if (handleResult.equals(handleResult2)) {
					count +=1;
					fps += Integer.parseInt(handleResult2.testValue);
					fpsHandleList.remove(handleResult2);
				}
				
			}
			fps = fps / count;
			handleResult.setTestValue(String.valueOf(fps));
			if (!tempHandleList.contains(handleResult)) {
				tempHandleList.add(handleResult);
			}
			fps = 0;
			count = 1;
		}
		
		//������ʱ����
		fpsHandleList.clear();
		fpsHandleList.addAll(tempHandleList);
		tempHandleList.clear();
		
	}
	/**
	 *  �����õ�FpsData
	 * @param fpsData
	 */
	private void handleFpsData(FpsData fpsData){
		if (fpsData == null) {
			return;
		}
		mFpsHandleResult = HandleDataManager.getInstance().handleFpsData(fpsData);
		// �����ݽ����ж�
		if (mFpsHandleResult != null) {
			// ���������ӵ��б��У�Ϊ����PC��չʾ
			fpsHandleList.add(mFpsHandleResult);
			
			// �ж������Ƿ�ͨ���ж�
			if (!mFpsHandleResult.result) {  // ���������⣬֡��С��ĳ��ֵ
				if (errorList.contains(mFpsHandleResult)) {
					return;
				}
				
				//��¼������Ϣ��չʾ
				errorList.add(mFpsHandleResult);
				mShowMessageView.append(formatErrorInfo(mFpsHandleResult, String.valueOf(mFpsHandleResult.testValue), "ҳ����ֿ���"));
				
				// ������Ի���
				// ����log
				String logPath = SaveEnvironmentManager.getInstance().saveCurrentLog(GlobalConfig.DeviceName,
						nowTestPackage, Constants.TYPE_FPS);
				// ����method trace
				mFpsHandleResult.setLogPath(logPath);
				mFpsHandleResult.setMethodTracePath("");
				mFpsHandleResult.setMemoryHprofPath("");
			}
		}
	}
	
	@Override
	protected String formatErrorInfo(HandleDataResultBase result, String value, String errorInfo) {
		FpsHandleResult fpsHandleResult = null;
		if (result instanceof FpsHandleResult) {
			fpsHandleResult = (FpsHandleResult) result;
		}
		// TODO Auto-generated method stub
		StringBuilder sbBuilder = new StringBuilder("===================== \n");
    	if (!CommonUtil.strIsNull(errorInfo)) {
			sbBuilder.append(errorInfo).append("\n");
		}
    	sbBuilder.append("ActivityName = ").append(fpsHandleResult.activityName).append("\n");
    	sbBuilder.append("��ǰ֡��ֵ= ").append(value).append("\n");
    	sbBuilder.append("��ǰ��֡��= ").append(fpsHandleResult.dropCount).append("\n");
    	sbBuilder.append("Logfile= ").append(result.logPath).append("\n");
    	sbBuilder.append("===================== \n\n\n\n");
    	return sbBuilder.toString();
	}
	/**
	 * ����fps����֮��Ľ��
	 * @return
	 */
	public List<FpsHandleResult> getFpsHandleResult(){
		return fpsHandleList;
	}
	
	public void saveDataToDb(){
		// ����fps����
		if (fpsHandleList == null || fpsHandleList.size() < 1) {
			return;
		}
		
		PerformanceDB.getInstance().insertFpsData(fpsHandleList);
		
		// �ر����ݿ�
		PerformanceDB.getInstance().closeDB();
	}
	
	public void destoryData(){
		if (fpsHandleList != null) {
			fpsHandleList.clear();
			fpsHandleList = null;
		}
		
		if (errorList != null) {
			errorList.clear();
			errorList = null;
		}
	}
	
	/**
	 *  ���õ�ǰ����İ�ť�Ƿ��ǿ��Ե���ġ�
	 * @param enable
	 */
	public void setBtnEnable(boolean enable){
		if (startBtn == null || pauseBtn == null) {
			return;
		}
		if (enable) {
			startBtn.setEnabled(true);
			pauseBtn.setEnabled(false);
		}else {
			startBtn.setEnabled(false);
			pauseBtn.setEnabled(false);
		}
		
	}
}

