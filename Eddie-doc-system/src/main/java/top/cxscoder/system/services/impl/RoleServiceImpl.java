package top.cxscoder.system.services.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.Role;
import top.cxscoder.system.domain.entity.RoleMenu;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.mapper.RoleMapper;
import top.cxscoder.system.mapper.RoleMenuMapper;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.system.services.RoleService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Edward
 * @date 2023-12-01 20:58
 * @copyright Copyright (c) 2023 Edward
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Resource
    LoginService loginService;

    @Resource
    RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Override
    public void checkRoleDataScope(Long roleId) {
        if (!User.isAdmin(loginService.getLoginUserId()))
        {
            Role role = roleMapper.selectById(roleId);
            if (ObjectUtils.isEmpty(role))
            {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleNameUnique(Role role) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName,role.getRoleName());
        List<Role> roles = roleMapper.selectList(queryWrapper);
        return roles.size() <= 0;
    }

    @Override
    public boolean checkRoleKeyUnique(Role role) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleKey,role.getRoleKey());
        List<Role> roles = roleMapper.selectList(queryWrapper);
        return roles.size() <= 0;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(Role role) {
        if (!ObjectUtils.isEmpty(role.getRoleId()) && role.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    @Override
    @Transactional
    public boolean addRole(Role role) {

        if (!checkRoleNameUnique(role))
        {
            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!checkRoleKeyUnique(role))
        {
            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(loginService.getUsername());
        // 先插入角色信息，获取角色id
        int result = roleMapper.insert(role);
        if (result == 0) {
            throw new ServiceException("插入角色失败");
        }
        Long roleId = role.getRoleId();
        // 再插入角色菜单关联表数据
        List<Long> menuIds = Arrays.asList(role.getMenuIds());
        if (CollectionUtil.isNotEmpty(menuIds)) {
            List<RoleMenu> roleMenus = new ArrayList<>();
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenus.add(roleMenu);
                result =  roleMenuMapper.insert(roleMenu);
                if (result == 0) {
                    throw new ServiceException("角色菜单关联表插入失败");
                }
            }

        }
        return true;
    }
}
