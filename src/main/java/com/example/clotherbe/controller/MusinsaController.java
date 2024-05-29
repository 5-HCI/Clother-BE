package com.example.clotherbe.controller;

import com.example.clotherbe.service.MusinsaScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
            return "215"; //여름
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
                                             @RequestParam(required = false, defaultValue="") List<String> concept,
                                             @RequestParam Integer temperature) {

        List<String> returnLink = new ArrayList<>(); // 최종적으로 반환할 사진 링크

        //기온에 따른 무신사 태그 번호 가져오기
        String tag = determineTemperature(temperature);

        //성별 영어->한글 변환
        String returnGender = "전체";
        if(gender.equals("M"))
            returnGender = "남성";
        else if(gender.equals("F"))
            returnGender = "여성";

        //사용자가 스타일을 3개 선택할 경우 랜덤으로 한 가지 스타일은 2개의 사진 반환
        int randomNumber = 0;
        if(concept.size() == 3) {
            // 0에서 2 사이의 랜덤 숫자 생성 (0, 1, 2 중 하나)
            randomNumber = ThreadLocalRandom.current().nextInt(3);
        }
        int linkCount = 4/concept.size(); // 각 컨셉별 몇 개의 링크(사진)가 필요한지

        for(int i=0;i<concept.size();i++){
            /**
             * 코디숍 URL, 최신순 기준(sort=NEWEST) or 조회순 기준(sort=VIEW_COUNT)
             * */

            // 이미지 url
            String url = "https://www.musinsa.com/app/styles/lists?style_type=" + concept.get(i) +
                    "&brand=" + "&tag_no=" + tag + // 기온에 따른 태그 정보
                    "&display_cnt=60&list_kind=big&sort=NEWEST&page=1";

            // 무신사 링크 url
            String returnUrl1 = "https://www.musinsa.com/app/styles/views/";
            String returnUrl2 = "?use_yn_360=&style_type=" + concept.get(i) +
                    "&brand=&tag_no" + tag +"=&display_cnt=60&list_kind=big&sort=NEWEST&page=1";

            if(i == randomNumber)
                returnLink.addAll(scraperService.scrapeMusinsaImages(returnGender, url, linkCount+1, returnUrl1, returnUrl2));
            else
                returnLink.addAll(scraperService.scrapeMusinsaImages(returnGender, url, linkCount, returnUrl1, returnUrl2));
        }

        return returnLink;
    }

    /**
     * 코디맵
     * */
    @GetMapping("/codimap")
    public List<String> codimapScrapeImages(@RequestParam(required = false, defaultValue="전체") String gender,
                                            @RequestParam(required = false, defaultValue="") List<String> concept,
                                            @RequestParam Integer temperature) {

        List<String> returnLink = new ArrayList<>(); // 최종적으로 반환할 사진 링크

        //기온에 따른 무신사 태그 번호 가져오기
        String hotTag = determineTemperature(temperature + 3);
        String tag = determineTemperature(temperature);
        String coldTag = determineTemperature(temperature - 3);

        String returnGender = "전체";
        if(gender.equals("M"))
            returnGender = "남성";
        else if(gender.equals("F"))
            returnGender = "여성";

        //스타일별 3개의 사진 반환
        for(int i=0;i<concept.size();i++){

            /**
             * 코디숍 URL, 최신순 기준(sort=NEWEST) or 조회순 기준(sort=view_cnt)
             * */

            String url1 = "https://www.musinsa.com/app/codimap/lists?style_type=" + concept.get(i) +
                    "&tag_no=" + hotTag + "&brand=&display_cnt=60&list_kind=big&sort=date&page=1";
            returnLink.addAll(scraperService.scrapeMusinsaImages(returnGender, url1, 1, "", ""));
            String url2 = "https://www.musinsa.com/app/codimap/lists?style_type=" + concept.get(i) +
                    "&tag_no=" + tag + "&brand=&display_cnt=60&list_kind=big&sort=date&page=1";
            returnLink.addAll(scraperService.scrapeMusinsaImages(returnGender, url2, 1, "", ""));
            String url3 = "https://www.musinsa.com/app/codimap/lists?style_type=" + concept.get(i) +
                    "&tag_no=" + coldTag + "&brand=&display_cnt=60&list_kind=big&sort=date&page=1";
            returnLink.addAll(scraperService.scrapeMusinsaImages(returnGender, url3, 1, "", ""));

        }

        return returnLink;
    }
}
