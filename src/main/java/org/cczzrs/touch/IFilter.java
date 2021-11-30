package org.cczzrs.touch;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cczzrs.core.utils.MyUtil;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Order(1)
@WebFilter(filterName = "IFilter", urlPatterns = "/*", asyncSupported = true)
public class IFilter implements Filter{
    
    /**
     * 前置处理，跨域的必要设置
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpServletResponse.setHeader("Access-Control-Max-Age", "6000");
        //跨域请求会发送两次请求首次为预检请求，其请求方法为 OPTIONS
        if (httpServletRequest.getMethod().toUpperCase().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            log.info("[{}] OPTIONS [{}]", MyUtil.getIpAddress(httpServletRequest), httpServletRequest.getRequestURI());
        }
        chain.doFilter(request, response);
    }
}
