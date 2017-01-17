package com.xdja.database;

import java.sql.*;
import java.util.List;

import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.entity.BaseTestInfo;
import com.xdja.collectdata.handleData.HandleDataResult;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.log.LoggerManager;
import com.xdja.util.CommonUtil;

public class PerformanceDB {
	private static PerformanceDB mInstance = null;
	private static Connection conn;
	private static Statement stat;
	private static ResultSet result;
	private static String tableUrl = "jdbc:mysql://localhost:3306/performanceData";
	private static String dbUrl = "jdbc:mysql://localhost:3306/";
	private static String driverClass = "com.mysql.jdbc.Driver";
	private final static String DBNAME = "performanceData";
	public static PerformanceDB getInstance() {
		if (mInstance == null) {
			synchronized (PerformanceDB.class) {
				if (mInstance == null) {
					mInstance = new PerformanceDB();
				}
			}
		}

		return mInstance;
	}
	
	private String cpuTableName, memoryTableName, kpiTableName, fpsTableName, batteryTableName, flowTableName;

	private PerformanceDB() {
		try {
			Class.forName(driverClass);
			// �������ݿ�
			createDb();
			// �����������
			conn = DriverManager.getConnection(tableUrl, GlobalConfig.DBUSERNAME, GlobalConfig.DBUSERPWD);
			stat = conn.createStatement();

			// ��ȡpackageName��version
			BaseTestInfo baseTestInfo = CollectDataImpl.getBaseTestInfo();
			if (baseTestInfo == null) {
				return;
			}
			String packageName = baseTestInfo.packageName;
			String version = baseTestInfo.versionName;
			// ƴ�ӱ������
			cpuTableName = getFormatDbName(packageName, version, Constants.TYPE_CPU);
			memoryTableName = getFormatDbName(packageName, version, Constants.TYPE_MEMORY);
			kpiTableName = getFormatDbName(packageName, version, Constants.TYPE_KPI);
			fpsTableName = getFormatDbName(packageName, version, Constants.TYPE_FPS);
			batteryTableName = getFormatDbName(packageName, version, Constants.TYPE_BATTERY);
			flowTableName = getFormatDbName(packageName, version, Constants.TYPE_FLOW);
			
			//�������ݱ�
			createTables();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �ر����ݿ��һЩ��Դ
	 * �����в������֮�����
	 */
	public void closeDB() {
		try {
			if (result != null) {
				result.close();
			}
			
			if (stat != null) {
				stat.close();
			}
			
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/**
	 * �������ݿ�
	 */
	private void createDb() {
		Connection connection = null;
		Statement stat = null;
		try {
			connection = DriverManager.getConnection(dbUrl, GlobalConfig.DBUSERNAME, GlobalConfig.DBUSERPWD);
			stat = connection.createStatement();
			String sql = "create database if not exists" + DBNAME;
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LoggerManager.logError(PerformanceDB.class.getSimpleName(), "createDb", "���ݿ��Ѿ����ڣ�" + DBNAME);
			return;
		}finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * �������ݱ� �����ڹ����ֶ�
	 * @throws SQLException 
	 */
	private void createTable(String tableName) throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "`("
					 + "id int(11) not null AUTO_INCREMENT, page varchar(80),testvalue varchar(50), screenshotPath varchar(50),logPath varchar(50),methodTracePath varchar(50),hprofPath varchar(50), pass int, PRIMARY KEY(`id`))";
		stat.executeUpdate(sql);
	}

	/**
	 * �������ݱ�������Ҫ�Զ��������չ�ֶεĳ���
	 * 
	 * @param tableName
	 * @param sql
	 * @throws SQLException 
	 */
	@SuppressWarnings("unused")
	private void createTable(String tableName, String sql) throws SQLException {
		if (CommonUtil.strIsNull(sql) || CommonUtil.strIsNull(tableName)) {
			return;
		}
		stat.executeUpdate(sql);
	}

	/**
	 * �������е����ݱ�
	 * @throws SQLException 
	 */
	public void createTables() throws SQLException {

		// ����CPU���ݱ�
		createTable(cpuTableName);
		// ����Memory���ݱ�
		createTable(memoryTableName);
	}

	/**
	 * �õ���ʽ��������ݱ����֣� ��ʽ�ǣ� testType#packageName#version
	 * 
	 * @param packageName
	 * @param version
	 * @param testType
	 * @return
	 */
	private String getFormatDbName(String packageName, String version, String testType) {
		String pkg1 = packageName.replace(".", "_");
		String version1 = version.replace(".", "_");
		StringBuilder sbBuilder = new StringBuilder(testType);
		sbBuilder.append("#");
		sbBuilder.append(pkg1).append("#");
		sbBuilder.append(version1);

		return sbBuilder.toString();
	}

	/**
	 * ��Cpu���ݲ��뵽���ݱ���
	 * 
	 * @param handleDataList
	 */
	public void insertCpuData(List<HandleDataResult> handleDataList) {
		if (handleDataList == null || handleDataList.size() < 1) {
			return;
		}
		String insertSql = "insert into `" + cpuTableName
				+ "`(page, testvalue, screenshotPath, logPath, methodTracePath, hprofPath, pass) values (?,?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (HandleDataResult result : handleDataList) {
				psts.setString(1, result.activityName);
				psts.setFloat(2, Float.valueOf(result.testValue));
				psts.setString(3, result.screenshotsPath);
				psts.setString(4, result.logPath);
				psts.setString(5, result.methodTracePath);
				psts.setString(6, result.memoryTracePath);
				// �������ݣ�ֵ��1���쳣���ݣ�ֵ��0
				psts.setInt(7, result.result ? 1 : 0);
				psts.addBatch();
			}
			
			psts.executeBatch(); // ִ����������  
	        conn.commit();  // �ύ 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
