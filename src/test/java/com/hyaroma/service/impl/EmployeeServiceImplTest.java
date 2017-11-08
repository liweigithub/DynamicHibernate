package com.hyaroma.service.impl;

import com.alibaba.fastjson.JSON;
import com.hyaroma.blog.BaseTest;
import com.hyaroma.dao.base.Condition;
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




    @Test
    public void  demo(){
        Employee emp = new Employee();
        emp.setId(new Long("102568974547"));
        emp.setBaLance(22);
        emp.setBirthday(new Date());
        emp.setSex(1);
        emp.setName("wstv");
        emp.setStatus((short) 0);
        employeeService.saveOrUpdate(emp);
        //-----------------查询-------------------------------
        //查询方式1
        Employee employee = employeeService.findById(new Long("102568974547"));

        //hql
        List<Employee>  employeeList = null;
        employeeList= employeeService.findByHQL("from com.hyaroma.domain.Employee where id =? and name = ? ",new Long(1),"liwei");

        //sql
        employeeList =  employeeService.findBySql("select * from oa.employee", Employee.class);




        /*---------------------------------------findPage start------------------------------------------------------*/

        //-----------------------findPage 用法1
        //conditions 方式 如过没有条件或者排序条件传递控制（null）即可 此方式没有分页 分页参见 findPage
        ArrayList< Condition > conditions =  new ArrayList<Condition>();
        //查询性别为男的
        conditions.add(new Condition("sex",new Long("1"), Op.EQ, OpType.AND));
        //這里Condition  Op 支持多种方式 特别注意的是：OpType 支持两个字段作为一个判断条件：例如 where 1=1 and (id =1 or code=1)
        //有关联对象采用导航方式添加条件即可,例如：conditions.add(new Condition("department.id",new Long("1123"), Op.EQ, OpType.AND));
        LinkedHashMap<String, OrderType > orderBys = new LinkedHashMap<String, OrderType>();
        employeeList = employeeService.findList(conditions, orderBys);


        //-----------------------findPage 用法2
        //类似上面的方式 但是支持分页 (如果想用该方法不分页 则pageSize 传递最大值即可：例如: int pageSzie = Integer.MAX_VALUE;)
        int pageIndex = 1;
        int pageSize =50;
        PageBean<Employee> pageBean = employeeService.findPage(conditions, orderBys, pageIndex, pageSize);
        System.out.println("数据信息："+pageBean.getDataCount());
        System.out.println("总页数："+pageBean.getTotalPage());
        System.out.println("总记录数："+pageBean.getDataCount());
        System.out.println("是否有下一页："+pageBean.isNextPage());
        System.out.println("是否有上一页："+pageBean.isPrePage());
        System.out.println("类似百度的分页：前4后5 滚动页码：");

        for(int i = pageBean.getStart();i<=pageBean.getEnd();i++){
            int currentPage = i;
            System.out.print("\t"+i);
        }

        //通常情况下 写api接口 我们不想把 paben data的所有字段返回给移动端 我们就需要這样 遍历数据 取出想要的数据 封装到map

        List<Map<String,Object>> dataMaps = new ArrayList<Map<String, Object>>();
        for(Employee employee1:pageBean.getData()){
            Map<String,Object> data = new HashMap<String, Object>();
            data.put("name",employee1.getName());
            data.put("birthday",employee1.getBirthday());
            dataMaps.add(data);
        }
        Map<String, Object> infoData = PageBean.renderPageInfoData(pageBean, dataMaps, pageIndex);
        //给客户端就是json 数据 如果是spring mvc  直接返回 map就可以了
        System.out.print(JSON.toJSON(infoData));
        /*---------------------------------------findPage end------------------------------------------------------*/


        /*---------------------------------------findByNamedQuery start------------------------------------------------------*/
        /************findByNamedQuery************/
        String queryName = "emp.findByName";//xxxx-dynamic.ftl 内模板缓存的节点名称：emp.findByName
        Map<String,Object> parameters  = new HashMap<String, Object>();
        parameters.put("xxx","1");
        String  [] fildes = new String[]{"id","name"};
        PageBean<Map<String, Object>> mapPageBean = employeeService.findByNamedQuery(queryName, pageIndex, pageSize,
                fildes, parameters);
        List<Map<String, Object>> mapPageBeanDatas = mapPageBean.getData();
        System.out.print(JSON.toJSON(mapPageBeanDatas));


        /************findByNamedQuery************/
        //执行指定HQL语句：
        queryName = "emp.findById";
        String[] fields = {"id","name"};
        Map pramMap = new HashMap();
        pramMap.put("id",new Long("102568974547"));
        PageBean<Employee> pageBean2 = this.employeeService.findByNamedQuery(queryName, 1, 100, Employee.class, fields, pramMap);
        employeeList = pageBean2.getData();

    /*---------------------------------------findByNamedQuery end------------------------------------------------------*/
        //执行指定语句
        parameters.clear();
        parameters.put("id",new Long("102568974547"));
        int status = employeeService.executeByDynamicName("emp.deleteEmp",parameters);
        System.out.println("影响了："+status);
    }
}
