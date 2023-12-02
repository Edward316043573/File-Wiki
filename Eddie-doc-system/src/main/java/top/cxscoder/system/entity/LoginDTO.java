package top.cxscoder.system.entity;

import lombok.Data;

/**
 * @author Edward
 * @date 2023-11-30 17:18
 * @copyright Copyright (c) 2023 Edward
 */
@Data
public class LoginDTO {
    /**
     * 用户名
     */

    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;
}
