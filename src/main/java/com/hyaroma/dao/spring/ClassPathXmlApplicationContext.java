package com.hyaroma.dao.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;

/**
 * 继承 {@link org.springframework.context.support.ClassPathXmlApplicationContext} <br>
 * 主要用途是暴露 {@link #setBean(String, Object)} 方法, 可以向spring容器中动态注入bean实例 <br>
 * 
 * @author wstv
 * 
 */
@SuppressWarnings("rawtypes")
public class ClassPathXmlApplicationContext extends org.springframework.context.support.ClassPathXmlApplicationContext {

    public ClassPathXmlApplicationContext() {
        super();
    }

    public ClassPathXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }


    public ClassPathXmlApplicationContext(String path, Class clazz) throws BeansException {
        super(path, clazz);
    }

    public ClassPathXmlApplicationContext(String... configLocations) throws BeansException {
        super(configLocations);
    }

    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        super(configLocation);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
        super(configLocations, parent);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
            throws BeansException {
        super(configLocations, refresh, parent);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        super(configLocations, refresh);
    }

    public ClassPathXmlApplicationContext(String[] paths, Class clazz, ApplicationContext parent) throws BeansException {
        super(paths, clazz, parent);
    }

    public ClassPathXmlApplicationContext(String[] paths, Class clazz) throws BeansException {
        super(paths, clazz);
    }

    public void setBean(String beanName, Object obj) {
        ConfigurableListableBeanFactory configurableListableBeanFactory = this.getBeanFactory();

        // 放入单例对象
        configurableListableBeanFactory.registerSingleton(beanName, obj);

        // 依赖关系
        // configurableListableBeanFactory.registerDependentBean(beanName,
        // dependentBeanName);

        // 注册关闭需要销毁的单例对象
        if (obj instanceof DisposableBean && configurableListableBeanFactory instanceof SingletonBeanRegistry) {
            ((DefaultSingletonBeanRegistry) configurableListableBeanFactory).registerDisposableBean(beanName,
                    (DisposableBean) obj);
        }
    }

}
