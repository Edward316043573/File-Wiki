package top.cxscoder.system.security.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Edward
 * @date 2023-12-17 0:04
 * @copyright Copyright (c) 2023 Edward
 */
@Component
public class ExceptionHandlerFilter  extends OncePerRequestFilter {
    @Resource(name="handlerExceptionResolver")
    private HandlerExceptionResolver resolver;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        } catch (Exception e) {
            resolver.resolveException(httpServletRequest,httpServletResponse,null,e);
        }
    }
}
