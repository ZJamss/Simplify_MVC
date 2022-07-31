package com.mvc.servlet;

import com.google.gson.Gson;
import com.mvc.annotation.Controller;
import com.mvc.annotation.RequestMapping;
import com.mvc.annotation.RequestParam;
import com.mvc.annotation.ResponseBody;
import com.mvc.context.WebApplicationContext;
import com.mvc.exception.ContextException;
import com.mvc.handler.Handler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Program: simplify_mvc
 * @Description: 请求转发器
 * @Author: ZJamss
 * @Create: 2022-07-31 13:24
 **/
//@WebServlet("/")
public class DispatchServlet extends HttpServlet {

    private WebApplicationContext webApplicationContext;
    //url和类对象方法映射
    private List<Handler> handlers = new ArrayList<Handler>();

    @Override
    public void init() {
        //servlet初始化读取 classpath:mvc.xml配置参数
        String contextConfigLocation = this.getServletConfig().getInitParameter("contextConfigLocation");
        //创建spring容器
        webApplicationContext = new WebApplicationContext(contextConfigLocation);
        //初始化容器
        webApplicationContext.init();
        //初始化请求映射  /user/query -> Controller -> method -> parameter
        initHandlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //转到doPost()分发
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //请求分发处理
        executeDispatch(req, resp);
    }


    private void initHandlerMapping() {
        if (webApplicationContext.ioc.isEmpty()) {
            throw new ContextException("SpringIOC容器为空");
        }
        for (Map.Entry<String, Object> entry : webApplicationContext.ioc.entrySet()) {
            final Class<?> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                final Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
                        // user/query
                        String url = requestMappingAnnotation.value();
                        handlers.add(new Handler(url, method, entry.getValue()));
                    }
                }
            }
        }
    }

    /*
     * 请求分发处理
     * */
    private void executeDispatch(HttpServletRequest request, HttpServletResponse response) {
        Handler handler = getHandler(request);
        try {
            if (handler == null) {
                response.getWriter().print("<h1>404 NOT FOUND</h1>");
            }
            final Method method = handler.getMethod();
            //所需参数列表
            final Parameter[] parameters = method.getParameters();
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Map<String, String[]> parameterMap = request.getParameterMap();
            //invoke参数列表
            Object[] params = new Object[parameters.length];

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue()[0];
                //若有@RequestParam注解
                int index = isRequestParamAnnotationPresent(method, name);
                if (index != -1) {
                    params[index] = value;
                } else {
                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].getName().equals(name)) {
                            params[i] = value;
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < params.length; i++) {
                if (params[i] == null) {
                    if (parameterTypes[i] == HttpServletRequest.class)
                        params[i] = request;
                    else if (parameterTypes[i] == HttpServletResponse.class)
                        params[i] = response;
                    else {
                        response.getWriter().print("<h1>400 BAD REQUEST</h1>");
                        return;
                    }
                }
            }
            //调用控制器内的方法
            final Object result = method.invoke(handler.getController(), params);
            //返回方式
            final boolean responseBody = method.isAnnotationPresent(ResponseBody.class);
            if (responseBody) {
                response.setContentType("application/json;charset=UTF-8");
                final PrintWriter writer = response.getWriter();
                String json = new Gson().toJson(result);
                writer.print(json);
                writer.flush();
                writer.close();
            } else {
                if (result instanceof String) {
                    String view = (String) result;
                    if (view.contains(":")) {
                        final String viewType = view.split(":")[0];
                        final String viewPath = view.split(":")[1];
                        switch (viewType) {
                            case "forward":
                                request.getRequestDispatcher(viewPath).forward(request, response);
                                break;
                            case "redirect":
                                response.sendRedirect(viewPath);
                                break;
                        }
                    } else {
                        //默认转发
                        request.getRequestDispatcher(view).forward(request, response);
                    }
                }
            }

        } catch (IOException | InvocationTargetException | IllegalAccessException | ServletException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     * @Author ZJamss
     * @Description 获取请求对应的handler
     * @Date 2022/7/31 18:26
     * @Param
     **/
    public Handler getHandler(HttpServletRequest request) {
        if (handlers.isEmpty()) {
            throw new ContextException("请求映射器列表为空");
        }
        String requestURI = request.getRequestURI();
        // /user/query
        for (Handler handler : handlers) {
            if (handler.getUrl().equals(requestURI)) {
                return handler;
            }
        }
        return null;
    }

    /**
     * 判断控制器方法参数是否有@RequestParam主角,返回参数下标
     **/
    public int isRequestParamAnnotationPresent(Method method, String param) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                String name = requestParam.value();
                if (param.equals(name)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
