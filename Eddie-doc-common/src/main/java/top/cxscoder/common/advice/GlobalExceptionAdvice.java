package top.cxscoder.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.cxscoder.common.exception.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
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
        log.error("服务端异常", e);
        return ResponseResult.error("服务端异常", this.buildResponseBody(e.getMessage(), request));
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({Exception.class})
    public ResponseResult<Map<String, Object>> globalHandler(Exception e, HttpServletRequest request) {
        log.error("服务端异常", e);
        return ResponseResult.error("服务端异常", this.buildResponseBody(e.getMessage(), request));
    }

    private Map<String, Object> buildResponseBody(String error, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        body.put("timestamp", LocalDateTime.now().format(dateTimeFormatter));
        body.put("path", request.getRequestURI());
        body.put("method", request.getMethod());
        body.put("info", error);
        return body;
    }
}