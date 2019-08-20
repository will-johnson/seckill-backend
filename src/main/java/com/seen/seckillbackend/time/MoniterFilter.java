package com.seen.seckillbackend.time;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: zhangyasen
 * @Date: 2019/08/20
 */
@Slf4j
@Component
public class MoniterFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        long start = System.currentTimeMillis();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        String params = getQueryString(httpRequest);

        try {
            chain.doFilter(httpRequest, httpResponse);
        } finally {
            long cost = System.currentTimeMillis() - start;
            log.info("access url [{}{}], cost time [{}] ms", uri, params,
                    cost);
        }
    }

    private String getQueryString(HttpServletRequest req) {
        StringBuilder buffer = new StringBuilder("?");
        Enumeration<String> emParams = req.getParameterNames();
        try {
            while (emParams.hasMoreElements()) {
                String sParam = emParams.nextElement();
                String sValues = req.getParameter(sParam);
                buffer.append(sParam).append("=").append(sValues).append("&");
            }
            return buffer.substring(0, buffer.length() - 1);
        } catch (Exception e) {
            log.error("get post arguments error", buffer.toString());
        }
        return "";
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}