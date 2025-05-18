package com.hdu.hdufpga.config;

//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//@Slf4j
//public class CorsFilter implements Filter {
//
//    static final String OPTIONS = "OPTIONS";
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
//            throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        // 获取前端的 Origin
//        String origin = request.getHeader("Origin");
//        if (origin != null) {
//            response.setHeader("Access-Control-Allow-Origin", origin);
//        }
//
//        // 允许所有请求方法
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
//
//        // 允许的请求头
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, satoken, Content-Type, Authorization");
//
////        // 允许前端访问的自定义响应头
////        response.setHeader("Access-Control-Expose-Headers", "uuid");
//
//        // 允许携带 Cookie
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//
//        // 预检请求
//        if (OPTIONS.equals(request.getMethod())) {
//            log.info("浏览器发送了 OPTIONS 预检请求");
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.getWriter().write("OK");
//            response.getWriter().flush();
//            return;
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//}
