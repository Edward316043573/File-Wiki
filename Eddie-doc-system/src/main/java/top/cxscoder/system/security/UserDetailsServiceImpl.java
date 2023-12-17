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
import top.cxscoder.system.domain.entity.Menu;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.MenuService;
import top.cxscoder.system.services.UserService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private MenuService menuService;

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
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(User user)
    {
        // 获取当前用户的权限集合
        List<Menu> menus = menuService.selectMenuList(user.getUserId());
        Set<String> perms = menus.stream().map(m -> new String(m.getPerms())).collect(Collectors.toSet());
        return new LoginUser(user, perms);
    }
}
