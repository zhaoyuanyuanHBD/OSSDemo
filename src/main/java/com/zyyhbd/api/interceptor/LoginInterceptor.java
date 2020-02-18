package com.zyyhbd.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor{
	
	/**
	 * 拦截登录请求
	 */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object user = request.getSession().getAttribute("userId");

        if(user == null){

            //未登陆，返回登陆页面
            request.setAttribute("msg","请先登陆");

            long timestamp= System.currentTimeMillis();


            response.sendRedirect("http://47.96.187.200/profile/oauth2/authorize?client_id=dEAGYWIJZd" +
                    "&redirect_uri=http://127.0.0.1:8088/callback&oauth_timestamp="+String.valueOf(timestamp)+"&response_type=code");

            return false;
        }else{
            System.out.println(user+"------");
            //已登陆，放行请求
            return true;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


}
