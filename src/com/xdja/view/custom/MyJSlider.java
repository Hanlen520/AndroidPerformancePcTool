package com.xdja.view.custom;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MyJSlider extends JPanel implements ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2137850386165152756L;
	
	private JLabel mJSliderTitle;
	private JSlider mJSlider;
	private JLabel mJSliderValue; // ��ʾѡ���value
	
	/**
	 * 
	 * @param sliderTitle ����
	 * @param direction ����Ļ���ѡ�����ķ���
	 */
	public MyJSlider(int direction, String sliderTitle) {
		super();
		// TODO Auto-generated constructor stub
		if(sliderTitle == null || "".equals(sliderTitle)){
			sliderTitle = "default";
		}
		mJSliderTitle = new JLabel(sliderTitle);
		add(mJSliderTitle);
		
		mJSlider = new JSlider(JSlider.HORIZONTAL);
		mJSlider.setPaintTicks(true);
		mJSlider.setPaintLabels(true);
		mJSlider.setMajorTickSpacing(20);
		mJSlider.setMinorTickSpacing(5);
		mJSlider.addChangeListener(this);
		add(mJSlider);
		
		mJSliderValue = new JLabel();
		add(mJSliderValue);
		
		setBounds(0,  0, 200, 50);
	}
	
	/**
	 *  ����Slider�ı���
	 * @param title
	 */
	public void setSliderTitle(String title){
		if(title == null || "".equals(title)){
			title = "default";
		}
	}
	
	/**
	 * ����slider�����ֵ
	 * @param max
	 */
	public void setSliderMaxValue(int max){
		if (mJSlider != null) {
			mJSlider.setMaximum(max);
		}
	}
	
	/**
	 * ����slider����Сֵ
	 * @param min
	 */
	public void setSliderMinValue(int min){
		if(mJSlider != null){
			mJSlider.setMinimum(min);
		}
	}
	
	/**
	 * ����slider�ĵ�ǰ��ֵ
	 * @param curValue
	 */
	public void setSliderCurValue(int curValue){
		if (mJSlider != null) {
			mJSlider.setValue(curValue);
		}
	}
	
	/**
	 * ��ȡ��ǰSlider��ֵ
	 */
	public int getSliderValue(){
		if (mJSlider != null) {
			return mJSlider.getValue();
		}
		
		return 0;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() instanceof JSlider) {
			JSlider source = (JSlider) e.getSource();
			if (mJSliderValue != null) {
				mJSliderValue.setText("ѡ��" + String.valueOf(source.getValue()));
				mJSliderValue.setForeground(Color.red);
				mJSliderValue.setFont(new Font("����",  Font.PLAIN, 20));
			}
		}
	}
	
	/**
	 * ����һ������ӵļ��
	 * @param spac
	 */
	public void setMajorTickSpacing(int spac){
		if (spac <= 0) {
			spac = 1;
		}
		if (mJSlider != null) {
			mJSlider.setMajorTickSpacing(spac);
		}
	}
	
	/**
	 * ����һ��С���ӵļ��
	 * @param spac
	 */
	public void setMinTickSpacing(int spac){
		if (spac < 0) {
			spac = 0;
		}
		if (mJSlider != null) {
			mJSlider.setMinorTickSpacing(spac);
		}
	}
}
