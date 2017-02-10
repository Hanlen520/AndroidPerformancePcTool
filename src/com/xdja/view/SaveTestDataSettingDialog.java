package com.xdja.view;

import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.xdja.constant.Constants;
import com.xdja.util.ProPertiesUtil;
import com.xdja.util.SwingUiUtil;

/**
 * ���ڱ���������ݵ�����ҳ��
 * 
 * @author zlw
 *
 */
public class SaveTestDataSettingDialog extends JDialog implements ItemListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1998104508054845824L;
	private Checkbox dbCheckbox, localCheckbox;
	private JTextField ipTextField, portTextField, dbNameTextField, localTextField, dbUserTextField, dbPwdTextField;
	private JPanel dbSettingPanel, localSettingPanel;
	private JButton mSaveButton;
	
	public SaveTestDataSettingDialog(Frame owner, String title) {
		super(owner, title);
		// TODO Auto-generated constructor stub
		// �Ƿ�ʹ�����ݿ�
		dbCheckbox = new Checkbox("ʹ�����ݿ�", false);
		dbCheckbox.setPreferredSize(new Dimension(500, 70));
		dbCheckbox.addItemListener(this);
		dbSettingPanel = initDBSettingPanel();

		// �Ƿ񱣴浽����
		localCheckbox = new Checkbox("���浽����", true);
		localCheckbox.setPreferredSize(new Dimension(500, 70));
		localCheckbox.addItemListener(this);
		localSettingPanel = initLocalSettingPanel();
		setdbSettingEnable(false);
		
		// �������ð�ť
		mSaveButton = new JButton("��������");
		mSaveButton.setPreferredSize(new Dimension(300, 50));
		mSaveButton.addActionListener(this);
		
		
		add(dbCheckbox);
		add(dbSettingPanel);
		add(localCheckbox);
		add(localSettingPanel);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBounds(600, 260, 700, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * ��ʼ�����ݿ����õ����
	 * 
	 * @return
	 */
	private JPanel initDBSettingPanel() {
		JPanel jPanel = new JPanel();

		// ���ݿ�ip��ַ
		JLabel iptitle = new JLabel("ip��ַ��          ");
		ipTextField = new JTextField(30);
		ipTextField.setText("localhost");
		jPanel.add(iptitle);
		jPanel.add(ipTextField);

		// ���ݿ�port��ַ
		JLabel porttitle = new JLabel("port��             ");
		portTextField = new JTextField(30);
		portTextField.setText("3306");
		jPanel.add(porttitle);
		jPanel.add(portTextField);

		// ���ݿ�����
		JLabel dbNametitle = new JLabel("���ݿ����ƣ�");
		dbNameTextField = new JTextField(30);
		dbNameTextField.setText("Performancedata");
		jPanel.add(dbNametitle);
		jPanel.add(dbNameTextField);
		// ���ݿ�����
		JLabel dbUsertitle = new JLabel("���ݿ��û�����");
		dbUserTextField = new JTextField(30);
		dbUserTextField.setText("Performancedata");
		jPanel.add(dbUsertitle);
		jPanel.add(dbUserTextField);
		// ���ݿ�����
		JLabel dbPwdtitle = new JLabel("���ݿ����ƣ�");
		dbPwdTextField = new JTextField(30);
		dbPwdTextField.setText("Performancedata");
		jPanel.add(dbPwdtitle);
		jPanel.add(dbPwdTextField);
		

		jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		jPanel.setPreferredSize(new Dimension(500, 100));
		return jPanel;
	}

	/**
	 * ��ʼ���������ݵ����ص����
	 * 
	 * @return
	 */
	private JPanel initLocalSettingPanel() {
		JPanel jPanel = new JPanel();

		// ����·��
		JLabel localNametitle = new JLabel("����·����");
		localTextField = new JTextField(40);
		localTextField.setText("C:\\performance");
		jPanel.add(localNametitle);
		jPanel.add(localTextField);

		jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		jPanel.setPreferredSize(new Dimension(500, 100));
		return jPanel;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		Checkbox checkbox = (Checkbox) e.getSource();
		String label = checkbox.getLabel();
		switch (label) {
		case "ʹ�����ݿ�":
			System.out.println("����");
			if (dbCheckbox != null && !dbCheckbox.getState()) {
				dbCheckbox.setState(true);
				setdbSettingEnable(true);
				
			} else {
				localCheckbox.setState(false);
				setdbSettingEnable(true);
			}
			break;
		case "���浽����":
			System.out.println("����");
			if (localCheckbox != null && !localCheckbox.getState()) {
				localCheckbox.setState(true);
				setdbSettingEnable(false);
			} else {
				dbCheckbox.setState(false);
				setdbSettingEnable(false);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 *  ����db����ҳ���Ƿ����ʹ��
	 * @param enable true ����
	 *               false ������
	 * 
	 */
	private void setdbSettingEnable(boolean enable){
		if (dbSettingPanel != null) {
			ipTextField.setEditable(enable);
			portTextField.setEditable(enable);
			dbNameTextField.setEditable(enable);
		}
		if (localSettingPanel != null) {
			localTextField.setEditable(!enable);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String command = e.getActionCommand();
		switch (command) {
		case "��������":
			saveDataToProprtites();
			break;

		default:
			break;
		}
	}
	
	/**
	 *  �����ݱ��浽�����ļ���
	 */
	private void saveDataToProprtites(){
		if (dbCheckbox != null) {
			boolean choose = dbCheckbox.getState();
			String chooseStr = choose ? "true" : "false";
			ProPertiesUtil.getInstance().writeProperties(Constants.DBSAVE_CHOOSE, chooseStr);
			if (choose) {
				if (ipTextField != null) {
					String ip = ipTextField.getText();
					ProPertiesUtil.getInstance().writeProperties(Constants.DBIP_SETTING, ip);
				}
				
				if (portTextField != null) {
					String port = portTextField.getText();
					ProPertiesUtil.getInstance().writeProperties(Constants.DBPORT_SETTING, port);
				}
				
				if (dbNameTextField !=null) {
					String dbName = dbNameTextField.getText();
					ProPertiesUtil.getInstance().writeProperties(Constants.DBNAME_SETTING, dbName);
				}
				
				if (dbUserTextField != null) {
					String user = dbUserTextField.getText();
					ProPertiesUtil.getInstance().writeProperties(Constants.DBUSER_SETTING, user);
				}
				
				if (dbPwdTextField != null) {
					String pwd = dbPwdTextField.getText();
					ProPertiesUtil.getInstance().writeProperties(Constants.DBPWD_SETTING, pwd);
				}
			}else {
				if (localTextField != null) {
					String localPath = localTextField.getText();
					ProPertiesUtil.getInstance().writeProperties(Constants.LOCALSAVE_SETTING, localPath);
				}
			}
		}
		SwingUiUtil.getInstance().showTipsDialog(this, "��ʾ", "�����Ѿ�����ɹ�", "ȷ��", null);
	}
}
