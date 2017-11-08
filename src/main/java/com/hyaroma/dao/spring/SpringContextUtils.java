package com.hyaroma.dao.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * Spring的工具类，用来获得配置文件中的bean和手动添加bean <br>
 * 
 * 注: 使用此类的前提, 需要在applicationContext.xml文件中加入如下配置
 * <p>
 * &lt;bean id="springBeanUtil"
 * class="com.hyaroma.blog.spring.SpringContextUtils" /&gt;
 * 
 */
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    /***
     * 当继承了ApplicationContextAware类之后，那么程序在调用 getBean(String)的时候会自动调用该方法，不用自己操作
     */
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtils.applicationContext == null) {
            SpringContextUtils.applicationContext = applicationContext;
        } else {
            if (applicationContext instanceof WebApplicationContext) {
                SpringContextUtils.applicationContext = applicationContext;
            }
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /***
     * 根据一个bean的id获取配置文件中相应的bean
     * 
     * @param beanName
     * @return
     * @throws BeansException
     */
    public static Object getBean(String beanName) throws BeansException {
        return applicationContext.getBean(beanName);
    }

    /***
     * 类似于getBean(String beanName)只是在参数中提供了需要返回到的类型。
     * 
     * @param beanName
     * @param requiredType
     * @return
     * @throws BeansException
     */
    public static <T> T getBean(String beanName, Class<T> requiredType) throws BeansException {
        return (T) applicationContext.getBean(beanName, requiredType);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     * 
     * @param beanName
     * @return boolean
     */
    public static boolean containsBean(String beanName) {
        return applicationContext.containsBean(beanName);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     * 
     * @param beanName
     * @return boolean
     * @throws NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String beanName) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(beanName);
    }

    /**
     * @param beanName
     * @return Class 注册对象的类型
     * @throws NoSuchBeanDefinitionException
     */
    @SuppressWarnings("rawtypes")
    public static Class getType(String beanName) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(beanName);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     * 
     * @param beanName
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String beanName) throws NoSuchBeanDefinitionException {
        return applicationContext.getAliases(beanName);
    }

    /**
     * 向spring容器中动态注入bean实例 <br>
     * 前提是使用{@link org.fuwushe.common.spring.XmlWebApplicationContext} <br>
     * 或 {@link org.fuwushe.common.spring.FileSystemXmlApplicationContext} <br>
     * 或 {@link org.fuwushe.common.spring.ClassPathXmlApplicationContext} 类
     * 
     * @param beanName
     * @param obj
     */
    public static void setBean(String beanName, Object obj) {
        if (applicationContext instanceof XmlWebApplicationContext) {
            ((XmlWebApplicationContext) applicationContext).setBean(beanName, obj);
        } else if (applicationContext instanceof FileSystemXmlApplicationContext) {
            ((FileSystemXmlApplicationContext) applicationContext).setBean(beanName, obj);
        } else if (applicationContext instanceof ClassPathXmlApplicationContext) {
            ((ClassPathXmlApplicationContext) applicationContext).setBean(beanName, obj);
        }
    }
}
