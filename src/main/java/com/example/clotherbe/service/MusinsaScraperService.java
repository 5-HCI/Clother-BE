package com.example.clotherbe.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MusinsaScraperService {

    private WebDriver driver;

    public MusinsaScraperService() {

        // WebDriver 경로 설정 (ChromeDriver 경로)
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");

        // ChromeOptions 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 브라우저 창을 띄우지 않음

        // WebDriver 시작
        driver = new ChromeDriver(options);
    }

    public List<String> scrapeMusinsaImages(String gender, String url, int linkCount,
                                            String returnUrl1, String returnUrl2) {
        List<String> imageUrls = new ArrayList<>();

        driver.get(url);

        if(gender.equals("여성")||gender.equals("남성")){
            // 성별 버튼 클릭
            WebElement womenButton = driver.findElement(By.xpath("//button[contains(text(), '"+gender+"')]"));
            womenButton.click();
        }

        // 페이지 로드 대기
        try {
            Thread.sleep(5000); // 잠시 대기하여 페이지가 로드되도록 함
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 모든 이미지 요소 가져오기
        List<WebElement> images = driver.findElements(By.tagName("img"));

        // 세 번째 이미지부터 src 속성을 추출하여 리스트에 추가
        for (int i = 2; i < linkCount+2; i++) { // 인덱스 2는 세 번째 요소를 의미함
            WebElement img = images.get(i);
            String src = img.getAttribute("src");

            String id = "";
            if(!returnUrl1.isEmpty()){

                try {
                    WebElement parentAnchor = img.findElement(By.xpath("ancestor::a[1]"));
                    String onclick = parentAnchor.getAttribute("onclick");
                    id = extractIdFromOnclick(onclick);

                } catch (NoSuchElementException e) {
                    //System.out.println("No anchor tag found for image: " + src);
                }
            }

            if (!src.isEmpty()) {
                imageUrls.add(src);

                if(!returnUrl1.isEmpty()){
                    String returnUrl = returnUrl1+id+returnUrl2;
                    imageUrls.add(returnUrl);
                }
            }
        }

        return imageUrls;
    }

    private String extractIdFromOnclick(String onclick) {
        // Extract the ID from the onclick attribute
        if (onclick != null && onclick.contains("goView")) {
            int start = onclick.indexOf("'") + 1;
            int end = onclick.indexOf("'", start);
            return onclick.substring(start, end);
        }
        return "";
    }

    public void closeWebDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}

