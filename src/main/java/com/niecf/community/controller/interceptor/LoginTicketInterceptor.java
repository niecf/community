package com.niecf.community.controller.interceptor;

import com.niecf.community.entity.LoginTicket;
import com.niecf.community.entity.User;
import com.niecf.community.service.UserService;
import com.niecf.community.util.CookieUtil;
import com.niecf.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;


    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket= CookieUtil.getValue(request,"ticket");
        if(ticket!=null){
            LoginTicket loginTicket= userService.findLoginTicket(ticket);
            if(loginTicket!=null&&loginTicket.getStatus()==0&&loginTicket.getExpired().after(new Date())){
                User user=userService.findUserById(loginTicket.getUserId());
                hostHolder.setUsers(user);

            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user=hostHolder.getUsers();
        if(user!=null &&modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
