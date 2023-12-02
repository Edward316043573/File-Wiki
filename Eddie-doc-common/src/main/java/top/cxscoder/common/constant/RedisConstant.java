package top.cxscoder.common.constant;

import java.io.Serializable;

/**
 * @author Edward
 * @date 2023-12-01 14:55
 * @copyright Copyright (c) 2023 Edward
 */
public class RedisConstant implements Serializable {
    /**
     * key前缀
     */
    public static final String PREFIX_KEY = "wiki:";

    /**
     * 登录 key前缀
     */
    public static final String LOGIN_KEY_PREFIX = PREFIX_KEY + "login:";
}
