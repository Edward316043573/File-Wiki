package top.cxscoder.common.advice;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Edward
 * @date 2023-11-30 15:31
 * @copyright Copyright (c) 2023 Edward
 */
@Data
@NoArgsConstructor
public class ResponseResult<T> implements Serializable {

    /**
     * 200 成功
     */
    protected static final int SUCCESS_CODE = 200;

    /**
     * 500 失败
     */
    protected static final int ERROR_CODE = 500;

    /**
     * 401 认证失败
     */
    protected static final int AUTHORIZED_ERROR_CODE = 401;

    /**
     * 403 权限不足
     */
    protected static final int ACCESS_DENIEND_CODE = 403;
    /**
     * 成功
     */
    protected static final String SUCCESS_MSG = "SUCCESS";

    /**
     *
     */
    protected static final String ERROR_MSG = "ERROR";

    /**
     * 返回状态码
     */
    protected int code;

    /**
     * 提示信息
     */
    protected String msg;

    /**
     * 数据类型
     */
    protected T data;

    protected ResponseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    protected ResponseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    protected static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(SUCCESS_CODE, message, data);
    }

    public static <T> ResponseResult<T> success(String message) {
        return new ResponseResult<>(SUCCESS_CODE, message);
    }

    public static <T> ResponseResult<T> error(String message, T data) {
        return new ResponseResult<>(ERROR_CODE, message, data);
    }

    public static <T> ResponseResult<T> authorizedError(String message, T data) {
        return new ResponseResult<>(AUTHORIZED_ERROR_CODE, message, data);
    }

    public static <T> ResponseResult<T> noAccessError(String message, T data) {
        return new ResponseResult<>(ACCESS_DENIEND_CODE, message, data);
    }
}
