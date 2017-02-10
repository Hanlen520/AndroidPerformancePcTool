package com.xdja.view.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.entity.FlowData;
import com.xdja.collectdata.handleData.HandleDataManager;
import com.xdja.collectdata.handleData.entity.FlowHandleResult;
import com.xdja.constant.GlobalConfig;

public class FlowView extends BaseChartView {

	/**
	 * serial UID is auto generated
	 */
	private static final long serialVersionUID = 1719925024734975743L;
	private TimeSeries flowCost;
	private FlowData mFlowData;
	private float mLastFlow = -1;
	private Thread flowThread;
	// ������������ʱ������
	private List<FlowHandleResult> mFlowHandleResults = new ArrayList<>(12);
	// ����Ĭ����ʱ������
	private List<FlowHandleResult> mFlowHandleSlientResults = new ArrayList<>(12);
	public boolean slient = false;

	public FlowView(String chartContent, String title, String yaxisName) {
		super();
		this.flowCost = new TimeSeries(chartContent);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(this.flowCost);

		DateAxis domain = new DateAxis("Time");
		NumberAxis range = new NumberAxis("��������(KB)");
		domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
		range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));

		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesStroke(0, new BasicStroke(3F));

		XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		
		domain.setAutoRange(true);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(true);

		range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JFreeChart chart = new JFreeChart(title, new Font("SansSerif", Font.BOLD, 24), plot, true);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
		addJpanel(chartPanel);
	}

	/**
	 * Adds an observation to the 'total memory' time series.
	 *
	 * @param y
	 *            the total memory used.
	 */
	private void addFlowObservation(double y) {
		this.flowCost.addOrUpdate(new Millisecond(), y);
	}

	public void destoryData() {
		if (mFlowHandleResults != null) {
			mFlowHandleResults.clear();
			mFlowHandleResults = null;
		}

		if (mFlowHandleSlientResults != null) {
			mFlowHandleSlientResults.clear();
			mFlowHandleSlientResults = null;
		}
	}

	public List<FlowHandleResult> getHandleResultList() {
		
		return mFlowHandleSlientResults;
	}
	
	

	/**
	 * ��������
	 * 
	 * @param flowData
	 */
	public void handleFlowData(float flowData) {

		FlowHandleResult flowHandle = HandleDataManager.getInstance().handleFlowData(flowData);
		if (flowHandle == null) {
			return;
		}
         mFlowHandleResults.add(flowHandle);
		if (flowHandle.isShowErrorMsg) {
			mShowMessageView.append(formatErrorInfo(flowHandle, flowHandle.testValue, "�������Ĺ���"));
		}

		
	}

	/**
	 * ����Ĭ״̬��������
	 * 
	 * @param flowData
	 */
	public void handleSlientData(float flowTotal) {
		FlowHandleResult flowHandle = HandleDataManager.getInstance().handleFlowSlientData(flowTotal);
		if (flowHandle == null) {
			return;
		}

		if (!flowHandle.result) {
			mShowMessageView.append(formatErrorInfo(flowHandle, flowHandle.testValue, "��Ĭ״̬�������Ĺ���"));
		}

		mFlowHandleSlientResults.add(flowHandle);
	}

	/**
	 * �����Ƿ���е��Ǿ�̬����
	 * 
	 * @param isSlient
	 */
	public void setSlient(boolean isSlient) {
		slient = isSlient;
	}

	/**
	 * ��ʼ����
	 * 
	 * @param packageName
	 */
	public void start(String packageName) {
		flowThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopFlag = false;
				mLastFlow = -1;
				isRunning = true;
				// �����ڴ�����Ϣչʾʲô��ʾ
				if (slient) {
					String slientStr = "===========��ʼ������Ĭ����=============\n\n";
					appendErrorInfo(slientStr);
				} else {
					String normalStr = "===========��ʼ��������=============\n\n";
					appendErrorInfo(normalStr);
				}

				while (true) {
					if (stopFlag) {
						isRunning = false;
						break;
					}

					mFlowData = CollectDataImpl.getFlowData(packageName);
					
					if (mFlowData != null) {
						if (mLastFlow == -1) {
							addFlowObservation(0);
							mLastFlow = mFlowData.flowTotal;
							continue;
						}
						float value = mFlowData.flowTotal - mLastFlow;
						addFlowObservation(value);
						mLastFlow = mFlowData.flowTotal;
						
						if (slient) {
							handleSlientData(value);
						}else {
							handleFlowData(value);
						}
						try {
							Thread.sleep(GlobalConfig.collectInterval);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

		flowThread.start();
	}

	/**
	 * ֹͣ�ɼ�����
	 */
	public void stop() {
		stopFlag = true;
		// �жϴ�����ʾ������ʾ����
		if (slient) {
			String slientStr = "===========������Ĭ����=============\n\n";
			appendErrorInfo(slientStr);
		} else {
			String normalStr = "===========��������=============\n\n";
			appendErrorInfo(normalStr);
		}
	}

	public List<FlowHandleResult> getHanResultList() {
		// TODO Auto-generated method stub
		return mFlowHandleResults;
	}
}
