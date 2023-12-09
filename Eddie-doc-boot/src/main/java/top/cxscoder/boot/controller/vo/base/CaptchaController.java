package top.cxscoder.boot.controller.vo.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.common.cache.RedisCache;

/**
 * 验证码接口
 *
 * @author Edward
 * @date 2023-11-30 23:46
 * @copyright Copyright (c) 2023 Edward
 */
@RestController
public class CaptchaController {

    @Autowired
    private RedisCache redisCache;

    @Value("${captcha.enable}")
    private boolean CAPTCHA_ENABLED;


}
