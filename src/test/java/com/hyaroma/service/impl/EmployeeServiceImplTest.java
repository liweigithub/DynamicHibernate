package com.hyaroma.service.impl;

import com.alibaba.fastjson.JSON;
import com.hyaroma.blog.BaseTest;
import com.hyaroma.dao.base.Condition;
import com.hyaroma.dao.base.IBaseDao;
import com.hyaroma.dao.base.PageBean;
import com.hyaroma.domain.Employee;
import com.hyaroma.enums.Op;
import com.hyaroma.enums.OpType;
import com.hyaroma.enums.OrderType;
import com.hyaroma.service.IEmployeeService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;


public class EmployeeServiceImplTest extends BaseTest{

    @Resource(name = "employeeServiceImpl")
    private IEmployeeService employeeService;

    @Resource(name = "baseDaoImpl")
    private IBaseDao baseDao;

    @Test
    public void save(){
        Employee employee = new Employee();
        String s = UUID.randomUUID().toString();
        employee.setId(s);
        employee.setBirthday(new Date());
        employee.setName("wstv");
        employee.setSex(1);
        employee.setStatus((short)1);
        employee.setBalance(100.01d);
        employeeService.saveOrUpdate(employee);
    }

    @Test
    public void update(){
        employeeService.update();
    }

    public void find(){
        Employee employee = employeeService.findById("1111");
    }
}
