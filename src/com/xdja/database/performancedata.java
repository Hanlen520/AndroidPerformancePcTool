package com.xdja.database;

import java.awt.List;
import java.sql.*;
import java.util.ArrayList;  

public class performancedata {
	static Connection conn;
	static Statement stat ;
	static ResultSet result ;
	static String url ;
	String username ;
	String password;
	
	private performancedata(String url,String username,String password){
		performancedata.url=url;
		this.username=username;
		this.password=password;
	}
	
	private performancedata(){
		
	}
	
	
	//�����������ݿ�//
	private  void conperformance(String test) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");  // ע�� JDBC ����
		//һ��ʼ������һ���Ѿ����ڵ����ݿ�  
        String url = "jdbc:mysql://localhost:3306/"+test+"?useUnicode=true&characterEncoding=utf-8";     
        conn = DriverManager.getConnection(url, "root", "");  //����Ϊ��
        stat = conn.createStatement();  
	
	}
	
	//�������ݿ�performance//
	private void creatperformance(Statement stat,Connection conn) throws SQLException{
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
	private void CPUteble(Statement stat,String table1){
		 String checkTable="show tables like \'"+table1+"'";  
		try {
			ResultSet resultSet=stat.executeQuery(checkTable);
			if (resultSet.next()) {  
	            System.out.println("table exist!");  
	        }else{ 
			stat.executeUpdate("create table "+table1+"(CPU double, Activity varchar(80), package varchar(80),version varchar(50),abnormal varchar(20),screenshot varchar(50),logcat varchar(50),traceview varchar(50),hprof varchar(50))");
		} resultSet.close();
			}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}

	//��������CPU���ݱ������//
	private void insertCPUteble(Statement stat,String table1,double d,String Activity, String pkg,String version ,String result,String  screenshotpath, String logpath,String traceviewpath, String hprofpath){
		try {
		stat.executeUpdate("insert into "+table1+" values('"+d +"', '"+Activity+"','"+pkg+"','"+version+"','"+result+"','"+screenshotpath+"', '"+logpath+"','"+traceviewpath+"','"+hprofpath+"')");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//�����ݱ��в������ݣ��������Ϊ�б�����//
	private void insertdata(Statement stat,ArrayList<ArrayList<String>> d,String table1){
		if (d!=null){
			int i = d.size();
			for (int j =0; j<i ;j++){
				try {
				stat.executeUpdate("insert into "+table1+" values('"+d.get(0) +"', '"+d.get(1)+"','"+d.get(2)+"','"+d.get(3)+"','"+d.get(4)+"','"+d.get(5)+"', '"+d.get(6)+"','"+d.get(7)+"','"+d.get(8)+"')");
		
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	//�����ر����ݿ�ķ���//
	private void closeperformance(Connection conn,Statement stat,ResultSet result){
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
		performancedata perfor = new performancedata();
		perfor.conperformance("test"); 
		perfor.creatperformance(performancedata.stat,performancedata.conn);
		perfor.conperformance("performance"); 
		perfor.CPUteble(performancedata.stat,"cputable");
		perfor.insertCPUteble(performancedata.stat,"cputable", 50.0, "xdja.actoma", "com.xdja.actoma", "2.3.3.3", "true" , "D:/log", "D:/log", "D:/log", "D:/log");
		perfor.closeperformance(performancedata.conn,performancedata.stat,performancedata.result);
    }
	
	
	public static void main(String[] args) throws Exception  
    {   
		CPUtestfordatabase ();
    }
	
	
}
