package com.publicwifi;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/nearbyWifi")
public class NearbyWifiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            double lat = Double.parseDouble(req.getParameter("lat"));
            double lng = Double.parseDouble(req.getParameter("lng"));

            try (Connection dbConn = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM wifi_info";
                PreparedStatement pstmt = dbConn.prepareStatement(sql);

                ResultSet rs = pstmt.executeQuery();
                List<WifiInfo> wifiInfos = new ArrayList<>();
                while (rs.next()) {
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
                    double distance = haversine(lat, lng, wifiInfo.getLAT(), wifiInfo.getLNT());
                    wifiInfo.setDistance(distance);
                    wifiInfos.add(wifiInfo);
                }

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(new Gson().toJson(wifiInfos));
            }
        } catch (SQLException e) {
            throw new ServletException("Cannot connect to the database", e);
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        int r = 6371; // radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = r * c;
        return distance;
    }
}