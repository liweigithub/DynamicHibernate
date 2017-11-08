package com.hyaroma.dao.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;

/**
 * 继承
 * {@link org.springframework.context.support.FileSystemXmlApplicationContext} <br>
 * 主要用途是暴露 {@link #setBean(String, Object)} 方法, 可以向spring容器中动态注入bean实例 <br>
 * 
 * @author wstv
 * 
 */
public class FileSystemXmlApplicationContext extends
        org.springframework.context.support.FileSystemXmlApplicationContext {

    public FileSystemXmlApplicationContext() {
        super();
    }

    public FileSystemXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public FileSystemXmlApplicationContext(String... configLocations) throws BeansException {
        super(configLocations);
    }

    public FileSystemXmlApplicationContext(String configLocation) throws BeansException {
        super(configLocation);
    }

    public FileSystemXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
        super(configLocations, parent);
    }

    public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
            throws BeansException {
        super(configLocations, refresh, parent);
    }

    public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        super(configLocations, refresh);
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
