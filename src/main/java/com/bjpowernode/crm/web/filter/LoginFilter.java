package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("进入到验证有没有登陆过的过滤器");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;


        //不应该拦截的资源
        String path = request.getServletPath();
        if ("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){
            filterChain.doFilter(req,resp);
        }else {

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            //如果user不为null，说明登陆过
            if (user!= null){
                filterChain.doFilter(req,resp);
            }else {
                //没有登录过
                // 重定向到登录页
                /*
                    重定向的路径
                    在实际项目开发中，对于路径的使用，不论操作的是前端还是后端，应该一律使用绝对路径
                    关于转发和重定向的写法如下：
                        转发：
                            使用的是一种特殊的绝对路径的使用，这种路径前面不加/项目名，这种路径也成为内部路径
                            /login.jsp
                        重定向:
                            使用的是传统的绝对路径写法，前面以/项目名开头，后面跟具体的资源路径

                    为什么使用重定向
                        转发之后，路径会停留在老路径上，而不是跳转之后最新资源的路径，
                        我们应该在为用户跳转到登录页的同时，将浏览器的地址栏应该自动设置为当前的登录路径

                 */
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        }
    }
}
