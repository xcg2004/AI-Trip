package com.xcg.aitripassistant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.aitripassistant.domain.po.Session;
import com.xcg.aitripassistant.utils.Result;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author XCG
 * @since 2025-05-14
 */
public interface ISessionService extends IService<Session> {
    Result<List<Long>> listIds(Long userId);

    Result<Long> create(Long userId);
}
