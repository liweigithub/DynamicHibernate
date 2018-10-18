package com.hyaroma.service.impl;

import com.alibaba.fastjson.JSON;
import com.hyaroma.dao.base.Condition;
import com.hyaroma.dao.base.IBaseDao;
import com.hyaroma.dao.base.PageBean;
import com.hyaroma.domain.Employee;
import com.hyaroma.enums.Op;
import com.hyaroma.enums.OpType;
import com.hyaroma.enums.OrderType;
import com.hyaroma.service.IEmployeeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author  wstv
 */
@Service("employeeServiceImpl")
public class EmployeeServiceImpl  extends  BaseServiceImpl<Employee> implements IEmployeeService{
    @Resource(name = "baseDaoImpl")
    private IBaseDao baseDao;

    @Override
    public void update() {
        Employee employee = baseDao.findById(Employee.class,"81581467-b9b0-4132-9ffb-1a662d79971a");
        employee.setBirthday(new Date());
        employee.setName("wstv124");
        employee.setSex(1);
        employee.setStatus((short)1);
        employee.setBalance(100.01d);
        baseDao.save(employee);
    }

    /**
     * 直接查询 不带分页
     * @return
     */
    public List<Employee> list(){
        ArrayList<Condition> conditions = new ArrayList<Condition>();

        //group 给表示组合查询 第一个条件：and 表示 组合内的条件  第二个and 表示组合之前的条件 就是这个条件需要是 or  还是 and
        //默认底层会有 where 1=1
        //比如在组合之前 此条件之前已经有一个  conditions.add(new Condition("status","1", Op.EQ,OpType.AND)); 按status 的
        //需要再添加一个组合条件：(sex=1 AND name LIKE '1%')  类似我们想要的结果是这样的：  status = 1 and (sex=1 AND name LIKE '1%')  此时这个组合条件是需要以or 还是 and 来进行封装就取决于 第二个and（group_and_and）
        //所以整个过程下来 我们得到的结果是：where 1=1 and status = 1 and (sex=1 AND name LIKE '1%')
        conditions.add(new Condition("status","1", Op.EQ,OpType.AND));
        //注意column 的参数是两个
        conditions.add(new Condition("sex,name","1", Op.LIKE_RIGHT, OpType.GROUP_AND_AND));
        //其他的类似

        //baseDao.findList(Employee.class,conditions,orderBys);这种方式也可以  “万能” baseDao
        //this.findList(conditions, orderBys); 此方式可以直接在controller用 注入EmployeeService即可
        List<Employee> list = this.findList(conditions, null);
        System.out.println(JSON.toJSON(list));

        //

        //EQ("=", "等于"),
        //EQ("=", "等于"),
        //      NEQ("<>", "不等于"),
        //    GT(">", "大于"),
        //    GTE(">=", "大于等于"),
        //    LT("<", "小于"),
        //    LTE("<=", "小于等于"),
        //    LIKE("like", "包含"),
        conditions.add(new Condition("status","1", Op.EQ,OpType.AND));


        //LIKE_LEFT("like", "左包含"),   '%wstv'
        //LIKE_RIGHT("like", "右包含"),   'wstv%'
        //where 1=1 and name like '%wstv'
        conditions.add(new Condition("name","wstv", Op.LIKE_LEFT,OpType.AND));

        // IN("in","包含"),
        // NOT_IN("not in","不包含");
        String idsStr = "23231,34234,545345,56452342";
        //where 1=1 and id in ('xxx','xx','xxxx')
        conditions.add(new Condition("id",idsStr, Op.IN,OpType.AND));
        list = this.findList(conditions, null);


        //order by 按需要排序的字段有序的往条件里添加
        LinkedHashMap<String, OrderType> orderBys = new LinkedHashMap<String, OrderType>();
        orderBys.put("birthday",OrderType.DESC);
        list = this.findList(conditions,  orderBys);

        return list;

        //condtion 一些使用技巧
        //有关联对象采用导航方式添加条件即可,
        // 例如：conditions.add(new Condition("duty.id","1123", Op.EQ, OpType.AND));
    }

    public void listPage(String name,int pageIndex,int pageSize){
        //此方式可以直接在controller使用
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        if (!"".equals(name)){
            conditions.add(new Condition("name",name, Op.EQ,OpType.AND));
        }

        //orderby 如果不需要排序 直接传递null值即可，
        //此时如果不想分页 但是想用findPage 将 pageSize 最大值即可:Integer.MAX_VALUE 或者直接用上面的    findList 方法
        PageBean<Employee> pageBean = this.findPage(conditions, null, pageIndex, pageSize);
        System.out.println("数据信息："+pageBean.getDataCount());
        System.out.println("总页数："+pageBean.getTotalPage());
        System.out.println("总记录数："+pageBean.getDataCount());
        System.out.println("是否有下一页："+pageBean.isNextPage());
        System.out.println("是否有上一页："+pageBean.isPrePage());
        System.out.println("类似百度的分页：前4后5 滚动页码：");

        //打印页码值
        for(int i = pageBean.getStart();i<=pageBean.getEnd();i++){
            int currentPage = i;
            System.out.print("\t"+i);
        }

        //通常情况下 写api接口 我们不想把 pageBean data的所有字段返回给移动端 我们就需要這样 遍历数据 取出想要的数据 封装到map
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

    }

    /**
     * 根据动态模板sql语句查询分页数据
     */
    public void listPage1BySql(){
        //动态模板语句返回的字段，需要和模板的sql字段顺序保持一致
        String[] fields = new String[]{
                "id",
                "name"
        };

        String queryName = "emp.findByName";
        int pageIndex =0;
        int pageSize=20;
        Map<String, Object> parameters = new HashMap<String, Object>();
        PageBean<Map<String, Object>> byNamedQuery = this.findByNamedQuery(queryName, pageIndex, pageSize, new String[]{"name"}, parameters);
    }


    /**
     * 根据动态模板HQL语句查询分页数据
     */
    public void listPage2ByHql(){
        String queryName = "emp.findById";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id","111");
        PageBean<Employee> pageBean =  baseDao.queryByDynamicName(queryName, 0,1, Employee.class,null, parameters);
        List<Employee> data = pageBean.getData();
        if (data!=null && !data.isEmpty()){
            Employee employee = data.get(0);
        }
    }


    /**
     * 根据动态模板HQL语句查询分页数据,hql语句为多个,返回list<Object[]>的情况
     * 比如：select  emp.name,duty.name from  Employee emp,Duty du where du.empid = emp.id where duty.name like '%主管%'
     */
    public void listPage3ByHql(){
        String queryName = "emp.findXXXXX";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name","主管");
        PageBean<Object[]> pageBean =  baseDao.queryByDynamicName(queryName, 0,1,  parameters);
        List<Object[]> data = pageBean.getData();
        if (data!=null && !data.isEmpty()){
            for (Object[] objects:data){
                String name =objects[0].toString();
                String dutyName =objects[1].toString();
            }
        }
    }


    public void excuteByDynamicName(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id","111");
        int i = baseDao.executeByDynamicName("emp.deleteEmp", parameters);
        if (i>=0){
            System.out.println("success");
        }
    }
}
