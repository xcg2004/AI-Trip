package com.xcg.aitripassistant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.aitripassistant.domain.dto.LoginDTO;
import com.xcg.aitripassistant.domain.dto.UserRegisterDTO;
import com.xcg.aitripassistant.domain.po.User;
import com.xcg.aitripassistant.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author XCG
 * @since 2025-05-14
 */
public interface IUserService extends IService<User> {

    Result<String> login(LoginDTO loginDTO);

    Result<String> register(UserRegisterDTO userRegisterDTO);

    Result<String> logout(HttpServletRequest request);


}
