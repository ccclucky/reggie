package com.cjs.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjs.reggie.entity.Setmeal;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
}
