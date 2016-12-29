package com.xdja.collectdata;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.xdja.adb.AdbManager;
import com.xdja.constant.GlobalConfig;
import com.xdja.util.CommonUtil;

/**
 * ��Adb������н����Ĺ�����
 * 
 * @author zlw
 *
 */
public class CollectDataUtil {

	public static final String COMMAND_SU = "su";
	public static final String COMMAND_SH = "sh";
	public static final String COMMAND_EXIT = "exit\n";
	public static final String COMMAND_LINE_END = "\n";
	private static IDevice device;
 //	private static DefaultHardwareDevice mDefaultHardwareDevice;
	
	private CollectDataUtil() {
		throw new AssertionError();
	}

	/**
	 * check whether has root permission
	 *
	 * @return
	 */
	public static boolean checkRootPermission() {
		return execCommand("echo root", true, false).result == 0;
	}

	/**
	 * execute shell command, default return result msg
	 *
	 * @param command
	 *            command
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(Object[], boolean, boolean)
	 */
	public static CommandResult execCommand(String command, boolean isRoot) {
		return execCommand(new String[] { command }, isRoot, true);
	}

	/**
	 * execute shell commands, default return result msg
	 *
	 * @param commands
	 *            command list
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(Object[], boolean, boolean)
	 */
	public static CommandResult execCommand(List commands, boolean isRoot) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, true);
	}

	/**
	 * execute shell commands, default return result msg
	 *
	 * @param commands
	 *            command array
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(Object[], boolean, boolean)
	 */
	public static CommandResult execCommand(String[] commands, boolean isRoot) {
		return execCommand(commands, isRoot, true);
	}

	/**
	 * execute shell command
	 *
	 * @param command
	 *            command
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return
	 * @see ShellUtils#execCommand(Object[], boolean, boolean)
	 */
	public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
		return execCommand(new String[] { command }, isRoot, isNeedResultMsg);
	}

	/**
	 * execute shell commands
	 *
	 * @param commands
	 *            command list
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return
	 * @see ShellUtils#execCommand(Object[], boolean, boolean)
	 */
	public static CommandResult execCommand(List commands, boolean isRoot, boolean isNeedResultMsg) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, isNeedResultMsg);
	}

	/**
	 * execute shell commands
	 *
	 * @param commands
	 *            command array
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return
	 *
	 * 		if isNeedResultMsg is false, {@link CommandResult#successMsg} is
	 *         null and {@link CommandResult#errorMsg} is null.
	 *
	 *         if {@link CommandResult#result} is -1, there maybe some
	 *         excepiton.
	 *
	 */
	public static CommandResult execCommand(Object[] commands, boolean isRoot, boolean isNeedResultMsg) {
		int result = -1;
		if (commands == null || commands.length == 0) {
			return new CommandResult(result, null, null);
		}

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = null;
		StringBuilder errorMsg = null;

		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
			os = new DataOutputStream(process.getOutputStream());
			for (Object command : commands) {
				String strCommand = String.valueOf(command);

				if (strCommand == null) {
					continue;
				}
				// donnot use os.writeBytes(strCommand), avoid chinese charset
				// error
				os.write(strCommand.getBytes());
				os.writeBytes(COMMAND_LINE_END);
				os.flush();
			}
			os.writeBytes(COMMAND_EXIT);
			os.flush();

			result = process.waitFor();
			// get command result
			if (isNeedResultMsg) {
				successMsg = new StringBuilder();
				errorMsg = new StringBuilder();
				successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
				// errorResult = new BufferedReader(new
				// InputStreamReader(process.getErrorStream()));
				String s;
				while ((s = successResult.readLine()) != null) {
					successMsg.append(s).append("\n");
				}
				// while ((s = errorResult.readLine()) != null) {
				// errorMsg.append(s).append("\n");
				// }
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (process != null) {
				process.destroy();
			}
		}
		return new CommandResult(result, successMsg == null ? null : successMsg.toString(),
				errorMsg == null ? null : errorMsg.toString());
	}

	/**
	 * execute shell commands
	 *
	 * @param commands
	 *            command array
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return
	 *
	 * 		if isNeedResultMsg is false, {@link CommandResult#successMsg} is
	 *         null and {@link CommandResult#errorMsg} is null.
	 *
	 *         if {@link CommandResult#result} is -1, there maybe some
	 *         excepiton.
	 *
	 */
	public static CommandResult execCmdCommand(String cmd, boolean isRoot, boolean isNeedResultMsg) {
		int result = -1;
		if (cmd == null || "" == cmd) {
			return new CommandResult(result, null, null);
		}

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = null;
		StringBuilder errorMsg = null;

		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("cmd /c " + cmd);
			// get command result
			if (isNeedResultMsg) {
				successMsg = new StringBuilder();
				errorMsg = new StringBuilder();
				successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
				errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String s;
				while ((s = successResult.readLine()) != null) {
					successMsg.append(s).append("\n");
				}
//				 ��ȡ���������ܻ���������waitFor����
				 while ((s = errorResult.readLine()) != null) {
				 errorMsg.append(s).append("\n");
				 }
			}
			WatchThread wt = new WatchThread(process);  
			wt.start();  
			result = process.waitFor();
			
//			ArrayList<String> commandStream = wt.getStream();
			wt.setOver(true); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (process != null) {
				process.destroy();
			}
		}

		return new CommandResult(result, successMsg == null ? null : successMsg.toString(),
				errorMsg == null ? null : errorMsg.toString());
	}

	/**
	 * �ܹ�ʵʱ�Ļ�ȡcmd�������Ϣ
	 * 
	 * @param cmd
	 * @param getDataListener
	 *            ��ȡ���ݵ�getDataListener
	 */
	public static void execCmdCommand(String cmd, GetDataInterface getDataListener) {

		BufferedReader br = null;
		try {

			Process proc = Runtime.getRuntime().exec(cmd);

			InputStream in = proc.getInputStream();

			br = new BufferedReader(new InputStreamReader(in, "GBK"));
			String line = null;

			while ((line = br.readLine()) != null) {
				if (getDataListener != null) {
					getDataListener.getString(line);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * ��device������
	 * @param dev
	 */
	public static void setDevice(IDevice dev){
		device = dev;
	}
	
	/**
	 * ��ִ��shell command��ͨ��ddmlib
	 * @param cmd
	 */
	public static CommandResult execShellCommand(String cmd){
		
		if (device == null) {
			device = AdbManager.getInstance().getIDevice(GlobalConfig.DeviceName);
		}
		if (CommonUtil.strIsNull(cmd)) {
			return new CommandResult(-1, "", "");
		}
		StringBuilder successMsg = new StringBuilder();
		
		// ����cmd
		cmd = cmd.substring(cmd.indexOf(" "));
		List<String> results = executeShellCommandWithOutput(device, cmd);
		if (results.size() > 1) {
			for(String str:results){
				successMsg.append(str).append("\n");
			}
			
			return new CommandResult(0, successMsg.toString(), "");
		}
		
		return new CommandResult(0, "", "");
	}
	
	/**
	 *  ͨ��ddmblibִ��cmd����
	 * @param device
	 * @param cmd
	 * @return
	 */
	public static List<String> executeShellCommandWithOutput(IDevice device, String cmd) {
		final List<String> results = new ArrayList<String>();
		try {
			device.executeShellCommand(cmd, new MultiLineReceiver() {

				@Override
				public void processNewLines(String[] lines) {
					for (String line : lines) {
						results.add(line);
					}
				}

				@Override
				public boolean isCancelled() {
					return false;
				}
			});
		} catch (Exception e) {
			System.out.println("executeShellCommandWithOutput error = " + e.getMessage());
		}
		return results;
	}
}

/**
 * ��ȡ���ݵļ���
 * 
 * @author zlw
 *
 */
interface GetDataInterface {
	public void getString(String content);

	public void getErrorString(String error);
}

