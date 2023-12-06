package top.cxscoder.system.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import top.cxscoder.common.cache.RedisCache;
import top.cxscoder.common.enums.UserStatus;
import top.cxscoder.common.exception.UnauthorizedException;
import top.cxscoder.system.services.impl.PermissionServiceImpl;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.UserService;

import javax.annotation.Resource;

/**
 * @author Edward
 * @date 2023-11-30 23:34
 * @copyright Copyright (c) 2023 Edward
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Resource
    private UserService userService;

    @Resource
    private PermissionServiceImpl permissionService;

    @Resource
    private RedisCache redisCache;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        // 根据用户名查询
        User user = userService.lambdaQuery().eq(User::getUserName, username).one();
        if (ObjectUtils.isEmpty(user))
        {
            log.info("登录用户：{} 不存在.", username);
            throw new UnauthorizedException("登录用户的不存在.");
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            log.info("登录用户：{} 已被删除.", username);
            throw new UnauthorizedException("登录用户已被删除");
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            log.info("登录用户：{} 已被停用.", username);
            throw new UnauthorizedException("登录用户已被停用");
        }
        // 权限封装
//        Set<String> list = menuMapper.selectPermsByUserId(user.getId());
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(User user)
    {
        return new LoginUser(user, permissionService.getMenuPermission(user));
    }
}
