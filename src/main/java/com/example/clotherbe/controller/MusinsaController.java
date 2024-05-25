package com.example.clotherbe.controller;

import com.example.clotherbe.service.MusinsaScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clother")
public class MusinsaController {

    @Autowired
    private MusinsaScraperService scraperService;

    /**
     * 기온에 맞는 태그를 결정하는 메서드
     * */
    private String determineTemperature(Integer temperature) {
        //하드코딩
        if (temperature <= 4) {
            return "36211"; //숏패딩
        } else if (temperature >= 5 && temperature <= 8) {
            return "36208 "; //겨울싱글코트
        } else if (temperature > 8 && temperature <= 11) {
            return "36209"; //환절기 코트
        } else if (temperature > 11 && temperature <= 16) {
            return "8933"; //카디건
        } else if (temperature > 16 && temperature <= 19) {
            return "214"; //봄
        }else if (temperature > 19 && temperature <= 22) {
            return "36201"; //긴소매 티셔츠
        }else if (temperature > 22 && temperature <= 27) {
            return "23641"; //숏팬츠
        } else {
            return "36203"; //민소매티셔츠
        }
    }


    /**
     * 코디숍
     * */
    @GetMapping("/codishop")
    public List<String> codishopScrapeImages(@RequestParam(required = false, defaultValue="전체") String gender,
                                             @RequestParam(required = false, defaultValue="") String concept) {

        String returnGender = "전체";

        /**
         * 코디숍 URL, 최신순 기준(sort=NEWEST) or 조회순 기준(sort=VIEW_COUNT)
         * */
        String url = "https://www.musinsa.com/app/styles/lists?style_type=" + concept +
                "&tag_no=" + //determineTemperature() <- 여기 괄호 안에 기온 반환하는 함수 넣기
                "&brand=&display_cnt=60&list_kind=big&sort=NEWEST&page=1";

        if(gender.equals("M"))
            returnGender = "남성";
        else if(gender.equals("F"))
            returnGender = "여성";

        return scraperService.scrapeMusinsaImages(returnGender, url);
    }

    /**
     * 코디맵
     * */
    @GetMapping("/codimap")
    public List<String> codimapScrapeImages(@RequestParam(required = false, defaultValue="전체") String gender,
                                            @RequestParam(required = false, defaultValue="") String concept) {

        String returnGender = "전체";

        /**
         * 코디숍 URL, 최신순 기준(sort=NEWEST) or 조회순 기준(sort=view_cnt)
         * */
        String url = "https://www.musinsa.com/app/codimap/lists?style_type=" + concept +
                "&tag_no=" + //determineTemperature() <- 여기 괄호 안에 기온 반환하는 함수 넣기
                "&brand=&display_cnt=60&list_kind=big&sort=date&page=1";

        if(gender.equals("M"))
            returnGender = "남성";
        else if(gender.equals("F"))
            returnGender = "여성";

        return scraperService.scrapeMusinsaImages(returnGender, url);
    }
}
