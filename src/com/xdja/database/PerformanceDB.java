package com.xdja.database;

import java.sql.*;
import java.util.List;

import com.xdja.collectdata.handleData.entity.BatteryHandleResult;
import com.xdja.collectdata.handleData.entity.CpuHandleResult;
import com.xdja.collectdata.handleData.entity.FlowHandleResult;
import com.xdja.collectdata.handleData.entity.FpsHandleResult;
import com.xdja.collectdata.handleData.entity.KpiHandleResult;
import com.xdja.collectdata.handleData.entity.MemoryHandleResult;
import com.xdja.constant.Constants;
import com.xdja.constant.GlobalConfig;
import com.xdja.log.LoggerManager;
import com.xdja.util.CommonUtil;

public class PerformanceDB {
	private static PerformanceDB mInstance = null;

	private Connection conn;
	private Statement stat;
	private ResultSet result;
	private String tableUrl = "jdbc:mysql://11.12.109.38:3306/performancedata";
	private String dbUrl = "jdbc:mysql://11.12.109.38:3306/";
	// private static String tableUrl =
	// "jdbc:mysql://localhost:3306/performanceData";
	// private static String dbUrl = "jdbc:mysql://localhost:3306/";
	private String driverClass = "com.mysql.jdbc.Driver";

	private final static String DBNAME = "performancedata";

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

	private String cpuTableName, cpuSlientTableName, memoryTableName, kpiTableName, fpsTableName, batteryTableName,
			flowTableName, flowSlientTableName, commonTableName;

