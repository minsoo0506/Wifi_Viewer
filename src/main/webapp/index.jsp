<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
    <title>Public WiFi Finder</title>
    <style>
        #get {
            margin-top: 20px;
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

        #locationWarning {
            display: block;
            padding: 20px;
            text-align: center;
            font-weight: bold;
            border: 2px solid rgba(211, 211, 211, 0.5);
            border-top: none;
        }
    </style>
    <script>
    var locationFetched = false; // 위치 정보가 가져와졌는지 여부를 저장하는 변수

    // 페이지가 로드될 때 실행
    window.onload = function() {
        // sessionStorage에서 위치 정보 불러오기
        var lat = sessionStorage.getItem('lat');
        var lng = sessionStorage.getItem('lng');
        if (lat && lng) {
            // 위치 정보가 있으면 해당 정보를 입력 필드에 설정하고, 위치 정보가 가져와졌음을 표시
            document.getElementById('lat').value = lat;
            document.getElementById('lng').value = lng;
            locationFetched = true;
            document.getElementById('locationWarning').innerText = '근처 WIFI 정보 보기를 눌러주세요';
        }

        // '내 위치 가져오기' 버튼을 클릭했을 때의 동작을 정의
        document.getElementById('getLocation').addEventListener('click', function() {
            // 위치 정보를 가져오는 코드
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    document.getElementById('lat').value = position.coords.latitude;
                    document.getElementById('lng').value = position.coords.longitude;
                    locationFetched = true;

                    // 위치 정보를 sessionStorage에 저장
                    sessionStorage.setItem('lat', position.coords.latitude);
                    sessionStorage.setItem('lng', position.coords.longitude);

                    // 위치 정보를 서버에 전송하는 코드
                    var xhr = new XMLHttpRequest();
                    var url = "/history";
                    xhr.open("POST", url, true);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    var currentDate = new Date();
                    xhr.send(JSON.stringify({ lat: position.coords.latitude, lng: position.coords.longitude, dttm: currentDate.toISOString()}));
                    xhr.onload = function() {
                      if (xhr.status == 200) {
                        console.log("Location data sent successfully");
                      } else {
                        console.log("Error sending location data: " + xhr.status);
                      }
                    }
                });
            } else {
                alert("Geolocation is not supported by this browser.");
            }
        });

        // '근처 WIFI 정보 보기' 버튼을 클릭했을 때의 동작을 정의
        document.getElementById('getWifi').addEventListener('click', function() {
            if (!locationFetched) {
                // 위치 정보가 아직 가져와지지 않았다면 경고 메시지를 표시
                document.getElementById('locationWarning').style.display = 'block';
                alert('내 위치 가져오기를 한 후에 조회해 주세요');
                return;
            } else {
                // 위치 정보가 가져와졌다면 경고 메시지를 숨김
                document.getElementById('locationWarning').style.display = 'none';
            }

            // 사용자의 위도와 경도 정보 가져오기
            var lat = document.getElementById('lat').value;
            var lng = document.getElementById('lng').value;

            // XMLHttpRequest 객체를 생성하여 서버에 요청을 보내는 코드
            var xhr = new XMLHttpRequest();
            xhr.open('GET', '/nearbyWifi?lat=' + lat + '&lng=' + lng, true);
            xhr.onreadystatechange = function() {
                // 서버로부터 응답이 오면 실행되는 코드
                if (xhr.readyState === XMLHttpRequest.DONE) {
                    if (xhr.status === 200) {
                        // 서버의 응답 상태가 200(성공)인 경우
                        // 서버로부터 받은 응답 텍스트를 JSON으로 파싱하여 테이블에 추가
                        var wifiInfos = JSON.parse(xhr.responseText);
                        // 화면에 표시할 테이블을 가져오기
                        var table = document.getElementById('info');
                        // 테이블의 내용을 초기화
                        table.innerHTML = '<tr><th>거리(Km)</th><th>관리번호</th><th>자치구</th><th>와이파이명</th><th>도로명주소</th><th>상세주소</th><th>설치위치(층)</th><th>설치유형</th><th>설치기관</th><th>서비스구분</th><th>망종류</th><th>설치년도</th><th>실내외구분</th><th>WIFI접속환경</th><th>X좌표</th><th>Y좌표</th><th>작업일자</th></tr>';

                        // 거리를 기준으로 정렬
                        wifiInfos.sort(function(a, b) {
                            return a.distance - b.distance;
                        });
                        
                        // 테이블에 데이터 추가
                        for (var i = 0; i < 20; i++) {
                          var row = table.insertRow(i + 1);
                          row.insertCell(0).innerHTML = wifiInfos[i].distance.toFixed(4);
                          row.insertCell(1).innerHTML = wifiInfos[i].X_SWIFI_MGR_NO;
                          row.insertCell(2).innerHTML = wifiInfos[i].X_SWIFI_WRDOFC;
                          row.insertCell(3).innerHTML = '<a href="/wifiInfo/' + wifiInfos[i].X_SWIFI_MGR_NO + '?distance=' + wifiInfos[i].distance.toFixed(4) + '">' + wifiInfos[i].X_SWIFI_MAIN_NM + '</a>';
                          row.insertCell(4).innerHTML = wifiInfos[i].X_SWIFI_ADRES1;
                          row.insertCell(5).innerHTML = wifiInfos[i].X_SWIFI_ADRES2;
                          row.insertCell(6).innerHTML = wifiInfos[i].X_SWIFI_INSTL_FLOOR;
                          row.insertCell(7).innerHTML = wifiInfos[i].X_SWIFI_INSTL_TY;
                          row.insertCell(8).innerHTML = wifiInfos[i].X_SWIFI_INSTL_MBY;
                          row.insertCell(9).innerHTML = wifiInfos[i].X_SWIFI_SVC_SE;
                          row.insertCell(10).innerHTML = wifiInfos[i].X_SWIFI_CMCWR;
                          row.insertCell(11).innerHTML = wifiInfos[i].X_SWIFI_CNSTC_YEAR;
                          row.insertCell(12).innerHTML = wifiInfos[i].X_SWIFI_INOUT_DOOR;
                          row.insertCell(13).innerHTML = wifiInfos[i].X_SWIFI_REMARS3;
                          row.insertCell(14).innerHTML = wifiInfos[i].LAT;
                          row.insertCell(15).innerHTML = wifiInfos[i].LNT;
                          row.insertCell(16).innerHTML = wifiInfos[i].WORK_DTTM;
                        }
                    } else {    
                        alert('Request failed.  Returned status of ' + xhr.status);
                    } 
                }
            };
            // 서버에 요청을 보냄
            xhr.send();

        });
    };
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
    <div id="get">
        <label for="lat">LAT :</label>
        <input type="text" id="lat" name="lat" value="0.0">

        <label for="lng">LNG :</label>
        <input type="text" id="lng" name="lng" value="0.0">

        <button id="getLocation">내 위치 가져오기</button>
        <button id="getWifi">근처 WIFI 정보 보기</button>
    </div>
</header>
<section>
    <table id="info">
      <tr>
        <th>거리(Km)</th>
        <th>관리번호</th>
        <th>자치구</th>
        <th>와이파이명</th>
        <th>도로명주소</th>
        <th>상세주소</th>
        <th>설치위치(층)</th>
        <th>설치유형</th>
        <th>설치기관</th>
        <th>서비스구분</th>
        <th>망종류</th>
        <th>설치년도</th>
        <th>실내외구분</th>
        <th>WIFI접속환경</th>
        <th>X좌표</th>
        <th>Y좌표</th>
        <th>작업일자</th>
      </tr>
    </table>
    <div id="locationWarning">위치 정보를 입력한 후에 조회해 주세요</div>
</section>
</body>
</html>