package com.hyaroma.service;



import com.hyaroma.dao.base.Condition;
import com.hyaroma.dao.base.PageBean;
import com.hyaroma.enums.OrderType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础服务
 */
public interface IBaseService<T>{
        T findById(String id);
        T findById(int id);
        Object saveOrUpdate(T t);
        void delete(T t);
        /**
         * 示例：
         *  ArrayList<Condition> conditions = new ArrayList<Condition>();
         *  该条件为 并且 = 条件   where 1=1 and deliveryStationId = 1111
         *  conditions.add(new Condition("deliveryStationId", 11111, Op.EQ, OpType.AND));
         *  该条件为列组合or条件  and 右包含条件 where 1=1 and (address like '四川%')
         *  conditions.add(new Condition("address,name", "四川", Op.LIKE_RIGHT, OpType.GROUP_OR_AND)  );
         *  LinkedHashMap<String, OrderType > orderBys =  new LinkedHashMap<String, OrderType >();
         *  orderBys.put("code", OrderType.ASC);
         *  int pageIndex = 1;
         *  int pageSize = 20;
         *  PageBean<Domain> page = this.findPage(conditions, orderBys, pageIndex, pageSize);
         * @param conditions 条件
         * @param orderBys 排序条件
         * @param pageIndex 页码值
         * @param pageSize 每页显示多少条
         * @return
         */
        PageBean<T> findPage(ArrayList<Condition> conditions, LinkedHashMap<String, OrderType> orderBys, int pageIndex, int pageSize);


        List<T> findList(ArrayList<Condition> conditions, LinkedHashMap<String, OrderType> orderBys);


        /**
         * 根据原生sql语句查询 entity必须为hibernate映射的实体
         */
        List findBySql(String sql, Class entity);


        /**
         * 命名查询
         * @param queryName sql或者hql查询名称
         * @param clazz  指定entity 返回特定的entity
         * @param fields 字段 必须和 entity保持一致 必需为查询返回的字段 顺序必须和 查询返回的顺序一样
         * @param parameters 查询参数
         * @return 特定的entitylist 集合
         */
        PageBean<Object> findByNamedQuery(String queryName, int pageIndex, int pageSize,
                                          Class clazz, String[] fields, Map<String, ?> parameters);


        /**
         *
         * @param queryName sql 或者 hql 的查询名称
         * @param fields   为查询返回的字段名字可以和查询返回的不一样 但是顺序必须和返回字段顺序一样
         * @param parameters 查询参数
         * @return list<map>
         */
       PageBean<Map<String, Object>> findByNamedQuery(String queryName, int pageIndex, int pageSize,
                                                      String[] fields, Map<String, ?> parameters);



}

