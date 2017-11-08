package com.hyaroma.dao.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;

/**
 * 继承 {@link org.springframework.web.context.support.XmlWebApplicationContext} <br>
 * 主要用途是暴露 {@link #setBean(String, Object)} 方法, 可以向spring容器中动态注入bean实例 <br>
 * 
 * 注: 如要使该方法生效必须在web.xml配置文件中加入如下配置
 * <p>
 * &lt;context-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &lt;param-name&gt;contextClass&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &lt;param-value&gt;com.lietou.common.spring
 * .XmlWebApplicationContext&lt;/param-value&gt;<br>
 * &lt;/context-param&gt;
 * 
 */
public class XmlWebApplicationContext extends org.springframework.web.context.support.XmlWebApplicationContext {

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
