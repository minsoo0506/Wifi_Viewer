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

// 위치 정보를 저장, 조회, 삭제하기 위한 서블릿
@WebServlet(urlPatterns = "/history")
public class LocationHistoryServlet extends HttpServlet {
    // GET 요청을 처리하기 위한 메소드
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

    // POST 요청을 처리하기 위한 메소드
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
    
    // DELETE 요청을 처리하기 위한 메소드
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

    // GET 요청을 처리하기 위한 메소드
    public void handleGetLocationInfoRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 데이터베이스 연결
        try(Connection dbConn = DatabaseConnection.getConnection()){
            // 데이터베이스 테이블에서 위치 정보 조회
            String sql = "SELECT * FROM location_info";
            // SQL 쿼리 준비
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            
            // SQL 쿼리 실행
            ResultSet rs = pstmt.executeQuery();
            // 조회된 위치 정보를 저장하기 위한 리스트
            List<LocationInfo> locationInfos = new ArrayList<>();
            // 조회된 위치 정보를 리스트에 저장
            while(rs.next()){
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setID(rs.getInt("Location_ID"));
                locationInfo.setLAT(rs.getDouble("LAT"));
                locationInfo.setLNG(rs.getDouble("LNG"));
                locationInfo.setDTTM(rs.getString("Search_DTTM"));
                locationInfos.add(locationInfo);
            }

            // 조회된 위치 정보를 JSP 페이지로 전달
            request.setAttribute("locationInfos", locationInfos);
            request.getRequestDispatcher("/locationHistory.jsp").forward(request, response);
        }
    }

    // POST 요청을 처리하기 위한 메소드
    public void handlePostLocationInfoRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 데이터베이스 연결
        try(Connection dbConn = DatabaseConnection.getConnection()){
            // 데이터베이스 테이블 생성
            Statement stmt = dbConn.createStatement();
            String createTableSql = "CREATE TABLE IF NOT EXISTS location_info (" +
                "Location_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "LAT REAL," +
                "LNG REAL," +
                "Search_DTTM TEXT" +
                ")";
            stmt.execute(createTableSql);
            
            // 데이터베이스 테이블에 위치 정보를 저장하기 위한 SQL 쿼리
            String insertSql = "INSERT INTO location_info (LAT, LNG, Search_DTTM) VALUES (?, ?, ?)";
            PreparedStatement pstmt = dbConn.prepareStatement(insertSql);
            
            // JSON 데이터 파싱
            Gson gson = new Gson(); 
            // JSON 데이터를 LocationInfo 객체로 변환
            LocationInfo locationInfo = gson.fromJson(request.getReader(), LocationInfo.class);

            // 위치 정보가 유효한지 확인
            if (locationInfo.getLAT()== 0.0 || locationInfo.getLNG() == 0.0 || locationInfo.getDTTM() == null) {
                throw new IllegalArgumentException("LAT, LNG, and Search_DTTM parameters must not be null");
            }
            // 위치 정보를 데이터베이스에 저장
            pstmt.setDouble(1, locationInfo.getLAT());
            pstmt.setDouble(2, locationInfo.getLNG());
            pstmt.setString(3, locationInfo.getDTTM());

            // SQL 쿼리 실행
            pstmt.executeUpdate();
        }
    }

    // DELETE 요청을 처리하기 위한 메소드
    public void handleDeleteLocationInfoRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 데이터베이스 연결
        try(Connection dbConn = DatabaseConnection.getConnection()){
            // 데이터베이스 테이블에서 위치 정보 삭제하기 위한 SQL 쿼리
            String deleteSql = "DELETE FROM location_info WHERE Location_ID = ?";
            PreparedStatement pstmt = dbConn.prepareStatement(deleteSql);
            
            // 삭제할 위치 정보의 ID 파라미터
            int id = Integer.parseInt(request.getParameter("ID"));
            
            // 위치 정보 삭제하기 위한 SQL 쿼리 실행
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}