package com.lin.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginInterceptor implements HandlerInterceptor {

    private List<String> unCheckUrls=Arrays.asList("/user/login");

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl=request.getRequestURI();
        requestUrl=requestUrl.replaceAll(request.getContextPath(),"");
        //判断是否针对匿名路径拦截，如果包含则表示匿名路径需要拦截，否则通过拦截器
        if (unCheckUrls.contains(requestUrl)){
            //包含公开url
            return true;
        }
        if (null==request.getSession().getAttribute("sessionUser")){
            response.sendRedirect(request.getContextPath()+"/user/login");
            return false;
        }
        return true;
    }
}
