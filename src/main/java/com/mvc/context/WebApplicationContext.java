package com.mvc.context;

import com.mvc.annotation.Autowired;
import com.mvc.annotation.Controller;
import com.mvc.annotation.Service;
import com.mvc.exception.ContextException;
import com.mvc.xml.XmlParser;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * @Program: simplify_mvc
 * @Description: 上下文（spring）
 * @Author: ZJamss
 * @Create: 2022-07-31 13:56
 **/
public class WebApplicationContext {

    //classpath:mvc.xml
    private String contextConfigLocation;
    private List<String> classNames = new ArrayList<>();

    public Map<String, Object> ioc = new HashMap<>();

    //ioc容器
    public WebApplicationContext(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    //初始化
    public void init() {
        //1.解析mvc.xml dom4j
        final String basePackage = XmlParser.getBasePackage(contextConfigLocation.split(":")[1]);
        final String[] basePackages = basePackage.split(",");
        //扫描完后的类列表：[com.business.controller.UserController, com.business.service.impl.UserServiceImpl, com.business.service.UserService]
        if (basePackages.length > 0) {
            for (String pkg : basePackages) {
                executeScanPackage(pkg);
            }
        }
        //实例化bean
        executeInstance();
        //spring容器中对象注入
        executeAutowired();
    }

    //扫描包
    public void executeScanPackage(String pkg) {
        // "com.business.controller" -> "/com/business/controller"
        final URL url = this.getClass().getClassLoader().getResource("/" + pkg.replaceAll("\\.", "/"));
        String path = url.getFile();
        File dir = new File(path);
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                //如果文件夹，递归扫描
                executeScanPackage(pkg + "." + f.getName());
            } else {
                //文件全路径 com.business.controller.UserController
                String className = pkg + "." + f.getName().replaceAll(".class", "");
                classNames.add(className);
            }
        }
    }

    private void executeInstance() {
        if (classNames.size() == 0) {
            //没有实例化的类
            throw new ContextException("没有要需要实例化的类");
        }
        try {
            for (String className : classNames) {
                final Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    //控制层的类
                    //UserController -> userController
                    final String beanName = clazz.getSimpleName()
                            .substring(0, 1)
                            .toLowerCase(Locale.ROOT) +
                            clazz.getSimpleName().substring(1);
                    ioc.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    //服务层类
                    //UserServiceImpl -> userService
                    Service serviceAnnotation = clazz.getDeclaredAnnotation(Service.class);
                    String beanName = serviceAnnotation.value();
                    if ("".equals(beanName)) {
                        final Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> i : interfaces) {
                            ioc.put(i.getSimpleName().substring(0, 1)
                                            .toLowerCase(Locale.ROOT) +
                                            i.getSimpleName().substring(1),
                                    clazz.newInstance());
                        }
                    } else {
                        ioc.put(beanName, clazz.newInstance());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //spring容器依赖注入
    private void executeAutowired() {
        if (ioc.isEmpty()) {
            throw new ContextException("没有找到初始化完成的bean对象");
        }
        try {
            for (Map.Entry<String, Object> entry : ioc.entrySet()) {
                final String key = entry.getKey();
                final Object bean = entry.getValue();
                final Field[] fields = bean.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                        String beanName = autowiredAnnotation.value();
                        if ("".equals(beanName)) {
                            Class<?> type = field.getType();
                            beanName = type.getSimpleName().substring(0, 1)
                                    .toLowerCase(Locale.ROOT) +
                                    type.getSimpleName().substring(1);
                        }
                        field.setAccessible(true);
                        //@Autowired属性注入
                        field.set(bean,ioc.get(beanName));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
