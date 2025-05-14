package com.xcg.aitripassistant.service;

import com.xcg.aitripassistant.domain.po.Item;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/** Tools
 * function calling
 */
@Service
public class TripService {
    @Autowired
    private IItemService itemService;
    @Tool(description = "这是一个用来查询所有商品信息的工具，并且按照价格降序排序")
    public List<Item> list(){
        return itemService.query()
                .orderByDesc("price")
                .last("limit 5")
                .list();
    }
    @Tool(description = "这是一个用来查询价格范围内的商品信息的工具,并且按照降序排序")
    public List<Item> searchItemByPriceRange(@ToolParam(description = "价格范围的左边界") Integer minPrice,
                             @ToolParam(description = "价格范围的右边界") Integer maxPrice){
        return itemService.query()
                .between("price",minPrice,maxPrice)
                .last("limit 5")
                .orderBy(true,false,"price")
                .list();
    }
}
