package com.example.clotherbe.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    public String name = "사용자";
    public String gender = "남자";

    public UserInfo() {
    }

    public UserInfo(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }
}
