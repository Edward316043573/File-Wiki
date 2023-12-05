package top.cxscoder.common.utils;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Edward
 * @date 2023-11-30 20:11
 * @copyright Copyright (c) 2023 Edward
 */
public class WebUtils {
    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string 待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string) {
        try
        {
            // 设置响应的状态码
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // 设置响应内容类型为JSON
            response.setContentType("application/json;charset=UTF-8");
            // 将字符串写入响应
            response.getWriter().write(string);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 封装错误返回信息
     * @param error 错误信息
     * @param request request对象
     * @return 封装后response体
     */
    public static Map<String, Object> buildResponseBody(String error, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        body.put("timestamp", LocalDateTime.now().format(dateTimeFormatter));
        body.put("path", request.getRequestURI());
        body.put("method", request.getMethod());
        body.put("info", error);
        return body;
    }
}
