package top.cxscoder.system.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.Role;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.mapper.RoleMapper;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.system.services.RoleService;

import javax.annotation.Resource;
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
}
