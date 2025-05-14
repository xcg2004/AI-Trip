package com.xcg.aitripassistant.interceptor;

import com.xcg.aitripassistant.propertity.JwtProperties;
import com.xcg.aitripassistant.utils.UserHolder;
import com.xcg.aitripassistant.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            String userId =  JwtUtils.parseToken(token);

            // 检查 Redis 中是否存在登录状态
            Boolean isLogin = redisTemplate.hasKey("user:login:" + userId);
            if (Boolean.FALSE.equals(isLogin)) {
                response.setStatus(401);
                return false;
            }

            log.info("当前用户id：{}", userId);
            UserHolder.setUser(Long.valueOf(userId));
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserHolder.removeUser();
    }
}
