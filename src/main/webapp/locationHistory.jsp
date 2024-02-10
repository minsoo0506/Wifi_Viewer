<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<head>
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

            #info tr:first-child th {
              text-align: center;
            }

            #info tr:nth-child(even){background-color: #f2f2f2;}

            #info tr:hover {background-color: #ddd;}

            #info th {
              padding-top: 12px;
              padding-bottom: 12px;
              text-align: left;
              background-color: #04AA6D;
              color: white;
            }
    </style>
    <script>
        function deleteLocation(id) {
            var xhr = new XMLHttpRequest();
            xhr.open("DELETE", "/history?ID=" + id, true);
            xhr.onload = function () {
                if (xhr.readyState == 4 && xhr.status == "200") {
                // 삭제가 성공적으로 완료된 후 페이지를 새로 고침
                location.reload();
                } else {
                    console.error(xhr.responseText);
                }
            }
            xhr.send(null);
            }
    </script>
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
        <th>ID</th>
        <th>X좌표</th>
        <th>Y좌표</th>
        <th>조회일자</th>
        <th>비고</th>
      </tr>
      <c:forEach var="locationInfo" items="${locationInfos}">
        <tr>
            <td>${locationInfo.ID}</td>
            <td>${locationInfo.LAT}</td>
            <td>${locationInfo.LNG}</td>
            <td>${locationInfo.DTTM}</td>
            <td style="text-align: center;"><button type="button" onclick="deleteLocation(${locationInfo.ID})">삭제</button></td>
        </tr>
      </c:forEach>
    </table>
</section>
</body>
</html>