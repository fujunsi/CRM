package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户控制器");
        String path = request.getServletPath();
        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if ("/settings/user/xxx.do".equals(path)){

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入验证登录操作");
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码明文形式转换为MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        //接收ip地址
        String ip = request.getRemoteAddr();
        System.out.println(ip);

        //未来的业务层开发，统一使用代理类形态的接口对象
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try{
            User user = service.login(loginAct,loginPwd,ip);

            request.getSession().setAttribute("user",user);

            //如果程序执行到此处，说明业务层没有为controller抛出任何异常
            //表示登陆成功
            //{"success":true}
            /*String str = "{\"success\":true}";
            response.getWriter().print(str);*/

            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            e.printStackTrace();

            //如果程序执行到此处,说明业务层登陆失败，为controller抛出异常
            //表示登陆失败
            String msg = e.getMessage();
            /*
                我们现在作为controller，需要为ajax请求提供多项信息
                可以有两种手段来处理：
                    （1）将多项信息打包成map，将mao解析为json串
                    （2）创建一个vo类
                        private boolean success;
                        private String msg;

                    如果对于现在展示的信息将来还会用大量的使用，我们创建一个vo类，使用方便
                    如果对于现在展示的信息只有这个需求能够使用，我们使用map就可以
             */
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);
        }

    }
}