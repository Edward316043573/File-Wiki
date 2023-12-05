package top.cxscoder.common.security.filter;


import cn.hutool.Hutool;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import top.cxscoder.common.cache.RedisCache;
import top.cxscoder.common.constant.RedisConstant;
import top.cxscoder.common.security.LoginUser;
import top.cxscoder.common.services.impl.TokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义认证过滤器
 *
 * @author Edward
 * @date 2023-12-01 0:33
 * @copyright Copyright (c) 2023 Edward
 */

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Value("${token.header}")
    private String tokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader(tokenHeader);
        if (!StringUtils.hasText(token)) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        String userId;

        try {
            JWT jwt = JWTUtil.parseToken(token);
            JWTPayload payload = jwt.getPayload();
            userId = (String) payload.getClaim("uid");
            // 验证token
            JWTValidator.of(token).validateAlgorithm(JWTSignerUtil.hs256(userId.getBytes())).validateDate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
            // 响应前端
        }
        //从redis中获取用户信息
        String redisKey = RedisConstant.LOGIN_KEY_PREFIX + userId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if(Objects.isNull(loginUser)){
            throw new RuntimeException("用户未登录");
        }
        //存入SecurityContextHolder
        //获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }
}
