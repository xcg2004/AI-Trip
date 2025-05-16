package com.xcg.aitripassistant.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcg.aitripassistant.domain.po.Session;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author XCG
 * @since 2025-05-14
 */
public interface SessionMapper extends BaseMapper<Session> {

    void insertFiveBatch(Long userId);

    List<Long> selectIds(Long userId);

    void create(Session session);
}
