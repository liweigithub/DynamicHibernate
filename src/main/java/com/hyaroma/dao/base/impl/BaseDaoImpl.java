package com.hyaroma.dao.base.impl;


import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import com.hyaroma.dao.base.Condition;
import com.hyaroma.dao.base.IBaseDao;
import com.hyaroma.dao.base.PageBean;
import com.hyaroma.dao.hibernate.DynamicStatementBuilderImpl;
import com.hyaroma.dao.hibernate.IDynamicStatementBuilder;
import com.hyaroma.dao.hibernate.StatementTemplate;
import com.hyaroma.enums.OrderType;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author  wstv
 */
@Repository("baseDaoImpl")
public class BaseDaoImpl extends HibernateDaoSupport implements IBaseDao {
    private static final String REG = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
            + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
    private static   Pattern sqlPattern = Pattern.compile(REG, Pattern.CASE_INSENSITIVE);
    protected Map<String, StatementTemplate> templateCache;//缓存相应的模板信息

    @Resource(name="dynamicStatementBuilderImpl")
    protected IDynamicStatementBuilder dynamicStatementBuilder;

    @Resource(name = "sessionFactory")
    public void setSuperSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }

    @Override
    public Session getSession() {
        try {
            return super.getSessionFactory().getCurrentSession();
        } catch (Exception e) {
            return super.getSessionFactory().openSession();
        }
    }
    /**
     *初始化相关模板信息数据
     */
    @PostConstruct
    public  void customerAfterPropertiesSet()    {
        try {
            templateCache = new HashMap<String, StatementTemplate>();
            if(this.dynamicStatementBuilder == null){
                this.dynamicStatementBuilder = new DynamicStatementBuilderImpl();
            }
            dynamicStatementBuilder.init();
            Map<String,String> namedHQLQueries = dynamicStatementBuilder.getNamedHQLQueries();
            Map<String,String> namedSQLQueries = dynamicStatementBuilder.getNamedSQLQueries();
            Configuration configuration = new Configuration();
            configuration.setNumberFormat("#");
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            for(Map.Entry<String, String> entry : namedHQLQueries.entrySet()){
                stringLoader.putTemplate(entry.getKey(), entry.getValue());
                templateCache.put(entry.getKey(), new StatementTemplate(StatementTemplate.TYPE.HQL,new Template(entry.getKey(),new StringReader(entry.getValue()),configuration)));
            }
            for(Map.Entry<String, String> entry : namedSQLQueries.entrySet()){
                stringLoader.putTemplate(entry.getKey(), entry.getValue());
                templateCache.put(entry.getKey(), new StatementTemplate(StatementTemplate.TYPE.SQL,new Template(entry.getKey(),new StringReader(entry.getValue()),configuration)));
            }
            configuration.setTemplateLoader(stringLoader);
        }catch (Exception e){
            throw new BeanInitializationException("Initialization of BaseDAO failed", e);
        }
    }


    @Override
    public int queryIntByHQL(String hql) {
        List li = getSession().createQuery(hql).list();
        if (li == null || li.isEmpty()) {
            return 0;
        } else {
            if (li.get(0) == null) {
                return 0;
            }
            return ((Long) li.get(0)).intValue();
        }
    }

    @Override
    public int queryIntBySQL(String SQL) {
        return ((java.math.BigInteger) getSession().createNativeQuery(SQL).uniqueResult()).intValue();
    }

    @Override
    public long queryLongByHQL(String hql) {
        List li = getSession().createQuery(hql).list();
        if (li == null || li.isEmpty()) {
            return 0;
        } else {
            if (li.get(0) == null) {
                return 0;
            }
            return ((Long) li.get(0)).longValue();
        }
    }

    @Override
    public long queryLongBySQL(String sql) {
        List li = getSession().createNativeQuery(sql).list();
        if (li == null || li.isEmpty()) {
            return 0;
        } else {
            if (li.get(0) == null) {
                return 0;
            }
            return ((BigDecimal) li.get(0)).longValue();
        }
    }

    @Override
    public double queryDoubleByHQL(String HQL) {
        Object obj = getSession().createQuery(HQL).uniqueResult();
        if (obj == null) {
            return 0;
        } else {
            return (Double) obj;
        }

    }

    @Override
    public double queryDoubleBySQL(String SQL) {
        Object obj = getSession().createNativeQuery(SQL).uniqueResult();
        if (obj == null) {
            return 0;
        } else {
            if(obj instanceof Double){
                return (Double) obj;
            }else{
                return ((BigDecimal) obj).doubleValue();
            }

        }
    }

    @Override
    public <D> D findById(Class<D> clazz, long id) {
        try {
            D instance = (D) getSession().get(clazz, id);
            return instance;
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public <D> Object  save(D transientInstance) {
        Serializable save = getSession().save(transientInstance);
        return  save;
    }


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
     *
     */
    @Override
    public <D> PageBean<D> findPage(Class<D> clazz, ArrayList<Condition> conditions, LinkedHashMap<String, OrderType> orderBys, int pageIndex, int pageSize) {
        String hql = "from "+clazz.getName()+" where 1=1 ";
        //from Dommain where xx=1 and xxx=2
        StringBuilder builder =new StringBuilder(hql);
        if (conditions!=null && !conditions.isEmpty()){
            int i = 0;
            for(Condition condition:conditions){

                if(isValid(condition.getValue().toString())){//校验是否sql 注入
                    builder.append(condition.buildHql());//不带 order by 的hql 语句
                }
                i++;
            }
        }
        //查询总记录数
        int count = this.queryIntByHQL("select count(*) " + builder.toString());
        //拼装order 排序条件
        if (orderBys !=null && !orderBys.isEmpty()) {
            Iterator orderBysIt = orderBys.entrySet().iterator();
            builder.append(" order by ");
            while (orderBysIt.hasNext()) {
                Map.Entry entry = (Map.Entry) orderBysIt.next();
                String columns = (String) entry.getKey();
                OrderType orderType = (OrderType) entry.getValue();
                builder.append(columns + " " + orderType.getCode() + ",");
            }
        }
        hql= builder.toString();
        if (orderBys !=null && !orderBys.isEmpty()) {
            hql = hql.substring(0,hql.length()-1);//切掉最后一个","分隔符 获得完整hql 语句
        }
        Query query = getSession().createQuery(hql);
        query.setFirstResult((pageIndex-1)*pageSize);
        query.setMaxResults(pageSize);
        List list = query.list();
        PageBean<D> bean = new PageBean<D>(pageIndex,pageSize,count);
        bean.setData(list);
        return bean;
    }


    @Override
    public <D> List<D> findList(Class<D> clazz, ArrayList<Condition> conditions, LinkedHashMap<String, OrderType> orderBys) {
        String hql = "from "+clazz.getName()+" where 1=1 ";
        //from Dommain where xx=1 and xxx=2
        StringBuilder builder =new StringBuilder(hql);
        if (conditions!=null && !conditions.isEmpty()){
            for(Condition condition:conditions){
                if(isValid(condition.getValue().toString())){//校验是否sql 注入
                    builder.append(condition.buildHql());//不带 order by 的hql 语句
                }
            }
        }
        //拼装order 排序条件
        if (orderBys !=null && !orderBys.isEmpty()) {
            builder.append(" order by ");
            Iterator orderBysIt = orderBys.entrySet().iterator();
            while (orderBysIt.hasNext()) {
                Map.Entry entry = (Map.Entry) orderBysIt.next();
                String columns = (String) entry.getKey();
                OrderType orderType = (OrderType) entry.getValue();
                builder.append(columns + " " + orderType.getCode() + ",");
            }
        }
        hql= builder.toString();
        if (orderBys !=null && !orderBys.isEmpty()) {
            hql = hql.substring(0,hql.length()-1);//切掉最后一个","分隔符 获得完整hql 语句
        }
        Query query = getSession().createQuery(hql);
        List<D> list = query.list();
        return list;
    }


    /**
     * 按sql查询对象列表.
     *
     * @param values 命名参数,按名称绑定.
     */
    @Override
    public List  queryBySQL(final String sql, final Object... values) {
        return createSQLQueryPage(sql,1,Integer.MAX_VALUE, values).list();
    }

    /**
     * 根据原生sql语句查询 entity必须为hibernate映射的实体
     * */
    @Override
    public List<Object> queryBySQL(String sql, Class entity) {
        return this.getSession().createNativeQuery(sql,entity).list();
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
    @Override
    public List<Map<String,Object>> queryBySQL(String sql, String[] fields) {
        return parseObjectToMap(this.getSession().createNativeQuery(sql).list(), fields);
    }
    /**
     * hql查询
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List queryByHQL(final String hql, final Object... values) {
        List list = createHQLQueryPage(hql,1,Integer.MAX_VALUE, values).list();
        return list;
    }


    @Override
    public List<Object> queryBySQL(String sql, Class clazz, String[] fields) {
        return parseObjectToList(this.getSession().createNativeQuery(sql).list(), clazz, fields);
    }


    /**
     *
     * @param queryName sql 或者 hql 的查询名称
     * @param fields   必需为查询返回的字段 而且添加顺序必须和查询返回字段顺序一致
     * @param parameters 查询参数
     * @return list<map>
     */
    @Override
    public PageBean<Map<String, Object>> queryByDynamicName(final String queryName,int pageIndex,int pageSize,final String[] fields,final Map<String, ?> parameters) {
        if( queryName==null || parameters==null || parameters.size()==0 ||fields==null||fields.length == 0) {
            return null;
        }
        StatementTemplate statementTemplate = templateCache.get(queryName);
        String statement = processTemplate(statementTemplate,parameters);
        List list = new ArrayList();
        int dataCount = 0;
        if(statementTemplate.getType() == StatementTemplate.TYPE.HQL){
            list = this.parseObjectToMap(createHQLQueryPage(statement, pageIndex, pageSize).list(), fields);
            if (pageSize != Integer.MAX_VALUE){
                dataCount = this.queryIntByHQL(convertToCountQl(statement));
            }
        }
        else{
          list = this.parseObjectToMap(createSQLQueryPage(statement, pageIndex, pageSize).list(),fields);
            if (pageSize != Integer.MAX_VALUE && pageSize != 0){
                dataCount  = this.queryIntBySQL(convertToCountQl(statement));
            }
        }
        PageBean<Map<String, Object>> pageBean = new PageBean<Map<String, Object>>(pageIndex,pageSize,dataCount);
        pageBean.setData(list);
        return pageBean;
    }

    /**
     * 命名查询
     * @param queryName sql或者hql查询名称
     * @param clazz  指定entity 返回特定的entity
     * @param fields entity字段 必须和 entity保持一致 必需为查询返回的字段
     * @param parameters 查询参数
     * @return 特定的entitylist 集合
     */
    @Override
    public PageBean<Object> queryByDynamicName(final String queryName,int pageIndex,int pageSize,Class clazz, final String[] fields,final Map<String, ?> parameters) {
        if( queryName==null || clazz==null || fields==null || fields.length ==0  || parameters==null || parameters.size()==0 ) {
            return null;
        };
        StatementTemplate statementTemplate = templateCache.get(queryName);
        String statement = processTemplate(statementTemplate,parameters);
        int dataCount = 0;
        List list = new ArrayList();
        if(statementTemplate.getType() == StatementTemplate.TYPE.HQL){
            if (pageSize != Integer.MAX_VALUE){
                dataCount = this.queryIntByHQL(convertToCountQl(statement));
            }
            list =  this.parseObjectToList(createHQLQueryPage(statement, pageIndex, pageSize).list(), clazz, fields);
        }
        else{
            if (pageSize != Integer.MAX_VALUE){
                dataCount = this.queryIntBySQL(convertToCountQl(statement));
            }
            list = this.parseObjectToList(createSQLQueryPage(statement, pageIndex, pageSize).list(), clazz, fields);
        }
        PageBean<Object> pageBean = new PageBean<Object>(pageIndex,pageSize,dataCount);
        pageBean.setData(list);
        return pageBean;
    }

 //-------------------private info ------------------------------

    /**
     * 将ql 转换
     * @param ql
     * @return
     */
    private String convertToCountQl(String ql){
        String  []replaceStr=new String[]{"from","order","group"};
        String countQl ="";
        for(String str:replaceStr){
            countQl = new String(ql.replaceAll(str,str.toUpperCase()));
            ql = countQl;
        }
        countQl = countQl.substring(countQl.indexOf("FROM"));
        if (countQl.contains("GROUP")){
            //before：　from xxx where 1=1 GROUP BY  xxx ORDER BY xxx  　after :from xxx where 1=1
            countQl = countQl.substring(0, countQl.lastIndexOf("GROUP"));
        }
        if (countQl.contains("ORDER")){
            //before：　from xxx where 1=1 ORDER BY  XX　after :from xxx where 1=1
            countQl = countQl.substring(0, countQl.lastIndexOf("ORDER"));
        }
        return "select COUNT(*) "+countQl;
    }
    /*
    * 参数sql注入校验
    */
    private static boolean isValid(String str) {
        if (sqlPattern.matcher(str).find()) {
            return false;
        }
        return true;
    }

    /**
     * 将查询到的对象数组转换为 list map 集合
     */
    private List parseObjectToMap(List dataList, String[] fields) {
        if (dataList == null || dataList.size() == 0 || fields == null || fields.length == 0)
        {
            return Collections.emptyList();
        }
        try {
            List list = new ArrayList();
            if (dataList != null && dataList.size() > 0) {
                for (int i = 0; i < dataList.size(); i++) {
                    Map map = new HashMap();
                    if(! dataList.get(i).getClass().isArray() ){
                        for (int j = 0; j < fields.length; j++) {
                            map.put(fields[j], dataList.get(i));
                        }
                    }else{
                        Object[] dataObj = (Object[]) dataList.get(i);
                        for (int j = 0; j < fields.length; j++) {
                            String currentValue = null;
                            try {
                                currentValue = dataObj[j] + "";
                            } catch (Exception e) {
                                currentValue = null;
                            }
                            if (fields.length > j && fields[j] != null) {
                                map.put(fields[j], currentValue);
                            }
                        }
                    }
                    list.add(map);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将查询到的对象数组转换为,转换为List<clazz>集合
     * */
    private List parseObjectToList(List dataList, Class clazz, String[] fields) {
        if(dataList==null||dataList.size()==0||clazz==null||fields==null||fields.length==0)    return Collections.emptyList();;
        try {
            List list = new ArrayList();
            if (dataList != null && dataList.size() > 0) {
                for (int i = 0; i < dataList.size(); i++) {
                    Object clazzObj = clazz.newInstance();
                    if(! dataList.get(i).getClass().isArray() ){
                        Field field = clazz.getDeclaredField(fields[0]);
                        field.setAccessible(true);
                        Object currentValue = null;
                        try {
                            currentValue = dataList.get(i);
                        } catch (Exception e) {
                            currentValue = null;
                        }
                        this.setFiled(clazzObj, field, currentValue);
                    }else{
                        Object[] dataObj = (Object[]) dataList.get(i);
                        for (int j = 0; j < fields.length; j++) {
                            Field field = clazz.getDeclaredField(fields[j]);
                            field.setAccessible(true);
                            Object currentValue = null;
                            try {
                                currentValue = dataObj[j];
                            } catch (Exception e) {
                                currentValue = null;
                            }
                            this.setFiled(clazzObj, field, currentValue);
                        }
                    }
                    list.add(clazzObj);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setFiled(Object clazzObj, Field field, Object value){
        if(clazzObj==null||field==null) {
            return;
        }
        try{
            if (field.getGenericType().toString().equals("class java.lang.String")) {
                field.set(clazzObj, value + "");
            } else if (field.getGenericType().toString().equals("class java.lang.Integer")) {
                field.set(clazzObj, value == null ? null: Integer.parseInt(value + ""));
            } else if (field.getGenericType().toString().equals("class java.lang.Double")) {
                field.set(clazzObj, value == null ? null: Double.parseDouble(value + ""));
            } else if (field.getGenericType().toString().equals("class java.util.Date")) {
                field.set(clazzObj,value == null ? null: new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.toString()));
            } else if (field.getGenericType().toString().equals("class java.lang.Short")) {
                field.set(clazzObj, value == null ? null: Short.parseShort(value.toString()));
            } else {
                field.set(clazzObj, value);
            }
        }catch(Exception e ){
            e.printStackTrace();
        }
    }

    private  String processTemplate(StatementTemplate statementTemplate,Map<String, ?> parameters){
        StringWriter stringWriter = new StringWriter();
        try {
            Template template = statementTemplate.getTemplate();
            template.setClassicCompatible(true);//默认情况变量为null则替换为空字符串，如果需要自定义，写上${empty!"EmptyValue of fbysss"}的形式即可
            template.process(parameters, stringWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    /**
     * 根据查询SQL与参数列表创建Query对象.
     * 与find()函数可进行更加灵活的操作.
     * @param sqlQueryString sql语句
     * @param values 数量可变的参数,按顺序绑定.
     */
    private NativeQuery createSQLQueryPage(final String sqlQueryString, int pageIndex,int pageSize,final Object... values) {
        NativeQuery query = this.getSession().createNativeQuery(sqlQueryString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        //避免需要自己实现limit语句 传0代表手动实现limit分页
        if (pageSize!=0){
            query.setFirstResult((pageIndex-1)*pageSize);
            query.setMaxResults(pageSize);
        }
        return query;
    }


    /**
     * 根据查询HQL与参数列表创建Query对象.
     * 与find()函数可进行更加灵活的操作.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    private Query createHQLQueryPage(final String queryString,int pageIndex,int pageSize,final Object... values) {
        Query query = this.getSession().createQuery(queryString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        query.setFirstResult((pageIndex-1)*pageSize);
        query.setMaxResults(pageSize);
        return query;
    }

    /**
     * 删除某个对象
     * @param persistentInstance
     */
    @Override
     public <D>   void delete(D persistentInstance) throws HibernateException {
        getSession().delete(persistentInstance);
    }



    /**
     * 删除或者更新
     * @param queryName sql或者hql查询名称
     * @param parameters 查询参数
     * @return 影响行行数
     */
    @Override
    public int executeByDynamicName(final String queryName,final Map<String, ?> parameters) {
        if( queryName==null ||  parameters==null || parameters.size()==0 ) {
            return -1;
        }
        StatementTemplate statementTemplate = templateCache.get(queryName);
        String statement = processTemplate(statementTemplate,parameters);
        if(statementTemplate.getType() == StatementTemplate.TYPE.HQL){
            Query query = this.getSession().createQuery(statement);
           return  query.executeUpdate();
        }
        else{
            Query query = this.getSession().createNativeQuery(statement);
            return  query.executeUpdate();
        }
    }

    @Override
    public int queryIntByDynamicName(final String queryName,final Map<String, ?> parameters) {
        if( queryName==null ||  parameters==null || parameters.size()==0 ) {
            return -1;
        }
        StatementTemplate statementTemplate = templateCache.get(queryName);
        String statement = processTemplate(statementTemplate,parameters);
        if(statementTemplate.getType() == StatementTemplate.TYPE.HQL){
            return  queryIntByHQL(statement);
        }
        else{
            return  queryIntBySQL(statement);
        }
    }

    @Override
    public String getStatement(final String queryName,final Map<String, ?> parameters){
        if( queryName==null ||  parameters==null || parameters.size()==0 ) {
            return "";
        }
        StatementTemplate statementTemplate = templateCache.get(queryName);
        String statement = processTemplate(statementTemplate,parameters);
        return statement;
    }
}
