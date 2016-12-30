package com.xdja.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.FpsData;
import com.xdja.constant.GlobalConfig;
import com.xdja.util.SwingUiUtil;

public class FpsView extends BaseChartView {

	/**
	 * serial UID auto generated
	 */
	private static final long serialVersionUID = -9002331611054515951L;
	private boolean stopFlag = false;
	private Thread fpsThread;
	private List<FpsData> fpsdataList = null;
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
			}
		});
		
		startBtn.setLocation(0, 30);
		pauseBtn.setLocation(30, 30);
		
		chartPanel.add(startBtn);
		chartPanel.add(pauseBtn);
		add(chartPanel);
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
					System.out.println(fpsdataList);
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
					
					try {
						Thread.sleep(GlobalConfig.collectInterval);
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
}
