package com.example.clotherbe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@SpringBootTest
class ClotherBeApplicationTests {

    @Test
    public String callWeatherApi() throws IOException {

        StringBuilder result = new StringBuilder();

        // 변수 설정
        String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String authKey = "mhxrJxrdRFpyRRwrUyCf3PjifcOjUCz2SmYZXYc%2BB%2B30dBTRaz0Q8PAGcbBrSjzFTLB764455oRLwqHr8brjQw%3D%3D"; // 본인 서비스 키

        // 구하고자 하는 위치 좌표 대입값 처리
        String nx = "59";
        String ny = "125";

        // 구하고자 하는 오늘날짜 대입값 처리
//        String inputDate = todayDate;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        LocalDate date = LocalDate.parse(inputDate, formatter); // 문자열을 LocalDate 객체로 변환
//        LocalDate previousDate = date.minusDays(1); // 오늘날짜 기준 전날 계산
//        String baseDate = previousDate.format(formatter); // 결과날짜 문자열로 변환
        String baseDate = "20240531";

        // 받은 오늘날짜 기준 전날 2300 고정 호출
        String baseTime = "2300";

        String dataType = "JSON";

        StringBuilder urlBuilder = new StringBuilder(apiURL);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + authKey);
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); // 한 시간대당 12줄, 현시간대부터 +25시간까지 표시
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        System.out.println(url);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        String returnLine;

        while ((returnLine = br.readLine()) != null) {
            result.append(returnLine + "\n\r");
        }
        urlConnection.disconnect();

        System.out.println(result.toString());
        return result.toString();
    }
}
