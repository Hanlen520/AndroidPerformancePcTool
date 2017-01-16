package com.xdja.view;

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
import com.xdja.collectdata.handleData.HandleDataResult;
import com.xdja.constant.GlobalConfig;
import com.xdja.database.performancedata;

public class CpuView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private TimeSeries totalcpu;
	private Thread cpuThread;
	private boolean stopFlag = false;
	private CpuData mCurCpuData = null;
	public static int i =0;  //��Ӿ�̬�����������������жϣ���ʱ�ɼ�CPU������������ģ�͵��ж�
	public static HandleDataResult errorRe ;//����һ�����ݴ���������ڽ��պʹ�������ģ���е�����
	public static ArrayList<ArrayList<String>> cpudbdata;//�����ά�����б����ڽ��ղ������ݲ��洢�����������������ݿ�
	public static ArrayList<String> cpudbd;//����һά�����б����ڽ��ղ������ݲ��洢�����������������ݿ�
	
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
			appendErrorInfo(formatErrorInfo(handleDataResult, cpuData));
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
		sbBuilder.append("ActivityName = ").append(result.activityName).append("\n");
		sbBuilder.append("��ǰ����ֵ              = ").append(cpuData.cpuUsage).append("\n");
		sbBuilder.append("Logfile      = ").append(result.logPath).append("\n");
		sbBuilder.append("����·��                  = ").append(result.screenshotsPath).append("\n");
		sbBuilder.append("methodTrace  = ").append(result.methodTracePath).append("\n");
		sbBuilder.append("===================== \n\n\n");
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

					addTotalObservation(mCurCpuData);//��ӵ�ǰCPU����̬ͼ����
					
					errorRe = handleData(mCurCpuData);//����ɼ�����CPU���ݣ���������ģ�ʹ������
					cpudbd.add(errorRe.CPUString());//ת�����Ϊ�ַ�������
					cpudbd.add(errorRe.activityName);//ת�����Ϊ�ַ�������
					cpudbd.add(errorRe.reString());//ת�����Ϊ�ַ�������
					cpudbd.add((String)errorRe.screenshotsPath);//ת�����Ϊ�ַ�������
					cpudbd.add(errorRe.logPath);//ת�����Ϊ�ַ�������
					cpudbd.add(errorRe.methodTracePath);//ת�����Ϊ�ַ�������
					cpudbd.add(errorRe.memoryTracePath);//ת�����Ϊ�ַ�������
					cpudbdata.add(cpudbd);//��ӵ���ά�б�
					cpudbd.clear();//����һά���б�Ԫ��
					
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
	 * ����ɼ�����CPU���ݵģ�������ݴ����߼��ͷ���,�����쳣���ݴ���������CPU��ֵ������Ϊtrue,Ҳ����ΪFALSE
	 */
	public HandleDataResult handleData(CpuData cpuData) {
		i =i+1;     //�ڲɼ�Ƶ�ʵķ���������߼��жϼ�����,��Ӽ�ʱ������;�ⲿ�ּ�����Ҳ���Է������߳�ѭ����
    	if (i>10){
    		i=1;
    	}
    	if (i ==10){     //�����߳�������߼��жϣ���������ʱִ����ط���
    		HandleDataManager.getInstance().cpuList(cpuData);
		}
    	HandleDataResult abresult = HandleDataManager.getInstance().handleCpu(cpuData);
    	return abresult;
	}
	
	/**
	 * ���浽���ݿ⣬savetodb()
	 */
	
	
	/**
	 * ֹͣ���񣬱���߳�ֹͣ�������������ݲ������ݿ⡣
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public void stop()  {
		stopFlag = true;
		performancedata perfor = new performancedata();
		try {
			perfor.conperformance("test");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			perfor.creatperformance(performancedata.stat,performancedata.conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//�������ݿ�performance
		try {
			perfor.conperformance("performance");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //�������ݿ�performance
		perfor.CPUteble(performancedata.stat,"cputable","com.xdja.actoma","V3.3056.1","CPU");//�������ݱ�cputable
		try {
			perfor.insertDatas(performancedata.stat,cpudbdata,"cputable");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//������������
		perfor.closeperformance(performancedata.conn,performancedata.stat,performancedata.result);//�ر����ݿ�����
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
