package com.example.clotherbe;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherService {


    @Transactional
    public String callWeatherApi(int inputNx, int inputNy, String todayDate) throws IOException {

        StringBuilder result = new StringBuilder();

        // 변수 설정
        String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String authKey = "mhxrJxrdRFpyRRwrUyCf3PjifcOjUCz2SmYZXYc%2BB%2B30dBTRaz0Q8PAGcbBrSjzFTLB764455oRLwqHr8brjQw%3D%3D"; // 본인 서비스 키

        // 구하고자 하는 위치 좌표 대입값 처리
        String nx = Integer.toString(inputNx);
        String ny = Integer.toString(inputNy);

        // 구하고자 하는 오늘날짜 대입값 처리
        String inputDate = todayDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(inputDate, formatter); // 문자열을 LocalDate 객체로 변환
        LocalDate previousDate = date.minusDays(1); // 오늘날짜 기준 전날 계산
        String baseDate = previousDate.format(formatter); // 결과날짜 문자열로 변환

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

        return result.toString();
    }

    @Transactional
    public ArrayList<Weather> dataParsing(String apiResult, String date) {
        ArrayList<Weather> weatherList = new ArrayList<>();
        Map<String, Weather> weatherMap = new HashMap<>();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(apiResult);
            JSONObject response = (JSONObject) jsonObject.get("response");
            JSONObject body = (JSONObject) response.get("body");
            JSONObject items = (JSONObject) body.get("items");
            JSONArray itemArray = (JSONArray) items.get("item");

            for (Object obj : itemArray) {
                JSONObject item = (JSONObject) obj;
                String fcstDate = (String) item.get("fcstDate");
                String fcstTime = (String) item.get("fcstTime");

                if (date.equals(fcstDate)) {
                    String key = fcstDate + fcstTime;
                    Weather weather = weatherMap.getOrDefault(key, new Weather());
                    weather.setNx(((Long) item.get("nx")).intValue());
                    weather.setNy(((Long) item.get("ny")).intValue());
                    weather.setDate(fcstDate);
                    weather.setTime(fcstTime);

                    String category = (String) item.get("category");
                    String fcstValueString = (String) item.get("fcstValue");
                    double fcstValue = 0.0;

                    try {
                        fcstValue = Double.parseDouble(fcstValueString);
                    } catch (NumberFormatException e) {
                        // 숫자가 아닌 값인 경우 fcstValue를 0으로 설정
                        fcstValue = 0.0;
                    }

                    switch (category) {
                        case "TMP":
                            weather.setTMP(fcstValue);
                            break;
                        case "TMN":
                            weather.setTMN(fcstValue);
                            break;
                        case "TMX":
                            weather.setTMX(fcstValue);
                            break;
                        case "SKY":
                            weather.setSKY(fcstValue);
                            break;
                        case "POP":
                            weather.setPOP(fcstValue);
                            break;
                        default:
                            break;
                    }

                    weatherMap.put(key, weather);
                }
            }

            weatherList.addAll(weatherMap.values());

            // 시간 순서대로 정렬
            Collections.sort(weatherList, new Comparator<Weather>() {
                @Override
                public int compare(Weather o1, Weather o2) {
                    return Integer.compare(Integer.parseInt(o1.getTime()), Integer.parseInt(o2.getTime()));
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return weatherList;
    }
}