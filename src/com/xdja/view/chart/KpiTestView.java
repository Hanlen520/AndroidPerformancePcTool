package com.xdja.view.chart;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;

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
import com.xdja.collectdata.entity.KpiData;
import com.xdja.collectdata.handleData.HandleDataManager;
import com.xdja.collectdata.handleData.entity.KpiHandleResult;
import com.xdja.constant.GlobalConfig;

public class KpiTestView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private static final String NOMESSGE = "��ҳ��������ݣ����ڲ���App���л�ҳ���ռ����ݣ���";
	private boolean stopFlag = false;
	private Thread kpiThread, kdatathread;
	private List<KpiData> KpiData = null;
	private List<KpiHandleResult> kpiHandleList = new ArrayList<>(12);
	private List<KpiHandleResult> errorList = new ArrayList<>(12);

	private CategoryPlot mPlot;
	private DefaultCategoryDataset mDataset = null;

	public KpiTestView(String chartContent, String title, String yaxisName) {
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
		addJpanel(chartPanel);
	}

	/**
	 * ��ʼ����
	 * 
	 * @param packageName
	 * @throws InterruptedException
	 */
	public void start(String packageName) throws InterruptedException {

		kdatathread = new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub

				CollectDataImpl.startCollectKpiData(packageName);
			}
		});
		kdatathread.start();
		kpiThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopFlag = false;
				while (true) {
					if (stopFlag) {
						break;
					}

					KpiData = CollectDataImpl.getKpiData();
					if (KpiData != null) {
						int listSize = KpiData.size();
						// mDataset = new DefaultCategoryDataset();
						for (int i = 0; i < listSize; i++) {
							mDataset.addValue(KpiData.get(i).loadTime, "kpi", KpiData.get(i).currentPage);
						}
						if (mPlot != null) {
							mPlot.setDataset(mDataset);
						}
					}
					
					handleKpiData(KpiData);
					try {
						Thread.sleep(GlobalConfig.collectMIDDLEInterval);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		kdatathread.join(1000);
		kpiThread.start();
	}

	/**
	 *  ����kpi��ص�����
	 */
	private void handleKpiData(List<KpiData> kpiList){
		kpiHandleList = HandleDataManager.getInstance().handleKpiData(kpiList);
		if (kpiHandleList == null || kpiHandleList.size() < 1) {
			return;
		}
		//��������Ϣչʾ
		for(KpiHandleResult kpiHandle : kpiHandleList){
			if (!kpiHandle.result) {
				if (errorList.contains(kpiHandle)) {
					continue;
				}
				errorList.add(kpiHandle);
				mShowMessageView.append(formatErrorInfo(kpiHandle, kpiHandle.testValue, "ҳ�����ʱ�����"));
			}
		}
	}
	
	/**
	 *  ����kpi��صĴ�������
	 * @return
	 */
	public List<KpiHandleResult> getHandleKpiList() {
		return kpiHandleList;
	}
	
	public void stop() {
		stopFlag = true;
		CollectDataImpl.stopCollectKpiData();
	}

	public void clear() {
		if (KpiData != null) {
			KpiData.clear();
		}
	}

}