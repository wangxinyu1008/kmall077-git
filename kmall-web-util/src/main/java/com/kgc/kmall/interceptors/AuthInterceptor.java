package com.kgc.kmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.util.CookieUtil;
import com.kgc.kmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shkstart
 * @create 2021-01-12 15:27
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //判断是否是HandlerMethod，因为访问静态资源时handler是ResourceHttpRequestHandler
        if(handler.getClass().equals(HandlerMethod.class)){
            //获取注解信息
            HandlerMethod handlerMethod=(HandlerMethod)handler;
            LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
            // 没有LoginRequired注解不拦截
            if (methodAnnotation != null) {
                String token="";
                String oldToken  = CookieUtil.getCookieValue(request, "oldToken", true);
                if(StringUtils.isNotBlank(oldToken)){
                    token=oldToken ;
                }
                String newToken = request.getParameter("token");
                if(StringUtils.isNotBlank(newToken)){
                    token=newToken;
                }
                //判断methodAnnotation的value属性值
                boolean value = methodAnnotation.value();
                String result="fail";
                Map<String,String> successMap = new HashMap<>();
               if(StringUtils.isNotBlank(token)){
                   String ip=request.getRemoteAddr();
                   if(StringUtils.isNotBlank(ip)||ip.equals("0:0:0:0:0:0:0:1")){
                       ip="127.0.0.1";
                   }
                   String successJson = HttpclientUtil.doGet("http://passport.kmall.com:8087/verify?token=" + token+"&currentIp="+ip);
                   successMap= JSON.parseObject(successJson, Map.class);
                   result=successMap.get("status").toString();
               }
                if(value){
                    //必登录，无效返回false，跳转login
                    if(result.equals("success")==false){
                        //重定向会passport登录
                        StringBuffer requestURL = request.getRequestURL();
                        response.sendRedirect("http://localhost:8087/index?ReturnUrl="+requestURL);
                        return false;
                    }
                }
                //判断是否登录，如果登陆把memberId存到request，更新cookie
                //如果登陆成功
                if(result.equals("success")){
                    request.setAttribute("memberId",successMap.get("memberId"));
                    request.setAttribute("nickname",successMap.get("nickname"));
                    //保存cookie
                    if(StringUtils.isNotBlank(token)){
                        CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
                    }
                    return true;
                }
            }
        }
        return true;
    }
}
