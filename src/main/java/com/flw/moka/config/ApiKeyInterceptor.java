package com.flw.moka.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("ALL")
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Autowired
    private Environment environment;

    private String API_KEY_HEADER;
    private String API_KEY_VALUE;

    @PostConstruct
    public void init() {
        API_KEY_HEADER = environment.getProperty("api.key.header");
        API_KEY_VALUE = environment.getProperty("api.key.value");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null || !apiKey.equals(API_KEY_VALUE)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access denied. API Key not valid.");
            response.getWriter().flush();
            return false;
        }
        return true;
    }
}
