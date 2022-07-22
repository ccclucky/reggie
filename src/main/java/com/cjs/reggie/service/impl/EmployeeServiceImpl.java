package com.cjs.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjs.reggie.entity.Employee;
import com.cjs.reggie.mapper.EmployeeMapper;
import com.cjs.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl <EmployeeMapper, Employee> implements EmployeeService {
}
