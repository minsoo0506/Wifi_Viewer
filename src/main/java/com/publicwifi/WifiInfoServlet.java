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
import java.sql.SQLException;
import java.sql.Statement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/wifiInfo")
public class WifiInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            //Connection connection = DatabaseConnection.getConnection();
            handleWifiInfoRequest(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Cannot connect to the database", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleWifiInfoRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try (Connection dbConn = DatabaseConnection.getConnection()) {
            dbConn.setAutoCommit(false);
            Statement stmt = dbConn.createStatement();
            String deleteSql = "DELETE FROM wifi_info";
            stmt.execute(deleteSql);

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

            String apiUrl = "http://openapi.seoul.go.kr:8088/696f4e41666d73703131336d68556d4b/xml/TbPublicWifiInfo/1/5/";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());

            int totalCount = Integer.parseInt(doc.getElementsByTagName("list_total_count").item(0).getTextContent());
            
            String sql = "INSERT INTO wifi_info VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = dbConn.prepareStatement(sql);

            int start = 1;
            int end = 1000;
            while (start <= totalCount) {
                apiUrl = "http://openapi.seoul.go.kr:8088/696f4e41666d73703131336d68556d4b/xml/TbPublicWifiInfo/" + start + "/" + end + "/";
                url = new URL(apiUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/xml");

                doc = builder.parse(conn.getInputStream());

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
                dbConn.commit();
                start = end + 1;
                end += 1000;
            }
            pstmt.close();
            dbConn.close();

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

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String page) {
        try {
            request.getRequestDispatcher(page).forward(request, response);
        } catch (ServletException | IOException ex) {
            ex.printStackTrace();
        }
    }    
}