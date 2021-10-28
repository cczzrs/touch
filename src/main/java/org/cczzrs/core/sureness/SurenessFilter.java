package org.cczzrs.core.sureness;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usthe.sureness.mgt.SurenessSecurityManager;
import com.usthe.sureness.processor.exception.DisabledAccountException;
import com.usthe.sureness.processor.exception.ExcessiveAttemptsException;
import com.usthe.sureness.processor.exception.ExpiredCredentialsException;
import com.usthe.sureness.processor.exception.IncorrectCredentialsException;
import com.usthe.sureness.processor.exception.ProcessorNotFoundException;
import com.usthe.sureness.processor.exception.UnauthorizedException;
import com.usthe.sureness.processor.exception.UnknownAccountException;
import com.usthe.sureness.processor.exception.UnsupportedSubjectException;
import com.usthe.sureness.subject.SubjectSum;
import com.usthe.sureness.util.SurenessContextHolder;

import org.cczzrs.core.utils.MyUtil;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.log4j.Log4j2;

/**
 * sureness filter class example, filter all http request
 * @author tomsun28
 * @date 23:22 2020-03-02
 */
@Log4j2
@Order(1)
@WebFilter(filterName = "SurenessFilter", urlPatterns = "/*", asyncSupported = true)
public class SurenessFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(!preHandle(servletRequest, servletResponse)){// 前置处理，跨域设置
            return;
        }
        long startTime = System.currentTimeMillis();
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>SurenessFilter<<<<<<<<<<<<<<<<<<<<<<<<<");
        try {
            SubjectSum subject = SurenessSecurityManager.getInstance().checkIn(servletRequest);
            // You can consider using SurenessContextHolder to bind subject in threadLocal
            // if bind, please remove it when end
            if (subject != null) {
                SurenessContextHolder.bindSubject(subject);
            }
        } catch (IncorrectCredentialsException | UnknownAccountException | ExpiredCredentialsException | ProcessorNotFoundException e1) {
            log.debug("this request account info is illegal, {}", e1.getMessage());
            log.info("凭据无效！请重新登录:[{}]=>{}", MyUtil.getIpAddress((HttpServletRequest) servletRequest), ((HttpServletRequest) servletRequest).getRequestURL());
            responseWrite(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED).body("凭据无效！请重新登录"), servletResponse);
            return;
        } catch (DisabledAccountException | ExcessiveAttemptsException e2 ) {
            log.debug("the account is disabled, {}", e2.getMessage());
            responseWrite(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED).body("Account is disabled"), servletResponse);
            return;
        } catch (UnauthorizedException e5) {
            log.debug("this account can not access this resource, {}", e5.getMessage());
            responseWrite(ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("无权访问"), servletResponse);
            return;
        } catch (UnsupportedSubjectException e6) {
            HttpServletRequest hsq = (HttpServletRequest) servletRequest;
            log.error("UnsupportedSubjectException: {}", e6.getMessage());
            log.error("未找到 uri 配置: {}:{} ==> {}==={}", hsq.getRemoteAddr(), hsq.getRemotePort(), hsq.getRequestURI(), hsq.getMethod());
            responseWrite(ResponseEntity.status(HttpStatus.NOT_FOUND).build(),
                    servletResponse);
            return;
        } catch (RuntimeException e) {
            log.error("other exception happen: ", e);
            responseWrite(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(),
                    servletResponse);
            return;
        } finally {
            log.info("<<<<<<<<<<<<<<<<<<<<<<<<<SurenessFilter>>>>>>>>>>>>>>>>>>>>>{} ms", System.currentTimeMillis() - startTime);
        }
        try {
            // if ok, doFilter and add subject in request
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            SurenessContextHolder.clear();
            log.info("XXXXXXXXXXXXXXXXXXXXXXXXXSurenessFilterXXXXXXXXXXXXXXXXXXXX{} ms", System.currentTimeMillis() - startTime);
        }
    }

    /**
     * 前置处理，跨域的必要设置
     */
    public boolean preHandle(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpServletResponse.setHeader("Access-Control-Max-Age", "6000");
        //跨域请求会发送两次请求首次为预检请求，其请求方法为 OPTIONS
        if (httpServletRequest.getMethod().toUpperCase().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            log.info("[{}] OPTIONS [{}]", MyUtil.getIpAddress(httpServletRequest), httpServletRequest.getRequestURI());
            return false;
        }
        return true;
    }
    /**
     * write response json data
     * @param content content
     * @param response response
     */
    private void responseWrite(ResponseEntity<?> content, ServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        ((HttpServletResponse)response).setStatus(content.getStatusCodeValue());
        content.getHeaders().forEach((key, value) ->
                ((HttpServletResponse) response).addHeader(key, value.get(0)));
        try (PrintWriter printWriter = response.getWriter()) {
            if (content.getBody() != null) {
                if (content.getBody() instanceof String) {
                    printWriter.write(content.getBody().toString());
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    printWriter.write(objectMapper.writeValueAsString(content.getBody()));
                }
            } else {
                printWriter.flush();
            }
        } catch (IOException e) {
            log.error("responseWrite response error: ", e);
        }
    }
}
