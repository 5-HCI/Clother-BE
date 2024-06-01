package com.example.clotherbe;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Weather {
    private int nx;
    private int ny;
    private String date;
    private String time;
    private double TMP; // 1시간 기온
    private double TMN; // 일 최저기온
    private double TMX; // 일 최고기온
    private double SKY; // 하늘상태
    private double POP; // 강수확률
}