package com.publicwifi;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/history")
public class LocationHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            handleGetLocationInfoRequest(req, resp);
        } catch (SQLException e){
            throw new ServletException("Cannot connect to the database", e);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            handlePostLocationInfoRequest(req, resp);
        } catch (SQLException e){
            throw new ServletException("Cannot connect to the database", e);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            handleDeleteLocationInfoRequest(req, resp);
        } catch (SQLException e){
            throw new ServletException("Cannot connect to the database", e);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void handleGetLocationInfoRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try(Connection dbConn = DatabaseConnection.getConnection()){
            String sql = "SELECT * FROM location_info";
            PreparedStatement pstmt = dbConn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            List<LocationInfo> locationInfos = new ArrayList<>();
            while(rs.next()){
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setID(rs.getInt("Location_ID"));
                locationInfo.setLAT(rs.getDouble("LAT"));
                locationInfo.setLNG(rs.getDouble("LNG"));
                locationInfo.setDTTM(rs.getString("Search_DTTM"));
                locationInfos.add(locationInfo);
            }

            System.out.println("Size of locationInfos: " + locationInfos.size());
            for (LocationInfo locationInfo : locationInfos) {
                System.out.println(locationInfo);
            }

            request.setAttribute("locationInfos", locationInfos);
            request.getRequestDispatcher("/locationHistory.jsp").forward(request, response);
        }
    }

    public void handlePostLocationInfoRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try(Connection dbConn = DatabaseConnection.getConnection()){
            Statement stmt = dbConn.createStatement();

            String createTableSql = "CREATE TABLE IF NOT EXISTS location_info (" +
                "Location_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "LAT REAL," +
                "LNG REAL," +
                "Search_DTTM TEXT" +
                ")";
            stmt.execute(createTableSql);

            String insertSql = "INSERT INTO location_info (LAT, LNG, Search_DTTM) VALUES (?, ?, ?)";
            PreparedStatement pstmt = dbConn.prepareStatement(insertSql);
            
            // JSON 데이터 파싱
            Gson gson = new Gson();
            LocationInfo locationInfo = gson.fromJson(request.getReader(), LocationInfo.class);

            if (locationInfo.getLAT()== 0.0 || locationInfo.getLNG() == 0.0 || locationInfo.getDTTM() == null) {
                throw new IllegalArgumentException("LAT, LNG, and Search_DTTM parameters must not be null");
            }
            pstmt.setDouble(1, locationInfo.getLAT());
            pstmt.setDouble(2, locationInfo.getLNG());
            pstmt.setString(3, locationInfo.getDTTM());
            pstmt.executeUpdate();
        }
    }

    public void handleDeleteLocationInfoRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try(Connection dbConn = DatabaseConnection.getConnection()){
            String deleteSql = "DELETE FROM location_info WHERE Location_ID = ?";
            PreparedStatement pstmt = dbConn.prepareStatement(deleteSql);
    
            int id = Integer.parseInt(request.getParameter("ID"));
    
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}