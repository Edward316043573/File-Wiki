package top.cxscoder.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.common.exception.UnauthorizedException;
import top.cxscoder.common.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Edward
 * @date 2023-11-30 15:31
 * @copyright Copyright (c) 2023 Edward
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({UnauthorizedException.class})
    public ResponseResult<Map<String, Object>> globalHandler(UnauthorizedException e, HttpServletRequest request) {
        log.error("认证失败", e);
        return ResponseResult.authorizedError("认证失败", WebUtils.buildResponseBody(e.getMessage(), request));
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ServiceException.class})
    public ResponseResult<Map<String, Object>> globalHandler(Exception e, HttpServletRequest request) {
        log.error("服务端异常", e);
        return ResponseResult.error(e.getMessage(), WebUtils.buildResponseBody(e.getMessage(), request));
    }

}