	private PerformanceDB() {
		try {
			Class.forName(driverClass);
			// �������ݿ�
			createDb();
			// �����������
			conn = DriverManager.getConnection(tableUrl, GlobalConfig.DBUSERNAME, GlobalConfig.DBUSERPWD);
			stat = conn.createStatement();

			cpuTableName = Constants.CPU_TABLE;
			memoryTableName = Constants.MEMORY_TABLE;
			kpiTableName = Constants.KPI_TABLE;
			fpsTableName = Constants.FPS_TABLE;
			batteryTableName = Constants.BATTERY_TABLE;
			flowTableName = Constants.FLOW_TABLE;
			cpuSlientTableName = Constants.SLIENT_CPU_TABLE;
			flowSlientTableName = Constants.SLIENT_FLOW_TABLE;
			commonTableName = Constants.COMMON_TABLE;
			// �������ݱ�
			createTables();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �ر����ݿ��һЩ��Դ �����в������֮�����
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

			mInstance = null;
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
			String sql = "create database if not exists " + DBNAME;
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LoggerManager.logError(PerformanceDB.class.getSimpleName(), "createDb", "���ݿ��Ѿ����ڣ�" + DBNAME);
			return;
		} finally {
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
	 * �������ݱ�������Ҫ�Զ��������չ�ֶεĳ���
	 * 
	 * @param tableName
	 * @param sql
	 * @throws SQLException
	 */
	private void createTable(String sql) throws SQLException {
		if (CommonUtil.strIsNull(sql)) {
			return;
		}
		stat.executeUpdate(sql);
	}

	/**
	 * �������е����ݱ�
	 * 
	 * @throws SQLException
	 */
	public void createTables() throws SQLException {
		String createMemorySql = "CREATE TABLE IF NOT EXISTS `" + memoryTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),package varchar(160),version varchar(160),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50),hprofPath varchar(50), isPass int, PRIMARY KEY(`id`))";
		String createCpuSql = "CREATE TABLE IF NOT EXISTS `" + cpuTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),package varchar(160),version varchar(160),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50), isPass int, PRIMARY KEY(`id`))";
		String createKpiSql = "CREATE TABLE IF NOT EXISTS `" + kpiTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),package varchar(160),version varchar(160),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50), isPass int, PRIMARY KEY(`id`))";
		String createFlowSql = "CREATE TABLE IF NOT EXISTS `" + flowTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),package varchar(160),version varchar(160),testvalue varchar(50), logPath varchar(50), isPass int, PRIMARY KEY(`id`))";
		String createFpsSql = "CREATE TABLE IF NOT EXISTS `" + fpsTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),package varchar(160),version varchar(160),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50),hprofPath varchar(50), isPass int, PRIMARY KEY(`id`))";
		String createBatterySql = "CREATE TABLE IF NOT EXISTS `" + batteryTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, uid int(11),testvalue varchar(50),  detailInfo varchar(256), package varchar(160), version varchar(160), PRIMARY KEY(`id`))";
		String createSlientCpuSql = "CREATE TABLE IF NOT EXISTS `" + cpuSlientTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),package varchar(160),version varchar(160),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50), isPass int, PRIMARY KEY(`id`))";
		String createSlientFlowSql = "CREATE TABLE IF NOT EXISTS `" + flowSlientTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),package varchar(160),version varchar(160),testvalue varchar(50), logPath varchar(50), isPass int, PRIMARY KEY(`id`))";
		String createCommonDataSql = "CREATE TABLE IF NOT EXISTS `" + commonTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, package varchar(160), version varchar(160), PRIMARY KEY(`id`))";

		// �����������ݵı�
		createTable(createCommonDataSql);
		// ����CPU���ݱ�
		createTable(createMemorySql);
		// ����Memory���ݱ�
		createTable(createCpuSql);
		// ����KPI���ݱ�
		createTable(createKpiSql);
		// ����Flow���ݱ�
		createTable(createFlowSql);
		// ����FPS���ݱ�
		createTable(createFpsSql);
		// �����������ݱ�
		createTable(createBatterySql);
		// ������Ĭcpu���ݱ�
		createTable(createSlientCpuSql);
		// ������Ĭ�������ݱ�
		createTable(createSlientFlowSql);
	}

	/**
	 * �õ���ʽ��������ݱ����֣� ��ʽ�ǣ� testType#packageName#version
	 * 
	 * @param packageName
	 * @param version
	 * @param testType
	 * @return
	 */
	@SuppressWarnings("unused")
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
	public void insertCpuData(List<CpuHandleResult> handleDataList) {
		if (handleDataList == null || handleDataList.size() < 1) {
			return;
		}

		if (conn == null) {
			return;
		}
		String insertSql = "insert into `" + cpuTableName
				+ "`(page,package,version ,testvalue, logPath, methodTracePath, isPass) values (?,?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (CpuHandleResult result : handleDataList) {
				psts.setString(1, result.activityName);
				psts.setString(2, result.packageName); // ����package
				psts.setString(3, result.version); // ����version
				psts.setFloat(4, Float.valueOf(result.testValue));
				psts.setString(5, result.logPath);
				psts.setString(6, result.methodTracePath);
				// �������ݣ�ֵ��1���쳣���ݣ�ֵ��0
				psts.setInt(7, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // ִ����������
			conn.commit(); // �ύ
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��Cpu���ݲ��뵽���ݱ���
	 * 
	 * @param handleDataList
	 */
	public void insertSlientCpuData(List<CpuHandleResult> handleDataList) {
		if (handleDataList == null || handleDataList.size() < 1) {
			return;
		}

		if (conn == null) {
			return;
		}

		String insertSql = "insert into `" + cpuSlientTableName
				+ "`(page,package,version, testvalue, logPath, methodTracePath, isPass) values (?,?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (CpuHandleResult result : handleDataList) {
				psts.setString(1, result.activityName);
				psts.setString(2, result.packageName); // ����package
				psts.setString(3, result.version); // ����version
				psts.setFloat(4, Float.valueOf(result.testValue));
				psts.setString(5, result.logPath);
				psts.setString(6, result.methodTracePath);
				// �������ݣ�ֵ��1���쳣���ݣ�ֵ��0
				psts.setInt(7, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // ִ����������
			conn.commit(); // �ύ
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��kpi�����ݽ���浽���ݿ���
	 * 
	 * @param handleKpiList
	 */
	public void insertKpiData(List<KpiHandleResult> handleKpiList) {
		if (handleKpiList == null || handleKpiList.size() < 1) {
			return;
		}

		if (conn == null) {
			return;
		}

		String insertSql = "insert into `" + kpiTableName
				+ "`(page,package,version, testvalue, logPath, methodTracePath, isPass) values (?,?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (KpiHandleResult result : handleKpiList) {
				psts.setString(1, result.activityName);
				psts.setString(2, result.packageName); // ����package
				psts.setString(3, result.version); // ����version
				psts.setFloat(4, Float.valueOf(result.testValue));
				psts.setString(5, result.logPath);
				psts.setString(6, result.methodTracePath);
				// �������ݣ�ֵ��1���쳣���ݣ�ֵ��0
				psts.setInt(7, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // ִ����������
			conn.commit(); // �ύ
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��Cpu���ݲ��뵽���ݱ���
	 * 
	 * @param handleDataList
	 */
	public void insertMemoryData(List<MemoryHandleResult> handleDataList) {
		if (handleDataList == null || handleDataList.size() < 1) {
			return;
		}
		if (conn == null) {
			return;
		}

		String insertSql = "insert into `" + memoryTableName
				+ "`(page,package,version, testvalue, logPath, methodTracePath, hprofPath, isPass) values (?,?,?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (MemoryHandleResult result : handleDataList) {
				psts.setString(1, result.activityName);
				psts.setString(2, result.packageName); // ����package
				psts.setString(3, result.version); // ����version
				psts.setFloat(4, Float.valueOf(result.testValue));
				psts.setString(5, result.logPath);
				psts.setString(6, result.methodTracePath);
				psts.setString(7, result.memoryHprofPath);
				// �������ݣ�ֵ��1���쳣���ݣ�ֵ��0
				psts.setInt(8, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // ִ����������
			conn.commit(); // �ύ
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��Flow���ݲ��뵽���ݿ���
	 * 
	 * @param handleFlowList
	 */
	public void insertFlowData(List<FlowHandleResult> handleFlowList) {
		if (handleFlowList == null || handleFlowList.size() < 1) {
			return;
		}

		if (conn == null) {
			return;
		}

		String insertSql = "insert into `" + flowTableName
				+ "`(page,package,version, testvalue, logPath, isPass) values (?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (FlowHandleResult result : handleFlowList) {
				psts.setString(1, result.activityName);
				psts.setString(2, result.packageName); // ����package
				psts.setString(3, result.version); // ����version
				psts.setFloat(4, Float.valueOf(result.testValue));
				psts.setString(5, result.logPath);
				// �������ݣ�ֵ��1���쳣���ݣ�ֵ��0
				psts.setInt(6, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // ִ����������
			conn.commit(); // �ύ
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��Flow���ݲ��뵽���ݿ���
	 * 
	 * @param handleFlowList
	 */
	public void insertSlientFlowData(List<FlowHandleResult> handleFlowList) {
		if (handleFlowList == null || handleFlowList.size() < 1) {
			return;
		}
		if (conn == null) {
			return;
		}

		String insertSql = "insert into `" + flowSlientTableName
				+ "`(page,package,version, testvalue, logPath, isPass) values (?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (FlowHandleResult result : handleFlowList) {
				psts.setString(1, result.activityName);
				psts.setString(2, result.packageName); // ����package
				psts.setString(3, result.version); // ����version
				psts.setFloat(4, Float.valueOf(result.testValue));
				psts.setString(5, result.logPath);
				// �������ݣ�ֵ��1���쳣���ݣ�ֵ��0
				psts.setInt(6, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // ִ����������
			conn.commit(); // �ύ
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��Fps�����ݲ��뵽���ݿ���
	 * 
	 * @param handleFpsList
	 */
	public void insertFpsData(List<FpsHandleResult> handleFpsList) {
		if (handleFpsList == null || handleFpsList.size() < 1) {
			return;
		}
		if (conn == null) {
			return;
		}

		String insertSql = "insert into `" + fpsTableName
				+ "`(page,package,version, testvalue, logPath, methodTracePath, hprofPath, isPass) values (?,?,?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (FpsHandleResult result : handleFpsList) {
				psts.setString(1, result.activityName);
				psts.setString(2, result.packageName); // ����package
				psts.setString(3, result.version); // ����version
				psts.setFloat(4, Float.valueOf(result.testValue));
				psts.setString(5, result.logPath);
				psts.setString(6, result.methodTracePath);
				psts.setString(7, result.memoryHprofPath);
				// �������ݣ�ֵ��1���쳣���ݣ�ֵ��0
				psts.setInt(8, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // ִ����������
			conn.commit(); // �ύ
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ���������ݲ��뵽���ݿ���
	 * 
	 * @param handleBatteryList
	 */
	public void insertBatteryData(List<BatteryHandleResult> handleBatteryList) {
		if (handleBatteryList == null || handleBatteryList.size() < 1) {
			return;
		}
		if (conn == null) {
			return;
		}

		String insertSql = "insert into `" + batteryTableName + "`(uid, testvalue, detailInfo, package, version) values (?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (BatteryHandleResult result : handleBatteryList) {
				psts.setString(1, result.uid);
				psts.setFloat(2, Float.valueOf(result.testValue));
				psts.setString(3, result.detailInfo);
				psts.setString(4, result.packageName);
				psts.setString(5, result.version);
				psts.addBatch();
			}

			psts.executeBatch(); // ִ����������
			conn.commit(); // �ύ
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  ���������ݱ�������
	 * @param packageName
	 * @param version
	 */
	public void saveCommonData(String packageName, String version) {
		if (CommonUtil.strIsNull(packageName) || CommonUtil.strIsNull(version)) {
			return;
		}
		
		if (conn == null) {
			return;
		}
		PreparedStatement psts = null;
		String searchSql = "select * from `" + commonTableName + "` where package = '" + packageName + "' and version='" + version+"'";
		String insertSql = "insert into `" + commonTableName + "`(package, version) values (?,?)";
		try {
			psts = conn.prepareStatement(insertSql);
			
			ResultSet resultSet = psts.executeQuery(searchSql);
			if (resultSet != null && resultSet.next()) {
				return;
			}
			psts.setString(1, packageName);
			psts.setString(2, version);
			psts.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (psts != null) {
				try {
					psts.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
