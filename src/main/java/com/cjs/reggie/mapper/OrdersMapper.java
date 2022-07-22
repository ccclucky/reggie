package com.cjs.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjs.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
