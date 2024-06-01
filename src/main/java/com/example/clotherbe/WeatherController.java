package com.example.clotherbe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

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
    public ResponseEntity<ArrayList<Weather>> getTodayWeather(
            @RequestParam(name = "latitude") double latitude,
            @RequestParam(name = "longitude") double longitude,
            @RequestParam(name = "date") String date
    ) throws IOException {
        LatXLngY latXLngY_sample = latXLngY.convertGRID_GPS(0, latitude, longitude);
        int inputNx = (int) latXLngY_sample.x;
        int inputNy = (int) latXLngY_sample.y;
        String apiResult = weatherService.callWeatherApi(inputNx, inputNy, date);
        ArrayList<Weather> weatherList = weatherService.dataParsing(apiResult,date);
        return new ResponseEntity<>(weatherList, HttpStatus.OK);
    }
}
