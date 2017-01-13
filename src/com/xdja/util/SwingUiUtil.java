package com.xdja.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicButtonUI;

import com.xdja.constant.Constants;

/**
 * �������ɸ��ֶԻ���Ĺ�����
 * @author zlw
 *
 */
public class SwingUiUtil {
	private volatile static SwingUiUtil mInstance;
	
	private SwingUiUtil(){
		
	}
	
	public static SwingUiUtil getInstance() {
		if (mInstance == null) {
			synchronized (SwingUiUtil.class) {
				if (mInstance == null) {
					mInstance = new SwingUiUtil();
				}
			}
		}
		
		return mInstance;
	}
	
	/**
	 * չʾ������ť�ĶԻ���
	 * @param parentComponent ���ؼ�
	 * @param title �Ի���ı���
	 * @param content �Ի��������
	 * @param ok_text �Ի����ȷ����ť���İ�
	 * @param cancel_text �Ի����ȡ����ť���İ�
	 */
	public void showOkAndCancelDialog(Component parentComponent, String title, String content, String ok_text, String cancel_text, ClickDialogBtnListener listener){
		if (parentComponent == null) {
			return;
		}
		
		if (title == null || "".equals(title)) {
			title = "����";
		}
		
		if (content == null || "".equals(content)) {
			content = "�㻹û�������İ�";
		}
		
		if (ok_text == null || "".equals(ok_text)) {
			ok_text = "ȷ��";
		}
		
		if (cancel_text == null || "".equals(cancel_text)) {
			cancel_text = "ȡ��";
		}
		
		Object[] options = { ok_text, cancel_text };
		JOptionPane warnPane = new JOptionPane(content,
				JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null,
				options, options[1]);
		JDialog dialog = warnPane.createDialog(parentComponent, title);
		dialog.setVisible(true);
		Object selectedValue = warnPane.getValue();
		if (selectedValue == null || selectedValue == options[1]) {
			listener.clickCancelBtn();
		} else if (selectedValue == options[0]) {
			listener.clickOkBtn();
		}
	}
	
	/**
	 *  չʾ��ʾ���İ��ĶԻ���ֻ��һ��ȷ����ť
	 * @param parentComponent
	 * @param title
	 * @param content
	 * @param ok_text
	 */
	public void showTipsDialog(Component parentComponent, String title, String content, String ok_text, ClickDialogBtnListener listener) {
		if (parentComponent == null) {
			return;
		}
		
		if (title == null || "".equals(title)) {
			title = "����";
		}
		
		if (content == null || "".equals(content)) {
			content = "�㻹û�������İ�";
		}
		
		if (ok_text == null || "".equals(ok_text)) {
			ok_text = "ȷ��";
		}
		
		Object[] options = { ok_text};
		JOptionPane warnPane = new JOptionPane(content,
				JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
				options, options[0]);
		
		JDialog dialog = warnPane.createDialog(parentComponent, title);
		dialog.setVisible(true);
		Object selectedValue = warnPane.getValue();
		if (selectedValue == null || selectedValue == options[0]) {
			if (listener != null) {
				listener.clickOkBtn();
			}
			dialog.setVisible(false);
			dialog.dispose();
		}
		
	}
	
	/**
	 *  ��ǰ��path·��
	 * @param curPath
	 */
	public void showChooseFileDialog(Component component, String curPath, chooseFileListener listener){
		JFileChooser fileChooser = null;
		if (CommonUtil.strIsNull(curPath)) {
			fileChooser = new JFileChooser();
		} else {
			fileChooser = new JFileChooser(curPath);
		}
		
		fileChooser.showOpenDialog(component);
		File selectFile = fileChooser.getSelectedFile();
		if (listener != null) {
			listener.chooseFile(selectFile);
		}
	}
	
	/**
	 *  ��ǰ��path·��
	 * @param curPath
	 */
	public void showSaveFileDialog(Component component, chooseFileListener listener){
		JFileChooser fileChooser = null;
		fileChooser = new JFileChooser();
		
		fileChooser.showSaveDialog(component);
		File selectFile = fileChooser.getSelectedFile();
		if (listener != null) {
			listener.chooseFile(selectFile);
		}
	}
	
	
	/**
	 * ���ڴ�����ͼ���icon
	 * @param text
	 * @param icon
	 * @return
	 */
	public JButton createBtnWithIcon(String text, String iconName){
		JButton button =  new JButton(text, new ImageIcon(Constants.IMG_PATH + iconName));
		button.setUI(new BasicButtonUI());
		button.setPreferredSize(new Dimension(80, 27));
		button.setContentAreaFilled(false);
		button.setFont(new Font("����", Font.PLAIN, 15));
		button.setMargin(new Insets(2, 2, 2, 2));
		return button;
	}
	
	/**
	 * ���ڴ�����ͼ���icon
	 * @param text
	 * @param color  ����ɫ
	 * @return
	 */
	public JButton createBtnWithColor(String text, Color color){
		JButton button =  new JButton(text);
		button.setUI(new BasicButtonUI());
		button.setPreferredSize(new Dimension(80, 27));
		button.setContentAreaFilled(true);
		button.setFont(new Font("����", Font.PLAIN, 15));
		button.setMargin(new Insets(2, 2, 2, 2));
		button.setBackground(color);
		return button;
	}
	
	/**
	 *  ��ȡ��Ļ�ĳߴ�
	 * @return
	 */
	public Dimension getScreenSize(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return screenSize;
	}
	
	
	/**
	 *  ����Ի���ť�ļ����¼�
	 * @author zlw
	 *
	 */
	public interface ClickDialogBtnListener{
		public void clickOkBtn();
		public void clickCancelBtn();
	}
	
	public interface chooseFileListener{
		//ѡ���ļ�
		public void chooseFile(File chooseFile);
	}
}
