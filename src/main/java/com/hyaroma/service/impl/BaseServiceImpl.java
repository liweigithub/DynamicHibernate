package com.hyaroma.service.impl;

import com.hyaroma.dao.utils.ReflectionUtils;
import com.hyaroma.dao.base.Condition;
import com.hyaroma.dao.base.IBaseDao;
import com.hyaroma.dao.base.PageBean;
import com.hyaroma.enums.OrderType;
import com.hyaroma.service.IBaseService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wstv
 * @param <T>
 */
public class BaseServiceImpl<T> implements IBaseService<T> {

    @Resource(name="baseDaoImpl")
    private IBaseDao baseDao;


    @Override
    public T findById(long id) {
        Class<T> clazz = ReflectionUtils.getSuperClassGenricType(getClass());
        T persisit = (T) baseDao.findById(clazz, id);
        return persisit;
    }


    /**
     * 根据原生sql语句进行查询,返回对象集合
     * String sql 原生sql语句
     *  Class clazz 为类 String[] fields
     * 必需为对象字段属性
     * 例子: Sql : select username as name ,userage as age from user;
     * User : private String name , private int age
     * clazz = User.class
     * fields ={ "name","age" }; //注意 sql和fields必需一致,并且fields为User的字段属性
     * 返回格式 list = { user1,user2,user3... }
     *
     * */
    public List findBySql(String sql, Class clazz, String[] fields) {
        return this.baseDao.queryBySQL(sql, clazz, fields);
    }


    /**
     * 根据原生sql语句进行查询,返回对象集合
     * String sql 原生sql语句
     * String[] fields 可以不为对象字段属性
     * 例子:
     * Sql : select username ,userage from user;
     * fields = { "name","age" };
     * 返回
     * map1 = { name="张三",age=30}; map2 = { name="李四",age=18};
     * list = { map1 ,map2}
     *
     * */
    public List<Map<String,Object>> findBySql(String sql, String[] fields) {
        return this.baseDao.queryBySQL(sql, fields);
    }


    /**
     * 根据原生sql语句查询 entity必须为hibernate映射的实体
     * */
    @Override
    public List findBySql(String sql, Class entity) {
        return this.baseDao.queryBySQL(sql, entity);
    }



    /**
     * 根据sql查询
     * */
    public List findBySql(String sql, Object... values) {
        return this.baseDao.queryBySQL(sql, values);

    }

    /**
     * 根据hql查询
     * */
    public List findByHql(String hql) {
        return this.baseDao.queryByHQL(hql);
    }

    /**
     * 按HQL查询对象列表.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    @Override
    public List findByHQL(String hql, Object... values) {
        return this.baseDao.queryByHQL(hql, values);
    }


    @Override
    public Object saveOrUpdate(T t) {
        return baseDao.save(t);
    }

    @Override
    public void delete(T t){
        baseDao.delete(t);
    }

    @Override
    public PageBean<T> findPage(ArrayList<Condition> conditions, LinkedHashMap<String, OrderType> orderBys, int pageIndex, int pageSize) {
        Class<T> clazz = ReflectionUtils.getSuperClassGenricType(getClass());
        PageBean<T> page = baseDao.findPage(clazz,conditions,orderBys,pageIndex, pageSize);
        return page;
    }

    @Override
    public List<T> findList(ArrayList<Condition> conditions, LinkedHashMap<String, OrderType> orderBys) {
        Class<T> clazz = ReflectionUtils.getSuperClassGenricType(getClass());
        List<T> list = baseDao.findList(clazz,conditions,orderBys);
        return list;
    }

    @Override
    public PageBean<Object> findByNamedQuery(String queryName,int pageIndex,int pageSize,
                                             Class clazz, String[] fields, Map<String, ?> parameters) {
        return this.baseDao.queryByDynamicName(queryName, pageIndex,pageSize,clazz, fields, parameters);
    }



    @Override
    public PageBean<Map<String, Object>> findByNamedQuery(String queryName,int pageIndex,int pageSize,
                                                          String[] fields, Map<String, ?> parameters) {
        return this.baseDao.queryByDynamicName(queryName,pageIndex,pageSize, fields, parameters);
    }

    @Override
    public int executeByDynamicName(String queryName, Map<String, ?> parameters) {
        return baseDao.executeByDynamicName(queryName,parameters);
    }
}
