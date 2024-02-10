package com.publicwifi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:sqlite:/Users/mnsoo/Desktop/zerobase/Wifi_Viewer/wifi.db";
        return DriverManager.getConnection(url);
    }
}