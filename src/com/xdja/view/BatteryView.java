
package com.xdja.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.util.CommonUtil;
import com.xdja.util.ProPertiesUtil;
import com.xdja.util.SwingUiUtil;

public class BatteryView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private Thread batteryThread;
	private String[][] batteryData ;
	
	private final static String  NOMESSGE = "�ռ��������ݣ���Ҫ�ε�USB���ӣ�Ȼ��ִ���Լ��Ĳ����������ٴ�����USB����������";
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
		
		//��freechart��ӵ������
		ChartPanel chartPanel = new ChartPanel(mBarchart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
		chartPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		//��ӡ���ʼ���ԡ������������ݡ���ť
		JButton startBtn = SwingUiUtil.getInstance().createBtnWithColor("��ʼ����", Color.green);
		JButton parseBtn = SwingUiUtil.getInstance().createBtnWithColor("��������", Color.RED);
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startBtn.setEnabled(false);
				
				//��¼��һ�β��Ե�packageName
				ProPertiesUtil.getInstance().writeProperties(Constants.LAST_PACKAGENAME, GlobalConfig.PackageName);
				//��������
				boolean isSuc = CollectDataImpl.clearBatteryData();
				if (isSuc) {
					//show dialog
					SwingUiUtil.getInstance().showTipsDialog(BatteryView.this, "��ʾ", "���Ե�������ε�usb��Ȼ��ִ���Լ��Ĳ�����������", "�õ�", null);
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
						SwingUiUtil.getInstance().showTipsDialog(BatteryView.this, "��ʾ", "��ѡ���ϴβ��Ե�Ӧ�ð���֮����ִ�н������ݲ���", "�õ�",null);
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
				try {
					batteryData = CCRDFile.getpowerdata(packageName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
}
