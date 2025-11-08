package org.fly.filter;//package org.fly.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//
//@WebFilter(urlPatterns ="/*")
//@Slf4j
//public class DemoFilter implements Filter {
//    // 初始化方法, web服务器开启时只执行一次
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        log.info("init初始化DemoFilter");
////        Filter.super.init(filterConfig);
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        log.info("doFilter拦截到了请求");
//        // 放行
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    // 销毁方法, web服务器关闭时只执行一次
//    @Override
//    public void destroy() {
//        log.info("destroy销毁DemoFilter");
//       Filter.super.destroy();
//    }
//}
