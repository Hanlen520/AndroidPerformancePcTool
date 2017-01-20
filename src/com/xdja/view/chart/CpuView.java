package com.xdja.view.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;
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
import com.xdja.collectdata.entity.CpuData;
import com.xdja.collectdata.handleData.HandleDataManager;
import com.xdja.collectdata.handleData.entity.CpuHandleResult;
import com.xdja.constant.GlobalConfig;

public class CpuView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private TimeSeries totalcpu;
	private Thread cpuThread;
	private CpuData mCurCpuData = null;
	public static int cpuCount = 0; // ���Ӿ�̬�����������������жϣ���ʱ�ɼ�CPU������������ģ�͵��ж�
	private List<CpuHandleResult> cpuHandleResults = new ArrayList<>(20);
	public boolean slient = false; // �Ƿ��Ǿ�Ĭ����

	public CpuView(String chartContent, String title, String yaxisName, boolean slient) {
		super();
		initView(chartContent, title, yaxisName);
	}

	public CpuView(String chartContent, String title, String yaxisName) {
		super();
		initView(chartContent, title, yaxisName);
	}

	private void initView(String chartContent, String title, String yaxisName) {
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
	 * �����Ƿ��Ǿ�Ĭ����
	 * 
	 * @param isSlient
	 *            �Ƿ��Ǿ�Ĭ����
	 */
	public void setSlientTest(boolean isSlient) {
		slient = isSlient;
	}

	/**
	 * 
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
	}

	/**
	 * ��������
	 * 
	 * @param packageName
	 */
	public void start(String packageName) {
		// ����ϴμ�¼������
		if (cpuHandleResults.size() > 0) {
			cpuHandleResults.clear();
		}

		cpuThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopFlag = false;
				mCurCpuData = null;
				isRunning = true;
				// �жϴ�����ʾ������ʾ����
				if (slient) {
					String slientStr = "===========��ʼCPU��Ĭ����=============\n\n";
					appendErrorInfo(slientStr);
				} else {
					String normalStr = "===========��ʼCPU����=============\n\n";
					appendErrorInfo(normalStr);
				}
				while (true) {
					if (stopFlag) {
						isRunning = false;
						break;
					}

					if (mCurCpuData == null) {
						mCurCpuData = CollectDataImpl.getCpuUsage(packageName, 0, 0);
						continue;
					}

					mCurCpuData = CollectDataImpl.getCpuUsage(packageName, mCurCpuData.lastProcTotal,
							mCurCpuData.lastProcPid);

					addTotalObservation(mCurCpuData);// ���ӵ�ǰCPU����̬ͼ����

					// �����ɼ�����CPU���ݣ���������ģ�ʹ�������
					if (slient) {
						handleSlientData(mCurCpuData);
					}else {
						handleData(mCurCpuData);
					}

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
	 * �����ɼ�����CPU���ݵģ�������ݴ����߼��ͷ���,�����쳣���ݴ����������CPU��ֵ������Ϊtrue,Ҳ����ΪFALSE
	 */
	public void handleData(CpuData cpuData) {
		// �����ݽ����ж�
		CpuHandleResult handleDataResult = HandleDataManager.getInstance().handleCpu(cpuData, cpuData.cpuUsage);
		if (handleDataResult == null) {
			return;
		}
		// ��¼����
		cpuHandleResults.add(handleDataResult);

		// ��������Ϣ
		if (handleDataResult != null && !handleDataResult.result) {
			appendErrorInfo(formatErrorInfo(handleDataResult, String.valueOf(cpuData.cpuUsage), "cpuʹ���ʹ���"));
		}
	}

	/**
	 * ������Ĭ״̬������
	 * ��10s�ڣ�cpuһֱ����0.5ʱ��������Ϊ�ǲ������ġ�
	 * @param cpuData
	 */
	public void handleSlientData(CpuData cpuData) {
		CpuHandleResult handleCpu = HandleDataManager.getInstance().handleCpusilence(cpuData, cpuData.cpuUsage);
		if (handleCpu == null) {
			return;
		}

		// ��¼����
		cpuHandleResults.add(handleCpu);
		// ��������Ϣ
		if (handleCpu != null && !handleCpu.result) {
			appendErrorInfo(formatErrorInfo(handleCpu, String.valueOf(cpuData.cpuUsage), "��Ĭ״̬ʹ��CPU"));
		}
	}

	/**
	 * ֹͣ���񣬱���߳�ֹͣ�������������ݲ������ݿ⡣
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
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

	public List<CpuHandleResult> getHandleResult() {
		return cpuHandleResults;
	}

	public void destoryData() {
		if (cpuHandleResults != null) {
			cpuHandleResults.clear();
			cpuHandleResults = null;
		}
	}
}