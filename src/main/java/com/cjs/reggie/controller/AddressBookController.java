package com.cjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cjs.reggie.common.BaseContext;
import com.cjs.reggie.common.R;
import com.cjs.reggie.entity.AddressBook;
import com.cjs.reggie.mapper.AddressBookMapper;
import com.cjs.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId((BaseContext.getCurrentId()));
        log.info("addressBook: {}", addressBook);
        addressBookService.save(addressBook);

        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {

        log.info("addressBook : {}", addressBook);
        LambdaUpdateWrapper<AddressBook> addressBookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        addressBookLambdaUpdateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookLambdaUpdateWrapper.set(AddressBook::getIsDefault, 0);
        // SQL : update address_book set is_default = 0 where user_id = ?
        addressBookService.update(addressBookLambdaUpdateWrapper);

        addressBook.setIsDefault(1);
        // SQL : update address_book set is_default =1 where id = ?
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    /**
     * 根据 id 查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookLambdaQueryWrapper.eq(AddressBook::getIsDefault, 1);

        // SQL : select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户全部地址信息
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook : {}", addressBook);

        // 条件构造器
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        addressBookLambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);

        // SQL : select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(addressBookLambdaQueryWrapper));
    }

}


























