<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Public WiFi Finder</title>
    <style>
        nav {
          margin-top: 10px;
          margin-bottom: 10px;
        }

        #info {
          font-family: Arial, Helvetica, sans-serif;
          border-collapse: collapse;
          width: 100%;
        }

        #info td, #info th {
          border: 1px solid #ddd;
          padding: 8px;
        }

        #info .title {
            text-align: center;
            background-color: #04AA6D;
            color: white;
        }

        #info tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        #info tr:nth-child(odd) {
            background-color: #fff;
        }

        #info tr:hover {
            background-color: #ddd;
        }
    </style>
</head>
<body>
    <header>
    <h1>와이파이 정보 구하기</h1>
    <nav>
        <a href="/">홈</a> |
        <a href="/history">위치 히스토리 목록</a> |
        <a href="/wifiInfo">Open API 와이파이 정보 가져오기</a>
    </nav>
    </header>
    <section>
    <table id="info">
        <tr>
            <td class="title">거리(Km)</td>
            <td class="value">${wifiInfo.distance}</td>
        </tr>
        <tr>
            <td class="title">관리번호</td>
            <td class="value">${wifiInfo.x_SWIFI_MGR_NO}</td>
        </tr>
        <tr>
            <td class="title">자치구</td>
            <td class="value">${wifiInfo.x_SWIFI_WRDOFC}</td>
        </tr>
        <tr>
            <td class="title">와이파이명</td>
            <td class="value">${wifiInfo.x_SWIFI_MAIN_NM}</td>
        </tr>
        <tr>
            <td class="title">도로명주소</td>
            <td class="value">${wifiInfo.x_SWIFI_ADRES1}</td>
        </tr>
        <tr>
            <td class="title">상세주소</td>
            <td class="value">${wifiInfo.x_SWIFI_ADRES2}</td>
        </tr>
        <tr>
            <td class="title">설치위치(층)</td>
            <td class="value">${wifiInfo.x_SWIFI_INSTL_FLOOR}</td>
        </tr>
        <tr>
            <td class="title">설치유형</td>
            <td class="value">${wifiInfo.x_SWIFI_INSTL_TY}</td>
        </tr>
        <tr>
            <td class="title">설치기관</td>
            <td class="value">${wifiInfo.x_SWIFI_INSTL_MBY}</td>
        </tr>
        <tr>
            <td class="title">서비스구분</td>
            <td class="value">${wifiInfo.x_SWIFI_SVC_SE}</td>
        </tr>
        <tr>
            <td class="title">망종류</td>
            <td class="value">${wifiInfo.x_SWIFI_CMCWR}</td>
        </tr>
        <tr>
            <td class="title">설치년도</td>
            <td class="value">${wifiInfo.x_SWIFI_CNSTC_YEAR}</td>
        </tr>
        <tr>
            <td class="title">실내외구분</td>
            <td class="value">${wifiInfo.x_SWIFI_INOUT_DOOR}</td>
        </tr>
        <tr>
            <td class="title">WIFI접속환경</td>
            <td class="value">${wifiInfo.x_SWIFI_REMARS3}</td>
        </tr>
        <tr>
            <td class="title">X좌표</td>
            <td class="value">${wifiInfo.LAT}</td>
        </tr>
        <tr>
            <td class="title">Y좌표</td>
            <td class="value">${wifiInfo.LNT}</td>
        </tr>
        <tr>
            <td class="title">작업일자</td>
            <td class="value">${wifiInfo.WORK_DTTM}</td>
        </tr>
    </table>
</section>
</body>
</html>