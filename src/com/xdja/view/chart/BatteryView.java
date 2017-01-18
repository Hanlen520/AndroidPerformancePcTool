
package com.xdja.view.chart;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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

import com.xdja.collectdata.CCRDFile;
import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.entity.BatteryData;
import com.xdja.collectdata.handleData.HandleDataManager;
import com.xdja.collectdata.handleData.entity.BatteryHandleResult;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.database.PerformanceDB;
import com.xdja.util.CommonUtil;
import com.xdja.util.ProPertiesUtil;
import com.xdja.util.SwingUiUtil;

public class BatteryView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private Thread batteryThread;
	private List<BatteryData> batteryDataList;

	private final static String NOMESSGE = "�ռ��������ݣ���Ҫ�ε�USB���ӣ�Ȼ��ִ���Լ��Ĳ����������ٴ�����USB����������";
	private CategoryPlot mPlot;
	private DefaultCategoryDataset mDataset = null;
	private BatteryHandleResult batteryHandleResult = null;

	public BatteryView(String chartContent, String title, String yaxisName) {
		super();
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
		mRenderer.setMaximumBarWidth(1);
		mRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		mRenderer.setBaseItemLabelsVisible(true);
		mPlot.setRenderer(mRenderer);

		// ��freechart��ӵ������
		ChartPanel chartPanel = new ChartPanel(mBarchart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
		chartPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		// ��ӡ���ʼ���ԡ������������ݡ���ť
		JButton startBtn = SwingUiUtil.getInstance().createBtnWithColor("��ʼ����", Color.green);
		JButton parseBtn = SwingUiUtil.getInstance().createBtnWithColor("��������", Color.RED);
		startBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startBtn.setEnabled(false);

				// ��¼��һ�β��Ե�packageName
				ProPertiesUtil.getInstance().writeProperties(Constants.LAST_PACKAGENAME, GlobalConfig.PackageName);
				// ��������
				boolean isSuc = CollectDataImpl.clearBatteryData();
				if (isSuc) {
					// show dialog
					SwingUiUtil.getInstance().showTipsDialog(BatteryView.this, "��ʾ", "���Ե�������ε�usb��Ȼ��ִ���Լ��Ĳ�����������", "�õ�",
							null);
				}
			}
		});

		parseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startBtn.setEnabled(true);

				try {
					String lastPackageName = ProPertiesUtil.getInstance().getProperties(Constants.LAST_PACKAGENAME);
					if (CommonUtil.strIsNull(lastPackageName) || !lastPackageName.equals(GlobalConfig.PackageName)) {
						SwingUiUtil.getInstance().showTipsDialog(BatteryView.this, "��ʾ", "��ѡ���ϴβ��Ե�Ӧ�ð���֮����ִ�н������ݲ���",
								"�õ�", null);
						return;
					}
					start(lastPackageName);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		chartPanel.add(startBtn);
		chartPanel.add(parseBtn);
		addJpanel(chartPanel);
	}

	/**
	 * Adds an observation to the 'total memory' time series.
	 *
	 * @param fpsdata
	 *            the total memory used.FpsData
	 */

	/**
	 * ��ʼ����
	 * 
	 * @param packageName
	 * @throws InterruptedException
	 */
	public void start(String packageName) throws InterruptedException {

		batteryThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					batteryDataList = CCRDFile.getpowerdata(packageName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (batteryDataList != null || batteryDataList.size() > 0) {
					for(BatteryData batteryData : batteryDataList){
						mDataset.addValue(batteryData.getBatteryValue(), "uid", batteryData.getUid());
					}
					
					if (mPlot != null) {
						mPlot.setDataset(mDataset);
					}
				}
				
				handleBatteryData();
			}

		});
		batteryThread.start();
	}
	
	/**
	 * �����������
	 */
	private void handleBatteryData(){
		if (batteryDataList == null || batteryDataList.size() < 0) {
			return;
		}
		
		List<BatteryHandleResult> batteryHandleResults = HandleDataManager.getInstance().handleBatteryData(batteryDataList);
		if (batteryHandleResults == null || batteryHandleResults.size() < 1) {
			return;
			
		}
		
		// ���浽���ݿ�
		saveDataToDb(batteryHandleResults);
		
		// ��ʾ�쳣��Ϣ
		for(BatteryHandleResult result : batteryHandleResults){
			if (!result.result) {
				mShowMessageView.append(formatErrorInfo(result, result.testValue, "�������Ĺ���"));
			}
		}
	}
	
	/**
	 * �����ݱ��浽���ݿ���
	 */
	public void saveDataToDb(List<BatteryHandleResult> batteryHandleResults){
		if (batteryDataList == null) {
			return;
		}
		
		PerformanceDB.getInstance().insertBatteryData(batteryHandleResults);
		
		PerformanceDB.getInstance().closeDB();
	}
}
