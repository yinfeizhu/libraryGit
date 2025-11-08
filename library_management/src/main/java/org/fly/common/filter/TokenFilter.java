package org.fly.common.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.fly.utils.CurrentHolder;
import org.fly.utils.JwtUtils;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("doFilter拦截到了请求");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取请求路径
        String requestURI = request.getRequestURI();// URI是资源路径，例如/emp/list
        //2.判断请求路径是否包含login，register如果包含则放行
        if (requestURI.contains("login")) {
            log.info("登录操作，放行");
            filterChain.doFilter(request, response);
            return;
        }else if (requestURI.contains("register")) {
            log.info("注册操作，放行");
            filterChain.doFilter(request, response);
            return;
        } else if (// 放行静态资源路径
            requestURI.contains("coverImage") ||
            requestURI.contains("/image")||
            requestURI.contains("/css") ||
            requestURI.contains("/js")||
            requestURI.contains("/favicon.ico")) {
            log.info("静态资源请求，放行");
            filterChain.doFilter(request, response);
            return;
        }
        //3.判断请求路径是否包含token，如果有则获取 token
        String token = request.getHeader("token");
        //4.判断token存在，如果不存在则返回错误信息401
        if (token == null|| token.isEmpty()) {
            log.info("请求未携带token，返回401");
            response.setStatus(401);
            return;
        }
        //5.如果token存在，则解析token，获取员工信息，校验失败则返回错误信息401
        try {
            Claims claims = JwtUtils.parseToken(token);
            Integer userId = Integer.valueOf(claims.get("id").toString());
            CurrentHolder.setCurrentUserId(userId);
        } catch (Exception e) {
            log.info("令牌不合法，返回401");
            response.setStatus(401);
            return;
        }
        //6.校验通过，放行
        log.info("令牌合法，放行");
        filterChain.doFilter(request, response);
        CurrentHolder.remove();
    }
}
