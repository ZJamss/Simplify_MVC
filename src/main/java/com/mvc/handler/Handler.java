package com.mvc.handler;

import java.lang.reflect.Method;

/**
 * @Program: simplify_mvc
 * @Description:
 * @Author: ZJamss
 * @Create: 2022-07-31 18:05
 **/
public class Handler {
    private String url;
    private Method method;
    private Object controller;

    public Handler() {
    }

    public Handler(String url, Method method, Object controller) {
        this.url = url;
        this.method = method;
        this.controller = controller;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Handler{" +
                "url='" + url + '\'' +
                ", method=" + method +
                ", controller=" + controller +
                '}';
    }
}
