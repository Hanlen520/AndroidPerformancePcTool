package com.xdja.collectdata.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import com.xdja.adb.AdbManager;
import com.xdja.collectdata.CollectDataImpl;
import com.xdja.util.CommonUtil;

public class SaveLogThread extends Thread{
	private File mCurLogFile;
	private String cmds = "";
	private Process mProcess;
	private int mCurPid;
	private final static long GetLogTime = 10 * 1000; // �ɼ�logʱ�䣬30s
	/**
	 * 
	 * @param deviceName 
	 * @param testtype ���Ե����ͣ�Ϊ�˱����־
	 */
	public SaveLogThread(String deviceName, String packageName, String filePath){
		mCurLogFile = new File(filePath);
		if (!mCurLogFile.exists()) {
			try {
				mCurLogFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (CommonUtil.strIsNull(packageName)) {
			mCurPid = 0;
		} else {
			mCurPid = CollectDataImpl.getPid(packageName);
		}
		
		String serialName = AdbManager.getInstance().getSerialNumber(deviceName);
		if (CommonUtil.strIsNull(serialName)) {
			cmds = "adb logcat  -v time *:v";
		}else {
			cmds = "adb -s " + serialName + " logcat  -v time *:v";
		}
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		FileOutputStream fos = null;
		BufferedReader mReader = null;
		try {
			startTimer();
			mProcess = Runtime.getRuntime().exec(cmds);
			mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()), 1024);
			String line;
			System.out.println("begin line = ");
			while ((line = mReader.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}
				fos = new FileOutputStream(mCurLogFile, true);
				if (mCurPid != 0) {
					if (fos != null && line.contains(String.valueOf(mCurPid))) {
						fos.write((line + "\n").getBytes());
					}
				}else {
					if (fos != null) {
						fos.write((line + "\n").getBytes());
					}
				}
			}
			System.out.println("end line = ");
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			if(mProcess != null){  
                mProcess.destroy();  
                mProcess = null;  
            }  
            try {  
                if(mReader != null){  
                    mReader.close();  
                    mReader = null;  
                }  
                if(fos != null){  
                    fos.close();  
                    fos = null;  
                }  
            } catch (Exception e2) {  
                e2.printStackTrace();  
            }  
		}
	}
	
	
	/**
	 *  ����һ����ʱ������Ƿ�ֹͣ����
	 *  
	 */
	private void startTimer(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ���ٽ���
				destoryProcess();
				
				// ֹͣ�߳�
				if (isAlive()) {
					interrupt();
				}
			}
		}, GetLogTime);
	}
	
	/**
	 *  ���ٽ���
	 */
	public void destoryProcess(){
		if (mProcess != null) {
			mProcess.destroy();
		}
	}
}
