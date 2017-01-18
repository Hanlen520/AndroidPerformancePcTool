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
import com.xdja.collectdata.entity.FpsData;
import com.xdja.collectdata.handleData.HandleDataManager;
import com.xdja.collectdata.handleData.entity.FpsHandleResult;
import com.xdja.collectdata.handleData.entity.HandleDataResultBase;
import com.xdja.constant.GlobalConfig;
import com.xdja.database.PerformanceDB;
import com.xdja.util.CommonUtil;
import com.xdja.util.SwingUiUtil;

public class FpsView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private boolean stopFlag = false;
	private Thread fpsThread;
	private List<FpsData> fpsdataList = null;
	private List<FpsHandleResult> fpsHandleList = new ArrayList<>(12);
	private List<FpsHandleResult> errorList = new ArrayList<>(12);
	private CategoryPlot mPlot;
	private DefaultCategoryDataset mDataset  = null;
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
		JButton startBtn = SwingUiUtil.getInstance().createBtnWithColor("��ʼ", Color.green);
		JButton pauseBtn = SwingUiUtil.getInstance().createBtnWithColor("����", Color.RED);
		pauseBtn.setEnabled(false);
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startBtn.setEnabled(false);
				pauseBtn.setEnabled(true);
				start(GlobalConfig.PackageName);
			}
		});
		
		pauseBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startBtn.setEnabled(true);
				pauseBtn.setEnabled(false);
				stop();
				
				//��������
				saveDataToDb();
			}
		});
		
		startBtn.setLocation(0, 30);
		pauseBtn.setLocation(30, 30);
		
		chartPanel.add(startBtn);
		chartPanel.add(pauseBtn);
		addJpanel(chartPanel);
	}
	
	/**
	 * ��ʼ����
	 * 
	 * @param packageName
	 */
	public void start(String packageName) {
		fpsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopFlag = false;
				while (true) {
					if (stopFlag) {
						break;
					}
					fpsdataList = CollectDataImpl.getFpsData(packageName);
					if (fpsdataList != null ) {
						for(FpsData fpsdata : fpsdataList){
//							mDataset = new DefaultCategoryDataset();
							mDataset.addValue(fpsdata.fps, "֡��", fpsdata.activityName);
							mDataset.addValue(fpsdata.dropcount, "��֡��", fpsdata.activityName);
							mDataset.addValue(fpsdata.framecount, "��֡��", fpsdata.activityName);
							if (mPlot != null) {
								mPlot.setDataset(mDataset);
							}
						}
					}
					
					handleFpsData(fpsdataList);
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
	
	private void handleFpsData(List<FpsData> fpsDatas){
		fpsHandleList = HandleDataManager.getInstance().handleFpsData(fpsDatas);
		
		if (fpsHandleList != null && fpsHandleList.size() > 0) {
			// չʾ������Ϣ
			for(FpsHandleResult fpsData : fpsHandleList){
				if (!fpsData.result) {
					if (errorList.contains(fpsData)) {
						continue;
					}
					
					errorList.add(fpsData);
					mShowMessageView.append(formatErrorInfo(fpsData, String.valueOf(fpsData.testValue), "ҳ����ֿ���"));
				}
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
    	sbBuilder.append("��ǰ��֡��= ").append(fpsHandleResult.dropcount).append("\n");
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
	
	private void saveDataToDb(){
		// ����fps����
		if (fpsHandleList == null || fpsHandleList.size() < 1) {
			return;
		}
		
		PerformanceDB.getInstance().insertFpsData(fpsHandleList);
		
		// �ر����ݿ�
		PerformanceDB.getInstance().closeDB();
	}
}


