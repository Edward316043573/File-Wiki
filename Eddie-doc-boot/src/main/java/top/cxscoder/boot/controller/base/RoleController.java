package top.cxscoder.boot.controller.base;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.DTO.RoleDTO;
import top.cxscoder.system.domain.entity.Role;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.system.services.RoleService;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author Edward
 * @date 2023-11-30 19:24
 * @copyright Copyright (c) 2023 Edward
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    RoleService roleService;

    @Resource
    LoginService loginService;

    /**
     * 获取角色列表
     */
    @PreAuthorize("hasAnyAuthority('system:role:list')")
    @PostMapping("/list")
    public IPage<Role> list(@RequestBody RoleDTO roleDTO)
    {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!ObjectUtils.isEmpty(roleDTO.getRoleName()),Role::getRoleName,roleDTO.getRoleName());
        queryWrapper.like(!ObjectUtils.isEmpty(roleDTO.getRoleKey()),Role::getRoleKey,roleDTO.getRoleKey());
        queryWrapper.eq(!ObjectUtils.isEmpty(roleDTO.getStatus()),Role::getStatus,roleDTO.getStatus());
        return roleService.page(new Page<>(roleDTO.getPage(), roleDTO.getPageSize()));
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("hasAnyAuthority('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public Role getInfo(@PathVariable Long roleId)
    {
        // 校验角色是否有数据权限
        roleService.checkRoleDataScope(roleId);
        return roleService.getById(roleId);
    }

    /**
     * 新增角色
     */
    @PreAuthorize("hasAnyAuthority('system:role:add')")
    @PostMapping
    public boolean add(@Validated @RequestBody RoleDTO roleDto)
    {
        Role role = BeanUtil.copyProperties(roleDto, Role.class);
        return roleService.addRole(role);

    }

//    @PreAuthorize("hasAnyAuthority('system:role:add')")
//    @PostMapping
//    public boolean add(@Validated @RequestBody Role role)
//    {
//        if (!roleService.checkRoleNameUnique(role))
//        {
//            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
//        }
//        else if (!roleService.checkRoleKeyUnique(role))
//        {
//            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
//        }
//        role.setCreateBy(loginService.getUsername());
//        return roleService.save(role);
//
//    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("hasAnyAuthority('system:role:edit')")
    @PutMapping
    public boolean edit(@Validated @RequestBody Role role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role))
        {
            throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(loginService.getUsername());

        if (roleService.updateById(role))
        {
            // 更新缓存用户权限
//            LoginUser loginUser = getLoginUser();
//            if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin())
//            {
//                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
//                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
//                tokenService.setLoginUser(loginUser);
//            }
//            return success();
            return true;
        }
        throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 删除角色
     */
    @PreAuthorize("hasAnyAuthority('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public boolean remove(@PathVariable Long[] roleIds)
    {
        return roleService.removeBatchByIds(Arrays.asList(roleIds));
    }
}
