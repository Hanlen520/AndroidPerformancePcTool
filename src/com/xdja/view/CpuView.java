package com.xdja.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
import com.xdja.collectdata.entity.CpuData;
import com.xdja.collectdata.handleData.HandleDataManager;
import com.xdja.collectdata.handleData.HandleDataResult;
import com.xdja.constant.GlobalConfig;

public class CpuView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private TimeSeries totalcpu;
	private Thread cpuThread;
	private boolean stopFlag = false;
	private CpuData mCurCpuData = null;

	public CpuView(String chartContent, String title, String yaxisName) {
		super();
		this.totalcpu = new TimeSeries("Ӧ��CPUռ����");
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(this.totalcpu);

		DateAxis domain = new DateAxis("Time");
		NumberAxis range = new NumberAxis("CPU(%)");
		domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
		range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));

		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.red);
		// renderer.setSeriesPaint(1, Color.green);
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

		JFreeChart chart = new JFreeChart("Ӧ��CPUʹ�����", new Font("SansSerif", Font.BOLD, 24), plot, true);
		chart.setBackgroundPaint(Color.white);
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
	private void addTotalObservation(CpuData cpuData) {
		if (cpuData == null) {
			return;
		}
		
		this.totalcpu.addOrUpdate(new Millisecond(), cpuData.cpuUsage);
		
		// �����ݽ����ж�
		HandleDataResult handleDataResult = HandleDataManager.getInstance().handleCpu(cpuData);
		// ��¼����
		mHandleDataList.add(handleDataResult);
		if (handleDataResult != null && !handleDataResult.result) {
			// ��������Ϣ
			if (mShowMessageView != null) {
				mShowMessageView.append(formatErrorInfo(handleDataResult, cpuData));
			}
		}
	}

	/**
	 * ��ʽ��������Ϣ
	 * 
	 * @param result
	 * @return
	 */
	private String formatErrorInfo(HandleDataResult result, CpuData cpuData) {
		StringBuilder sbBuilder = new StringBuilder("===================== \n");
		sbBuilder.append("ActivityName = ").append(result.activityName);
		sbBuilder.append("��ǰ����ֵ              = ").append(cpuData.cpuUsage);
		sbBuilder.append("Logfile      = ").append(result.logPath);
		sbBuilder.append("����·��                  = ").append(result.screenshotsPath);
		sbBuilder.append("methodTrace  = ").append(result.methodTracePath);
		sbBuilder.append("===================== \n\n\n\n");
		return sbBuilder.toString();
	}

	/**
	 * ��������
	 * 
	 * @param packageName
	 */
	public void start(String packageName) {
		// ����ϴμ�¼������
		if (mHandleDataList.size() > 0) {
			mHandleDataList.clear();
		}

		if (mShowMessageView != null) {
			mShowMessageView.setText("");
		}
		
		cpuThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopFlag = false;
				mCurCpuData = null;
				while (true) {
					if (stopFlag) {
						break;
					}

					if (mCurCpuData == null) {
						mCurCpuData = CollectDataImpl.getCpuUsage(packageName, 0, 0);
						continue;
					}

					mCurCpuData = CollectDataImpl.getCpuUsage(packageName, mCurCpuData.lastProcTotal,
							mCurCpuData.lastProcPid);

					addTotalObservation(mCurCpuData);

					try {
						Thread.sleep(GlobalConfig.collectInterval);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		cpuThread.start();
	}

	/**
	 * ֹͣ����
	 */
	public void stop() {
		stopFlag = true;
	}

	/**
	 * ��ȡ�����������
	 * 
	 * @return
	 */
	public List<HandleDataResult> getHandleResult() {
		return mHandleDataList;
	}
}
