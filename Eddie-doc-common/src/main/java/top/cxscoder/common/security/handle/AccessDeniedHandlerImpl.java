package top.cxscoder.common.security.handle;

import com.alibaba.fastjson.JSON;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import top.cxscoder.common.advice.ResponseResult;
import top.cxscoder.common.utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 权限不足处理类 返回未授权
 *
 * @author Edward
 * @date 2023-12-01 0:33
 * @copyright Copyright (c) 2023 Edward
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseResult<Map<String, Object>> result = ResponseResult.error("你没有权限访问这个功能", WebUtils.buildResponseBody(accessDeniedException.getMessage(), request));
        WebUtils.renderString(response,JSON.toJSONString(result));
    }
}
