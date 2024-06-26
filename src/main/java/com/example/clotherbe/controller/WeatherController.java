package com.example.clotherbe.controller;

import com.example.clotherbe.domain.LatXLngY;
import com.example.clotherbe.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class WeatherController {

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private LatXLngY latXLngY;

//    @GetMapping("/weatherapi")
//    public String getTodayWeather() throws IOException {
//        String apiResult = weatherService.callWeatherApi(59, 125, "20240601");
//        return apiResult;
//    }

    @GetMapping("/weather")
    public ResponseEntity<Map<String, Object>> getTodayWeather(
            @RequestParam(name = "latitude") double latitude,
            @RequestParam(name = "longitude") double longitude,
            @RequestParam(name = "date") String date,
            @RequestParam(name = "usrName", required = false, defaultValue = "") String userName
    ) throws IOException {
        LatXLngY latXLngY_sample = latXLngY.convertGRID_GPS(0, latitude, longitude);
        int inputNx = (int) latXLngY_sample.x;
        int inputNy = (int) latXLngY_sample.y;
        String apiResult = weatherService.callWeatherApi(inputNx, inputNy, date);
        Map<String, Object> result = weatherService.dataParsing(apiResult, date, userName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
