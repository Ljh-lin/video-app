package com.lin.interceptor;


import com.lin.utils.IMoocJSONResult;
import com.lin.utils.JsonUtils;
import com.lin.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MyInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;
    public static final String USER_REDIS_SESSION="user_redis_session";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

        String userId=request.getHeader("userId");
        String userToken=request.getHeader("userToken");

        if (StringUtils.isNotBlank(userId)&&StringUtils.isNotBlank(userToken)){
            String uniqueToken=redis.get(USER_REDIS_SESSION+":"+userId);
            if (StringUtils.isEmpty(uniqueToken)&&StringUtils.isBlank(uniqueToken)){
                returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("请登录..."));
                return false;
            }else {
                if (!uniqueToken.equals(userToken)){
                    returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("账号被挤掉"));
                    return false;
                }
            }

        }else {
            returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("请登录..."));
            return false;
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
     * 返回前端的json
     * @param response
     * @param result
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }

}
