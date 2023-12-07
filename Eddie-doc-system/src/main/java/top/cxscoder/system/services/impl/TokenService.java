package top.cxscoder.system.services.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.cxscoder.system.security.LoginUser;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Edward
 * @date 2023-12-01 15:49
 * @copyright Copyright (c) 2023 Edward
 */
@Component
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;


    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public String createJWT(String subject, LoginUser loginUser) {
        // 创建token
        Map<String, Object> map = new HashMap<>();
        map.put("uid",subject);
        // 设置签发时间
        map.put(JWTPayload.ISSUED_AT,DateTime.now());
        // 设置生效时间
        map.put(JWTPayload.NOT_BEFORE,DateTime.now());
        // 设置过期时间
        map.put(JWTPayload.EXPIRES_AT,DateTime.now().offset(DateField.MINUTE,expireTime));
        String token = JWTUtil.createToken(map, subject.getBytes());
        return token;
    }


}
