package com.aiocloud.gateway.center.base;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }

    public <T> T getBean(String name) {
        return (T) context.getBean(name);
    }

    public <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}

