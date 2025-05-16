package com.xcg.aitripassistant.controller;

import com.xcg.aitripassistant.service.ISessionService;
import com.xcg.aitripassistant.utils.Result;
import com.xcg.aitripassistant.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/session")
public class SessionController {
    @Autowired
    private ISessionService sessionService;

    @GetMapping("/ids")
    public Result<List<Long>> getSessionIds(){
        Long userId = UserHolder.getUser();
        log.info("获取：{}用户的所有会话id",userId);
        return sessionService.listIds(userId);
    }

    /**
     * 用户点击新建会话
     * @return
     */
    @PostMapping("/create")
    public Result<Long> createSession(){
        Long userId = UserHolder.getUser();
        log.info("用户：{}新建会话",userId);
        return sessionService.create(userId);
    }

}
