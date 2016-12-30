package com.xdja.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.FpsData;
import com.xdja.collectdata.KpiData;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;



public class KpiTestView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private boolean stopFlag = false;
	private Thread kpiThread,kdatathread;
	private List<KpiData> KpiData  = null;
	
	
	private CategoryPlot mPlot;
	private DefaultCategoryDataset mDataset  = null;
	
	
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
		// x��
		CategoryAxis mDomainAxis = mPlot.getDomainAxis();
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
		
		//��freechart��ӵ������
		ChartPanel chartPanel = new ChartPanel(mBarchart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
		add(chartPanel);
	}

	/**
	 * Adds an observation to the 'total memory' time series.
	 *
	 * @param fpsdata
	 *            the total memory used.FpsData
	 */
	

//
//	/**
//	 * ��ʼ����
//	 * 
//	 * @param packageName
//	 */
//	public void start(String packageName) {
//		
//		new Thread(new Runnable(){
//			
//			public void run() {
//				// TODO Auto-generated method stub
//			CollectDataImpl.startCollectKpiData(packageName);	
//			}}).start();
//		
//		kpiThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				int i=0;
//				stopFlag = false;
//				while (true) {
//					if (stopFlag) {
//						break;
//					}
//					
//					KpiData = CollectDataImpl.getKpiData();
//					System.out.println(KpiData);
//					if (KpiData != null) {
//						int m = KpiData.size() ;
//						if (m>i){
//						mDataset.addValue(KpiData.get(i+1).loadTime, "kpi", KpiData.get(i+1).currentPage);
//						i++;
//						}else if (i>0) {
//							mDataset.addValue(KpiData.get(i).loadTime, "kpi", KpiData.get(i).currentPage);}
//						if (mPlot != null) {
//							mPlot.setDataset(mDataset);
//						}
//					}
//				}
//			}
//		});
//
//		kpiThread.start();
//	}
	
	
	/**
	 * ��ʼ����
	 * 
	 * @param packageName
	 * @throws InterruptedException 
	 */
	public void start(String packageName) throws InterruptedException {
		
		kdatathread = new Thread(new Runnable(){
			
			public void run() {
				// TODO Auto-generated method stub
			
			CollectDataImpl.startCollectKpiData(packageName);
			}});
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
					System.out.println(KpiData);
					if (KpiData != null) {
						int listSize=KpiData.size(); 
//						mDataset = new DefaultCategoryDataset();
						for (int i=0;i<listSize;i++){
						mDataset.addValue(KpiData.get(i).loadTime, "kpi", KpiData.get(i).currentPage);
						}
						if (mPlot != null) {
							mPlot.setDataset(mDataset);
						}
					}
				}
			}
		});
		kdatathread.join(1000);
		kpiThread.start();
	}

	public void stop() {
		stopFlag = true;
    }
	
	public void clear() {
		if (KpiData!=null){
	KpiData.clear();}
	}

}