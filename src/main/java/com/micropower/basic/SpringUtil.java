package com.micropower.basic;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Date: 2021/2/20 9:57
 * @Description: 注入工具类
 * @Author:Kohaku_川
 **/
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 通过实现ApplicationContextAware接口中的setApplicationContext方法，我们可以获取到spring操作上线文applicationContext变量，
     * 　　　* 然后把它复制给静态变量applicationContext，这样我们就可以通过MyApplicationContext.applicationContext.getBean()的方式取spring管理的bean。
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     */
    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}
