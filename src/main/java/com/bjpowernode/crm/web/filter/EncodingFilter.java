package com.bjpowernode.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    public void doFilter(ServletRequest Req, ServletResponse Resp, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进入到过滤字符编码的过滤器");

        //过滤post请求中文参数乱码
        Req.setCharacterEncoding("UTF-8");
        //过滤响应流响应中文乱码
        Resp.setContentType("text/html;charset=UTF-8");

        //将请求放行
        filterChain.doFilter(Req,Resp);
    }
}
