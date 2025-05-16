package com.xcg.aitripassistant.controller;


import com.xcg.aitripassistant.domain.dto.LoginDTO;
import com.xcg.aitripassistant.domain.dto.UserRegisterDTO;
import com.xcg.aitripassistant.service.IUserService;
import com.xcg.aitripassistant.utils.Result;
import com.xcg.aitripassistant.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author XCG
 * @since 2025-05-12
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * 登录
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO){
        log.info("用户登录：{}",loginDTO);
        return userService.login(loginDTO);
    }

    /**
     * 注册
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO){
        log.info("用户注册：{}",userRegisterDTO);
        return userService.register(userRegisterDTO);
    }

    /**
     * 退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        log.info("用户注销：{}");
        return userService.logout(request);
    }


}
