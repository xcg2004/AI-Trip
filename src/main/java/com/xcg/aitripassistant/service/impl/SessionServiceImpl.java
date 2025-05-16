package com.xcg.aitripassistant.service.impl;

import com.xcg.aitripassistant.domain.po.Session;
import com.xcg.aitripassistant.mapper.SessionMapper;
import com.xcg.aitripassistant.service.ISessionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcg.aitripassistant.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author XCG
 * @since 2025-05-14
 */
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session> implements ISessionService {

    @Autowired
    private SessionMapper sessionMapper;

    @Override
    public Result<List<Long>> listIds(Long userId) {
        List<Long> sessionIds = sessionMapper.selectIds(userId);
        return Result.success(sessionIds);
    }

    @Override
    public Result<Long> create(Long userId) {
        Session session = Session
                .builder()
                .userId(userId)
                .build();
        sessionMapper.create(session);
        Long sessionId = session.getId();
        return Result.success(sessionId);
    }
}
