package top.cxscoder.common.security.handle;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;
import top.cxscoder.common.advice.ResponseResult;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.common.exception.UnauthorizedException;
import top.cxscoder.common.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 认证失败处理类 返回未授权
 *
 * @author Edward
 * @date 2023-12-01 0:33
 * @copyright Copyright (c) 2023 Edward
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        ResponseResult<Map<String, Object>> result;
        if (e instanceof BadCredentialsException) {
            result = ResponseResult.authorizedError("用户名/密码错误", WebUtils.buildResponseBody(e.getMessage(), request));
        } else if (e instanceof InsufficientAuthenticationException) {
            result = ResponseResult.authorizedError("认证失败，请检查是否登录", WebUtils.buildResponseBody(e.getMessage(), request));
        } else {
            result = ResponseResult.authorizedError("未知错误",WebUtils.buildResponseBody(e.getMessage(), request));
        }
        WebUtils.renderString(response,JSON.toJSONString(result));
    }


}
