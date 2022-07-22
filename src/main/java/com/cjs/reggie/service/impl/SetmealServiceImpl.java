package com.cjs.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjs.reggie.common.CustomException;
import com.cjs.reggie.dto.SetmealDto;
import com.cjs.reggie.entity.Setmeal;
import com.cjs.reggie.entity.SetmealDish;
import com.cjs.reggie.mapper.SetmealMapper;
import com.cjs.reggie.service.SetmealDishService;
import com.cjs.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保留套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            // 如果无法删除，抛出业务异常
            throw new CustomException("套餐正在售卖中，无法删除");
        }

        // 如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        // 删除关系表的数据---setmeal_dish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(lambdaQueryWrapper);

    }

    /**
     * 更改套餐的出售状态
     * @param ids
     */
    @Override
    public void updateStatus(int status, List<Long> ids) {
        // 将传过来的没个id对应的对象都更改
        for (Long id : ids) {
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(status);
            setmeal.setUpdateTime(LocalDateTime.now());

            this.updateById(setmeal);
        }

    }
}

























