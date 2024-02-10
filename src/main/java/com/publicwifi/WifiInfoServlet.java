package com.publicwifi;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

// 와이파이 정보를 조회하기 위한 서블릿
@WebServlet(urlPatterns = {"/wifiInfo", "/wifiInfo/*"})
public class WifiInfoServlet extends HttpServlet {
    // GET 요청을 처리하기 위한 메소드
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 요청 URL이 /wifiInfo/*로 시작하는지 확인
        String pathInfo = req.getPathInfo();
        // /wifiInfo로 시작하는 경우, 기존 로직을 실행
        if (pathInfo == null || pathInfo.equals("/")) {
            // 기존 로직을 실행
            try {
                handleWifiInfoRequest(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // /wifiInfo/*로 시작하는 경우, 새로운 로직을 실행
            handleWifiDetailRequest(req, resp);
        }
    }

    // POST 요청을 처리하기 위한 메소드
    public void handleWifiInfoRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 데이터베이스 연결
        try (Connection dbConn = DatabaseConnection.getConnection()) {
            // 데이터베이스 테이블에서 와이파이 정보 조회
            dbConn.setAutoCommit(false); // AutoCommit을 false로 설정
            // 기존 데이터 삭제
            Statement stmt = dbConn.createStatement();
            String deleteSql = "DELETE FROM wifi_info";
            stmt.execute(deleteSql);

            // 새로운 테이블 생성
            String createTableSql = "CREATE TABLE IF NOT EXISTS wifi_info (" +
                    "X_SWIFI_MGR_NO TEXT PRIMARY KEY," +
                    "X_SWIFI_WRDOFC TEXT," +
                    "X_SWIFI_MAIN_NM TEXT," +
                    "X_SWIFI_ADRES1 TEXT," +
                    "X_SWIFI_ADRES2 TEXT," +
                    "X_SWIFI_INSTL_FLOOR TEXT," +
                    "X_SWIFI_INSTL_TY TEXT," +
                    "X_SWIFI_INSTL_MBY TEXT," +
                    "X_SWIFI_SVC_SE TEXT," +
                    "X_SWIFI_CMCWR TEXT," +
                    "X_SWIFI_CNSTC_YEAR INTEGER," +
                    "X_SWIFI_INOUT_DOOR TEXT," +
                    "X_SWIFI_REMARS3 TEXT," +
                    "LAT REAL," +
                    "LNT REAL," +
                    "WORK_DTTM TEXT" +
                    ")";
            stmt.execute(createTableSql);

            // 와이파이 정보 API 호출
            String apiUrl = "http://openapi.seoul.go.kr:8088/696f4e41666d73703131336d68556d4b/xml/TbPublicWifiInfo/1/5/";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // GET 요청 설정
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");

            // API 응답을 XML로 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // DocumentBuilderFactory 생성
            DocumentBuilder builder = factory.newDocumentBuilder(); // DocumentBuilder 생성
            Document doc = builder.parse(conn.getInputStream()); // XML 파싱

            // API 응답에서 총 개수를 추출
            int totalCount = Integer.parseInt(doc.getElementsByTagName("list_total_count").item(0).getTextContent());
            
            // 와이파이 정보를 데이터베이스에 저장하는 코드
            String sql = "INSERT INTO wifi_info VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = dbConn.prepareStatement(sql);

            int start = 1; // 시작 인덱스
            int end = 1000; // 종료 인덱스
            // API 응답에서 와이파이 정보를 추출
            while (start <= totalCount) {
                // API 호출
                apiUrl = "http://openapi.seoul.go.kr:8088/696f4e41666d73703131336d68556d4b/xml/TbPublicWifiInfo/" + start + "/" + end + "/";
                url = new URL(apiUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/xml");

                // XML 파싱
                doc = builder.parse(conn.getInputStream());

                // 와이파이 정보를 데이터베이스에 저장
                NodeList nodeList = doc.getElementsByTagName("row");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    pstmt.setString(1, element.getElementsByTagName("X_SWIFI_MGR_NO").item(0).getTextContent());
                    pstmt.setString(2, element.getElementsByTagName("X_SWIFI_WRDOFC").item(0).getTextContent());
                    pstmt.setString(3, element.getElementsByTagName("X_SWIFI_MAIN_NM").item(0).getTextContent());
                    pstmt.setString(4, element.getElementsByTagName("X_SWIFI_ADRES1").item(0).getTextContent());
                    pstmt.setString(5, element.getElementsByTagName("X_SWIFI_ADRES2").item(0).getTextContent());
                    pstmt.setString(6, element.getElementsByTagName("X_SWIFI_INSTL_FLOOR").item(0).getTextContent());
                    pstmt.setString(7, element.getElementsByTagName("X_SWIFI_INSTL_TY").item(0).getTextContent());
                    pstmt.setString(8, element.getElementsByTagName("X_SWIFI_INSTL_MBY").item(0).getTextContent());
                    pstmt.setString(9, element.getElementsByTagName("X_SWIFI_SVC_SE").item(0).getTextContent());
                    pstmt.setString(10, element.getElementsByTagName("X_SWIFI_CMCWR").item(0).getTextContent());
                    pstmt.setString(11, element.getElementsByTagName("X_SWIFI_CNSTC_YEAR").item(0).getTextContent());
                    pstmt.setString(12, element.getElementsByTagName("X_SWIFI_INOUT_DOOR").item(0).getTextContent());
                    pstmt.setString(13, element.getElementsByTagName("X_SWIFI_REMARS3").item(0).getTextContent());
                    pstmt.setString(14, element.getElementsByTagName("LAT").item(0).getTextContent());
                    pstmt.setString(15, element.getElementsByTagName("LNT").item(0).getTextContent());
                    pstmt.setString(16, element.getElementsByTagName("WORK_DTTM").item(0).getTextContent());

                    //addBatch 하기
                    pstmt.addBatch();
                }
                //다 담은 후 Batch 실행
                pstmt.executeBatch();
                //Batch 초기화
                pstmt.clearBatch();
                dbConn.commit(); // commit
                start = end + 1; // 시작 인덱스 업데이트
                end += 1000; // 종료 인덱스 업데이트
            }
            pstmt.close();
            dbConn.close();

            // 와이파이 정보 조회 결과를 JSP 페이지로 전달
            request.setAttribute("message", totalCount + "개의 WIFI 정보를 정상적으로 저장하였습니다.");
            request.getRequestDispatcher("load-wifi.jsp").forward(request, response);
        } catch (NullPointerException e) {
            e.printStackTrace();
            request.setAttribute("message", "Null pointer exception: " + e.getMessage());
            forwardToPage(request, response, "load-wifi.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "기타 에러 발생: " + e.getMessage());
            forwardToPage(request, response, "load-wifi.jsp");
        }
    }

    // GET 요청을 처리하기 위한 메소드
    public void handleWifiDetailRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 요청 URL에서 와이파이 관리 번호를 추출
        String pathInfo = request.getPathInfo();
        String wifiMgrNo = pathInfo.substring(1); 
    
        // 쿼리 파라미터에서 거리 정보를 추출
        String distanceStr = request.getParameter("distance");
        double distance = 0.0;
        if (distanceStr != null) {
            distance = Double.parseDouble(distanceStr);
        }
        
        // 와이파이 정보 조회
        WifiInfo wifiInfo = getWifiInfo(wifiMgrNo);
        // 거리 정보 설정
        wifiInfo.setDistance(distance);
        
        // 와이파이 정보를 JSP 페이지로 전달
        request.setAttribute("wifiInfo", wifiInfo);
        forwardToPage(request, response, "/wifiDetail.jsp");
    }

    // JSP 페이지로 포워딩하는 메소드
    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String page) {
        try {
            // JSP 페이지로 포워딩
            request.getRequestDispatcher(page).forward(request, response);
        } catch (ServletException | IOException ex) {
            ex.printStackTrace();
        }
    }
    
    // 와이파이 정보를 조회하는 메소드(상세 정보 조회 시 사용) 
    private WifiInfo getWifiInfo(String wifiMgrNo) throws ServletException {
        // 데이터베이스 연결
        try (Connection dbConn = DatabaseConnection.getConnection()){
            // 데이터베이스 테이블에서 와이파이 정보 조회
            String sql = "SELECT * FROM wifi_info WHERE X_SWIFI_MGR_NO = ?";
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setString(1, wifiMgrNo);
            ResultSet rs = pstmt.executeQuery();
    
            WifiInfo wifiInfo = new WifiInfo();
            wifiInfo.setX_SWIFI_MGR_NO(rs.getString("X_SWIFI_MGR_NO"));
            wifiInfo.setX_SWIFI_WRDOFC(rs.getString("X_SWIFI_WRDOFC"));
            wifiInfo.setX_SWIFI_MAIN_NM(rs.getString("X_SWIFI_MAIN_NM"));
            wifiInfo.setX_SWIFI_ADRES1(rs.getString("X_SWIFI_ADRES1"));
            wifiInfo.setX_SWIFI_ADRES2(rs.getString("X_SWIFI_ADRES2"));
            wifiInfo.setX_SWIFI_INSTL_FLOOR(rs.getString("X_SWIFI_INSTL_FLOOR"));
            wifiInfo.setX_SWIFI_INSTL_TY(rs.getString("X_SWIFI_INSTL_TY"));
            wifiInfo.setX_SWIFI_INSTL_MBY(rs.getString("X_SWIFI_INSTL_MBY"));
            wifiInfo.setX_SWIFI_SVC_SE(rs.getString("X_SWIFI_SVC_SE"));
            wifiInfo.setX_SWIFI_CMCWR(rs.getString("X_SWIFI_CMCWR"));
            wifiInfo.setX_SWIFI_CNSTC_YEAR(rs.getInt("X_SWIFI_CNSTC_YEAR"));
            wifiInfo.setX_SWIFI_INOUT_DOOR(rs.getString("X_SWIFI_INOUT_DOOR"));
            wifiInfo.setX_SWIFI_REMARS3(rs.getString("X_SWIFI_REMARS3"));
            wifiInfo.setLAT(rs.getDouble("LAT"));
            wifiInfo.setLNT(rs.getDouble("LNT"));
            wifiInfo.setWORK_DTTM(rs.getString("WORK_DTTM"));
            
            return wifiInfo;
        } catch (SQLException e) {
            throw new ServletException("Cannot connect to the database", e);
        }
    }
}