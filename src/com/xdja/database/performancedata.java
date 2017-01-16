package com.xdja.database;

import java.awt.List;
import java.sql.*;
import java.util.ArrayList;  

public class performancedata {
	public static Connection conn;
	public static Statement stat ;
	public static ResultSet result ;
	static String url ;
	String username ;
	String password;
	
	private performancedata(String url,String username,String password){
		performancedata.url=url;
		this.username=username;
		this.password=password;
	}
	
	
	
	
	public performancedata() {
		// TODO Auto-generated constructor stub
	}




	public  void conperformance(String test) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");  // ע�� JDBC ����
		//һ��ʼ������һ���Ѿ����ڵ����ݿ�  
        String url = "jdbc:mysql://localhost:3306/"+test+"?useUnicode=true&characterEncoding=utf-8";     
        conn = DriverManager.getConnection(url, "root", "");  //����Ϊ��
        stat = conn.createStatement();  
	
	}
	
	//�������ݿ�performance//
	public void creatperformance(Statement stat,Connection conn) throws SQLException{
		//�������ݿ�performance 
		try {
			String url = "jdbc:mysql://localhost:3306/performance?useUnicode=true&characterEncoding=utf-8";
		    Connection conn1= DriverManager.getConnection(url, "root", "");
		    conn1.close();
		   } catch (SQLException e) {
//		    e.printStackTrace();
		    stat.executeUpdate("create database performance");
		   } 
		
        stat.close();  
        conn.close(); 
    }
	
	//����CPU���ݱ�//
	public void CPUteble(Statement stat,String table1,String pkg ,String version,String projectname){
		 String checkTable="show tables like \'"+table1+"'";  
		try {
			ResultSet resultSet=stat.executeQuery(checkTable);
			if (resultSet.next()) {  
	            System.out.println("table exist!");  
	        }else{ 
	        String pkg1 = pkg.replace(".", "_");
	        String version1 = version.replace(".", "_");
	        String firstid = projectname+pkg1+version1;
			stat.executeUpdate("create table "+table1+"("+firstid+" double, Activity varchar(80),abnormal varchar(20),screenshot varchar(50),logcat varchar(50),traceview varchar(50),hprof varchar(50))");
		} resultSet.close();
			}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}

	//��������CPU���ݱ������//
	private void insertCPUteble(Statement stat,String table1,double d,String Activity ,String result,String  screenshotpath, String logpath,String traceviewpath, String hprofpath){
		try {
		stat.executeUpdate("insert into "+table1+" values('"+d +"', '"+Activity+"','"+result+"','"+screenshotpath+"', '"+logpath+"','"+traceviewpath+"','"+hprofpath+"')");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	//�����ݱ��в������ݣ��������Ϊ�б����ͣ�������������//
		public void insertDatas(Statement stat,ArrayList<ArrayList<String>> d,String table1) throws SQLException{
			String insert_sql = "INSERT INTO tb_ipinfos (CPU, Activity, abnormal,screenshot,logcat,traceview,hprof) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement psts = conn.prepareStatement(insert_sql);
			if (d!=null){
				int i = d.size();
				for (int j =0; j<i ;j++){
					try {
						psts.setString(1, d.get(j).get(0));
						psts.setString(2, d.get(j).get(1));
						psts.setString(3, d.get(j).get(2));
						psts.setString(4, d.get(j).get(3));
						psts.setString(5, d.get(j).get(4));
						psts.setString(6, d.get(j).get(5));
						psts.setString(7, d.get(j).get(6));
						psts.addBatch(); 
			
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				psts.executeBatch(); // ִ����������  
		        conn.commit();  // �ύ  
			}
		}
		
		
		
	
	//�����ر����ݿ�ķ���//
	public void closeperformance(Connection conn,Statement stat,ResultSet result){
		try {
			if(result !=null){
			  result.close();	
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  //�ر��α�
        try {
        	if (stat !=null){
        	stat.close();	
        	}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    //�ر����ݿ��������
        try {
        	if (conn !=null){
        		conn.close();	
            }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   //�ر����ݿ�������Ӷ���	
	}
	
	static void CPUtestfordatabase () throws ClassNotFoundException, SQLException{
		performancedata perfor = new performancedata();//�������ݿ���perfor
		perfor.conperformance("test"); //�������ݿ�test
		perfor.creatperformance(performancedata.stat,performancedata.conn);//�������ݿ�performance
		perfor.conperformance("performance"); //�������ݿ�performance
		perfor.CPUteble(performancedata.stat,"cputable","com.xdja.actoma","V3.3056.1","CPU");//�������ݱ�cputable
		perfor.insertCPUteble(performancedata.stat,"cputable", 50.0,  "com.xdja.actoma", "true" , "D:/log", "D:/log", "D:/log", "D:/log");//���뵥��ģ������
		perfor.closeperformance(performancedata.conn,performancedata.stat,performancedata.result);//�ر����ݿ�����
    }
	
	
	public static void main(String[] args) throws Exception  
    {   
		CPUtestfordatabase();
    }
	
	
}
