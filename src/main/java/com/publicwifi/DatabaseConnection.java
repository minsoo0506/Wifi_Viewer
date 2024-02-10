package com.publicwifi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// SQLite 데이터베이스 연결을 위한 클래스
public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        try {
            // SQLite JDBC 드라이버를 로드
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // SQLite 데이터베이스 파일 경로
        String url = "jdbc:sqlite:/Users/mnsoo/Desktop/zerobase/Wifi_Viewer/wifi.db";
        // SQLite 데이터베이스 연결
        return DriverManager.getConnection(url);
    }
}