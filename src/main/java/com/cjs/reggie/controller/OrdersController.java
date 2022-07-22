package com.cjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjs.reggie.common.BaseContext;
import com.cjs.reggie.common.R;
import com.cjs.reggie.entity.Orders;
import com.cjs.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单明细
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据 ： {}", orders);

        ordersService.submit(orders);

        return R.success("下单成功");
    }

    /**
     * 订单查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        log.info("当前页码 ： {}， 当前数据条数 ： {}", page, pageSize);

        Page pageInfo = new Page(page, pageSize);

        // 获取用户id
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId, userId);

        ordersService.page(pageInfo, ordersLambdaQueryWrapper);

        return R.success(pageInfo);
    }
}























