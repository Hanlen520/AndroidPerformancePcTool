package com.xdja.view;

//import org.apache.log4j.Logger;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.ValueAxis;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.data.time.TimeSeries;
//import org.jfree.data.time.Millisecond;
//import org.jfree.data.time.TimeSeriesCollection;
//
//import com.xdja.constant.Constants;
//import com.xdja.monitor.ControllerMonitor;
//
//public class BatteryView extends ChartPanel {
//
//	/**
//	 * serial UID is auto generated
//	 */
//	private static final long serialVersionUID = 6214606803165478469L;
//	private Logger logger = Logger.getLogger(BatteryView.class);
//	private Thread batteryThread;
//	private static TimeSeries timeSeries;
//	private boolean stopFlag = false;
//	
//	public BatteryView(String chartContent, String title, String yaxisName) {
//		this(createChart(chartContent, title, yaxisName));
//	}
//
//	public BatteryView(JFreeChart chart) {
//		super(chart);
//	}
//	
//	public void start(final String packageName) {
//		batteryThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				stopFlag = false;
//				while(true) {
//					if (!stopFlag) {
//						try {
//							double info = ControllerMonitor.getInstance().getBatteryController().getInfo(packageName);
//							timeSeries.add(new Millisecond(), info);
//							logger.info(String.format("Package \"%s\" Battery: %f%%", packageName, info));
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							logger.error(e.getMessage(), e.getCause());
//							e.printStackTrace();
//						}
//					} else {
//						logger.info("Battery View test is stoped!");
//						break;
//					}
//				}
//			}
//		});
//		batteryThread.start();
//	}
//	
//	public void stop() {
//		stopFlag = true;
//	}
//	
//	public static JFreeChart createChart(String chartContent, String title, String yaxisName) {
//		timeSeries = new TimeSeries(chartContent, Millisecond.class);
//		TimeSeriesCollection dataset = new TimeSeriesCollection(timeSeries); 
//		//params:ͼ����⣬ͼ��x�ᣬͼ��y�ᣬ���ݼ�����ʾͼ�������ñ�׼���������Ƿ����ɳ�����
//		JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(title, Constants.TIME_UNIT, yaxisName, dataset, true, true, false);
//		// ��ȡplot����
//		XYPlot xyplot = timeSeriesChart.getXYPlot();
//		// ��ȡx�����
//		ValueAxis valueaxis = xyplot.getDomainAxis();
//		// �Զ��������������ݷ�Χ
//		valueaxis.setAutoRange(true);
//		// ������̶����ݷ�Χ 30s
//		valueaxis.setFixedAutoRange(60000D);
//		// ��ȡy�����
//		valueaxis = xyplot.getRangeAxis();
//		return timeSeriesChart;
//	}

//}



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
import com.xdja.collectdata.CCRDFile;
import com.xdja.collectdata.FpsData;
import com.xdja.collectdata.KpiData;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;



public class BatteryView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private boolean stopFlag = false;
	private Thread batteryThread;
	private String[][] batteryData ;
	
	
	private CategoryPlot mPlot;
	private DefaultCategoryDataset mDataset  = null;
	
	
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
		        stopFlag = false;
				try {
					batteryData = CCRDFile.getpowerdata(packageName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					System.out.println(batteryData);
					if (batteryData != null) {
						for (int i=0;i<batteryData.length;i++){
						mDataset.addValue(Double.parseDouble((batteryData[i][2])), "uid",batteryData[i][0]);
						}
						if (mPlot != null) {
							mPlot.setDataset(mDataset);
						}
					}
				}
			
		});
		batteryThread.start();
	}

	public void stop() {
		stopFlag = true;
    }
	
	

}
