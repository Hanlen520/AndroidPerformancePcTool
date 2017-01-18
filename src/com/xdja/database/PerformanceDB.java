package com.xdja.database;

import java.sql.*;
import java.util.List;

import com.xdja.collectdata.CollectDataImpl;
import com.xdja.collectdata.entity.BaseTestInfo;
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
	private static Connection conn;
	private static Statement stat;
	private static ResultSet result;
	private static String tableUrl = "jdbc:mysql://11.12.109.38:3306/performanceData";
	private static String dbUrl = "jdbc:mysql://11.12.109.38:3306/";
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
			// 创建数据库
			createDb();
			// 创建表的连接
			conn = DriverManager.getConnection(tableUrl, GlobalConfig.DBUSERNAME, GlobalConfig.DBUSERPWD);
			stat = conn.createStatement();

			// 获取packageName和version
			BaseTestInfo baseTestInfo = CollectDataImpl.getBaseTestInfo();
			if (baseTestInfo == null) {
				return;
			}
			String packageName = baseTestInfo.packageName;
			String version = baseTestInfo.versionName;
			// 拼接表的名称
			cpuTableName = getFormatDbName(packageName, version, Constants.TYPE_CPU);
			memoryTableName = getFormatDbName(packageName, version, Constants.TYPE_MEMORY);
			kpiTableName = getFormatDbName(packageName, version, Constants.TYPE_KPI);
			fpsTableName = getFormatDbName(packageName, version, Constants.TYPE_FPS);
			batteryTableName = getFormatDbName(packageName, version, Constants.TYPE_BATTERY);
			flowTableName = getFormatDbName(packageName, version, Constants.TYPE_FLOW);

			// 创建数据表
			createTables();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 关闭数据库的一些资源 在所有操作完成之后调用
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
	 * 创建数据库
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
			LoggerManager.logError(PerformanceDB.class.getSimpleName(), "createDb", "数据库已经存在：" + DBNAME);
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
	 * 创建数据表，适用需要自定义或者扩展字段的场景
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
	 * 创建数据表 适用于公共字段
	 * 
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private void createTable(String tableName, String createSql) throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),testvalue varchar(50), screenshotPath varchar(50),logPath varchar(50),methodTracePath varchar(50),hprofPath varchar(50), pass int, PRIMARY KEY(`id`))";
		stat.executeUpdate(sql);
	}

	/**
	 * 创建所有的数据表
	 * 
	 * @throws SQLException
	 */
	public void createTables() throws SQLException {
		String createMemorySql = "CREATE TABLE IF NOT EXISTS `" + memoryTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50),hprofPath varchar(50), pass int, PRIMARY KEY(`id`))";
		String createCpuSql = "CREATE TABLE IF NOT EXISTS `" + cpuTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50), pass int, PRIMARY KEY(`id`))";
		String createKpiSql = "CREATE TABLE IF NOT EXISTS `" + kpiTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50), pass int, PRIMARY KEY(`id`))";
		String createFlowSql = "CREATE TABLE IF NOT EXISTS `" + flowTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),testvalue varchar(50), logPath varchar(50), pass int, PRIMARY KEY(`id`))";
		String createFpsSql = "CREATE TABLE IF NOT EXISTS `" + fpsTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, page varchar(80),testvalue varchar(50), logPath varchar(50),methodTracePath varchar(50),hprofPath varchar(50), pass int, PRIMARY KEY(`id`))";
		String createBatterySql = "CREATE TABLE IF NOT EXISTS `" + batteryTableName + "`("
				+ "id int(11) not null AUTO_INCREMENT, uid int(11),testvalue varchar(50),  detailInfo varchar(256),PRIMARY KEY(`id`))";

		// 创建CPU数据表
		createTable(createMemorySql);
		// 创建Memory数据表
		createTable(createCpuSql);
		// 创建KPI数据表
		createTable(createKpiSql);
		// 创建Flow数据表
		createTable(createFlowSql);
		// 创建FPS数据表
		createTable(createFpsSql);
		// 创建电量数据表
		createTable(createBatterySql);
	}

	/**
	 * 得到格式化后的数据表名字， 格式是： testType#packageName#version
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
	 * 将Cpu数据插入到数据表中
	 * 
	 * @param handleDataList
	 */
	public void insertCpuData(List<CpuHandleResult> handleDataList) {
		if (handleDataList == null || handleDataList.size() < 1) {
			return;
		}

		String insertSql = "insert into `" + cpuTableName
				+ "`(page, testvalue, logPath, methodTracePath, pass) values (?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (CpuHandleResult result : handleDataList) {
				psts.setString(1, result.activityName);
				psts.setFloat(2, Float.valueOf(result.testValue));
				psts.setString(3, result.logPath);
				psts.setString(4, result.methodTracePath);
				// 正常数据，值是1，异常数据，值是0
				psts.setInt(5, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // 执行批量处理
			conn.commit(); // 提交
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将kpi的数据结果存到数据库中
	 * 
	 * @param handleKpiList
	 */
	public void insertKpiData(List<KpiHandleResult> handleKpiList) {
		if (handleKpiList == null || handleKpiList.size() < 1) {
			return;
		}

		String insertSql = "insert into `" + kpiTableName
				+ "`(page, testvalue, logPath, methodTracePath, pass) values (?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (KpiHandleResult result : handleKpiList) {
				psts.setString(1, result.activityName);
				psts.setFloat(2, Float.valueOf(result.testValue));
				psts.setString(3, result.logPath);
				psts.setString(4, result.methodTracePath);
				// 正常数据，值是1，异常数据，值是0
				psts.setInt(5, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // 执行批量处理
			conn.commit(); // 提交
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将Cpu数据插入到数据表中
	 * 
	 * @param handleDataList
	 */
	public void insertMemoryData(List<MemoryHandleResult> handleDataList) {
		if (handleDataList == null || handleDataList.size() < 1) {
			return;
		}
		String insertSql = "insert into `" + memoryTableName
				+ "`(page, testvalue, logPath, methodTracePath, hprofPath, pass) values (?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (MemoryHandleResult result : handleDataList) {
				psts.setString(1, result.activityName);
				psts.setFloat(2, Float.valueOf(result.testValue));
				psts.setString(3, result.logPath);
				psts.setString(4, result.methodTracePath);
				psts.setString(5, result.memoryHprofPath);
				// 正常数据，值是1，异常数据，值是0
				psts.setInt(6, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // 执行批量处理
			conn.commit(); // 提交
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 将Flow数据插入到数据库中
	 * @param handleFlowList
	 */
	public void insertFlowData(List<FlowHandleResult> handleFlowList){
		if (handleFlowList == null || handleFlowList.size() < 1) {
			return;
		}
		String insertSql = "insert into `" + memoryTableName
				+ "`(page, testvalue, logPath, pass) values (?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (FlowHandleResult result : handleFlowList) {
				psts.setString(1, result.activityName);
				psts.setFloat(2, Float.valueOf(result.testValue));
				psts.setString(3, result.logPath);
				// 正常数据，值是1，异常数据，值是0
				psts.setInt(4, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // 执行批量处理
			conn.commit(); // 提交
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *  将Fps的数据插入到数据库中
	 * @param handleFpsList
	 */
	public void insertFpsData(List<FpsHandleResult> handleFpsList){
		if (handleFpsList == null || handleFpsList.size() < 1) {
			return;
		}
		String insertSql = "insert into `" + fpsTableName
				+ "`(page, testvalue, logPath, methodTracePath, hprofPath, pass) values (?,?,?,?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (FpsHandleResult result : handleFpsList) {
				psts.setString(1, result.activityName);
				psts.setFloat(2, Float.valueOf(result.testValue));
				psts.setString(3, result.logPath);
				psts.setString(4, result.methodTracePath);
				psts.setString(5, result.memoryHprofPath);
				// 正常数据，值是1，异常数据，值是0
				psts.setInt(6, result.result ? 1 : 0);
				psts.addBatch();
			}

			psts.executeBatch(); // 执行批量处理
			conn.commit(); // 提交
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 将电量数据插入到数据库中
	 * @param handleBatteryList
	 */
	public void insertBatteryData(List<BatteryHandleResult> handleBatteryList){
		if(handleBatteryList == null || handleBatteryList.size() < 1){
			return;
		}
		
		String insertSql = "insert into `" + memoryTableName
				+ "`(uid, testvalue, detailInfo) values (?,?,?)";
		PreparedStatement psts = null;
		try {
			conn.setAutoCommit(false);
			psts = conn.prepareStatement(insertSql);
			for (BatteryHandleResult result : handleBatteryList) {
				psts.setString(1, result.uid);
				psts.setFloat(2, Float.valueOf(result.testValue));
				psts.setString(3, result.detailInfo);
				psts.addBatch();
			}

			psts.executeBatch(); // 执行批量处理
			conn.commit(); // 提交
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
