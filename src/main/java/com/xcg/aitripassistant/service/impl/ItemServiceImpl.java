package com.xcg.aitripassistant.service.impl;

import com.xcg.aitripassistant.domain.po.Item;
import com.xcg.aitripassistant.mapper.ItemMapper;
import com.xcg.aitripassistant.service.IItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author XCG
 * @since 2025-05-02
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

}
