package top.cxscoder.common.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.cxscoder.system.entity.Role;
import top.cxscoder.system.entity.User;
import top.cxscoder.system.service.MenuService;
import top.cxscoder.system.service.RoleService;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Edward
 * @date 2023-12-01 0:38
 * @copyright Copyright (c) 2023 Edward
 */
@Service
public class PermissionServiceImpl {
    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(User user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            roles.add("admin");
        }
        else
        {
//            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(User user)
    {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            perms.add("*:*:*");
        }
        else
        {
            // TODO 改成lambda表达式
            List<Role> roles = user.getRoles();
            if (!CollectionUtils.isEmpty(roles))
            {
                // 多角色设置permissions属性，以便数据权限匹配权限
                for (Role role : roles)
                {
//                    Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId());
//                    role.setPermissions(rolePerms);
//                    perms.addAll(rolePerms);
                }
            }
            else
            {
//                perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId()));
            }
        }
        return perms;
    }
}
