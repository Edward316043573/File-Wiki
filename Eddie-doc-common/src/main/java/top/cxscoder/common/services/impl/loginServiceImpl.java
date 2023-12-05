package top.cxscoder.common.services.impl;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.MessageUtils;
import top.cxscoder.common.advice.ResponseResult;
import top.cxscoder.common.cache.RedisCache;
import top.cxscoder.common.constant.RedisConstant;
import top.cxscoder.common.exception.UnauthorizedException;
import top.cxscoder.common.security.LoginUser;
import top.cxscoder.common.services.LoginService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Edward
 * @date 2023-12-01 13:27
 * @copyright Copyright (c) 2023 Edward
 */
@Service
public class loginServiceImpl implements LoginService {

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisCache redisCache;

    @Resource
    TokenService tokenService;

    @Override
    public Map<String,String> Login(String username, String password, String code) {
        // TODO 验证验证码

        // TODO 预检验
        loginPreCheck(username, password);
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if(Objects.isNull(authenticate)){
            throw new UnauthorizedException("登录失败");
        }
        //如果认证通过了，使用userid生成一个jwt jwt存入ResponseResult返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUser().getUserId().toString();
        String token = tokenService.createJWT(userid, loginUser);
        Map<String,String> map = new HashMap<>();
        map.put(header,token);
        // TODO 封装一下用户信息
        //把完整的用户信息存入redis  userid作为key
        redisCache.setCacheObject(RedisConstant.LOGIN_KEY_PREFIX + userid,loginUser);
        return map;
    }

    @Override
    public void logout() {
        // 删除Redis中的key
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getUserId();
        //删除redis中的值
        redisCache.deleteObject(RedisConstant.LOGIN_KEY_PREFIX+userid);

    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            // TODO 异步记录LOG

            throw new UnauthorizedException("用户名或密码为空");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < 4  || password.length() > 40)
        {
            // TODO 异步记录LOG

           throw new UnauthorizedException("密码长度有误");
        }
    }
}
