package com.xdja.collectdata.thread;

import java.io.IOException;

import com.android.ddmlib.Client;
import com.android.ddmlib.ClientData;
import com.android.ddmlib.ClientData.IMethodProfilingHandler;
import com.xdja.collectdata.SaveEnvironmentManager;
import com.xdja.constant.Constants;
import com.xdja.log.LoggerManager;
import com.xdja.util.CommonUtil;

/**
 *  ����Method Trace���߳�
 * @author zlw
 *
 */
public class TraceMethodThread extends Thread implements IMethodProfilingHandler{
	private final static String LOGTAG = TraceMethodThread.class.getSimpleName();
	private String mType; //��������
	private Client mCurClient;
	
	public TraceMethodThread(String mType, Client client) {
		super();
		this.mType = mType;
		mCurClient = client;
		setName("TraceMethodThread---" + mType + "---");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			if (CommonUtil.strIsNull(mType)) {
				mType = Constants.TYPE_KPI; //Ĭ����kpiʱ��
			}
			
			ClientData.setMethodProfilingHandler(this);
			mCurClient.startMethodTracer();
			
			//Ĭ�ϲɼ�10s��method����
			Thread.sleep(10 * 1000);
			
			mCurClient.stopMethodTracer();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onSuccess(String remoteFilePath, Client client) {
		// TODO Auto-generated method stub
		LoggerManager.logError(LOGTAG, "onsuccess", "Method profiling: Older devices (API level < 10) are not supported yet. Please manually retrieve the file " +
	              remoteFilePath +
	              " from the device and open the file to view the results.");
	}

	@Override
	public void onSuccess(byte[] data, Client client) {
		// TODO Auto-generated method stub
		if (data != null) {
			SaveEnvironmentManager.getInstance().writeTraceToLocal(data, mType);
		}
	}

	@Override
	public void onStartFailure(Client client, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndFailure(Client client, String message) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
