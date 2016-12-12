package com.xdja.util;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.xdja.constant.Constants;

/**
 * �������ɸ��ֶԻ���Ĺ�����
 * @author zlw
 *
 */
public class DialogUtil {
	private volatile static DialogUtil mInstance;
	
	private DialogUtil(){
		
	}
	
	public static DialogUtil getInstance() {
		if (mInstance == null) {
			synchronized (DialogUtil.class) {
				if (mInstance == null) {
					mInstance = new DialogUtil();
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
		
		Object[] options = { Constants.CONFIRM, Constants.CANCEL };
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
	 *  ����Ի���ť�ļ����¼�
	 * @author zlw
	 *
	 */
	public interface ClickDialogBtnListener{
		public void clickOkBtn();
		public void clickCancelBtn();
	}
}
