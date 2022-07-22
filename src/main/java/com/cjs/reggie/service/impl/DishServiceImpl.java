package com.cjs.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjs.reggie.common.CustomException;
import com.cjs.reggie.dto.DishDto;
import com.cjs.reggie.entity.Dish;
import com.cjs.reggie.entity.DishFlavor;
import com.cjs.reggie.entity.Setmeal;
import com.cjs.reggie.mapper.DishMapper;
import com.cjs.reggie.service.DishFlavorService;
import com.cjs.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();  // 菜品ID

        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表的基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        // 添加当前提交过来的口味数据--dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品及对应口味信息
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {
        // 判断菜品状态
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, ids);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);

        int count = this.count(dishLambdaQueryWrapper);
        if (count > 0) {
            // 无法删除，抛出业务异常
            throw new CustomException("菜品正在出售，无法删除");
        }

        // 如果可以删除，先删除菜品表数据---dish
        this.removeByIds(ids);

        // 删除口味表数据---dish_flavor
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);

        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
    }

    /**
     * 更改菜品售卖状态
     * @param status
     * @param ids
     */
    @Override
    public void updateStatus(int status, List<Long> ids) {

        // 将传过来的没个id对应的对象都更改
        for (Long id : ids) {
            Dish dish = this.getById(id);
            dish.setStatus(status);
            dish.setUpdateTime(LocalDateTime.now());

            this.updateById(dish);
        }

    }
}























