package com.example.clotherbe.controller;

import com.example.clotherbe.domain.UserInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

    @PostMapping("/create")
    public UserInfo createUser(@RequestBody UserInfo userInfo) {
        return userInfo;
    }/** 반환 형태
     * {
     *     "name": "오성",
     *     "gender": "남자"
     * }
     * */
}
