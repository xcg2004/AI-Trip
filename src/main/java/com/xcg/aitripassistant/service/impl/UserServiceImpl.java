package com.xcg.aitripassistant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xcg.aitripassistant.domain.dto.LoginDTO;
import com.xcg.aitripassistant.domain.dto.UserRegisterDTO;
import com.xcg.aitripassistant.domain.po.Session;
import com.xcg.aitripassistant.domain.po.User;
import com.xcg.aitripassistant.mapper.SessionMapper;
import com.xcg.aitripassistant.mapper.UserMapper;
import com.xcg.aitripassistant.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcg.aitripassistant.utils.JwtUtils;
import com.xcg.aitripassistant.utils.Md5Util;
import com.xcg.aitripassistant.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author XCG
 * @since 2025-05-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SessionMapper sessionMapper;

    @Override
    public Result<String> login(LoginDTO loginDTO) {
        //查询是否存在用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, loginDTO.getUsername())
        );

        //用户名不存在
        if(user == null){
            return Result.fail("用户名不存在");
        }

        //密码错误
        if(!user.getPassword().equals(Md5Util.md5(loginDTO.getPassword()))){
            return Result.fail("密码错误");
        }

        String token = redisTemplate.opsForValue().get("user:login:" + user.getId());
        if(token == null || token.isBlank()){
            //获取userId，生成token
            Long userId = user.getId();
            token = JwtUtils.generateToken(user.getId().toString());
            //redis存入该用户状态 ttl=token
            redisTemplate.opsForValue().setIfAbsent(
                    "user:login:"+user.getId(),
                    token ,
                    15,
                    TimeUnit.MINUTES);

        }

        return Result.success(token);
    }

    @Override
    @Transactional
    public Result<String> register(UserRegisterDTO userRegisterDTO) {
        //1.注册
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO,user);
        user.setPassword(Md5Util.md5(userRegisterDTO.getPassword()));
        userMapper.insert(user);
        //2.创建5个会话，分配给用户
        sessionMapper.insertFiveBatch(user.getId());
        return Result.success();
    }

    @Override
    public Result<String> logout(HttpServletRequest request) {
        //获取用户id
        String token = request.getHeader("token");
        if(token == null || token.isEmpty()){
            return Result.fail("token不存在或无效");
        }

        //解析用户id
        String userId = JwtUtils.parseToken(token);

        //幂等，检验是否已经注销
        Boolean b = redisTemplate.hasKey("user:login:" + userId);

        if(b.equals(Boolean.FALSE)){
            return Result.success("注销成功");
        }

        //存在，删除key
        redisTemplate.delete("user:login:" + userId);

        return Result.success();
    }


}
