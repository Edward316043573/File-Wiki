package top.cxscoder.boot.controller.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.system.domain.DTO.UserDTO;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.RoleService;
import top.cxscoder.system.services.UserService;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author Edward
 * @date 2023-11-30 17:06
 * @copyright Copyright (c) 2023 Edward
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @Resource
    RoleService roleService;

    @Resource
    private LoginService loginService;

    @Resource
    PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表
     */
    @PreAuthorize("hasAnyAuthority('system:user:list')")
    @GetMapping("/list")
    public IPage<User> list(UserDTO userDTO)
    {
        return userService.page(new Page<>(userDTO.getPage(), userDTO.getPageSize()));
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("hasAnyAuthority('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public User getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        return userService.getById(userId);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("hasAnyAuthority('system:user:add')")
    @PostMapping
    public boolean add(@Validated @RequestBody User user)
    {
        if (!userService.checkUserNameUnique(user))
        {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (!ObjectUtils.isEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(loginService.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.save(user);
    }

    /**
     * 修改用户
     */
    @PreAuthorize("hasAnyAuthority('system:user:edit')")
    @PutMapping
    public boolean edit(@Validated @RequestBody User user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (!userService.checkUserNameUnique(user))
        {
            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(loginService.getUsername());
        return userService.updateById(user);
    }

    /**
     * 删除用户
     */
    @PreAuthorize("hasAnyAuthority('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public boolean remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, loginService.getLoginUserId()))
        {
            throw new ServiceException("当前用户不能删除");
        }
        return userService.removeBatchByIds(Arrays.asList(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("hasAnyAuthority('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public Integer resetPwd(@RequestBody User user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUpdateBy(loginService.getUsername());
        return userService.resetPwd(user);
    }

    /**
     * 状态修改
     */
    @PreAuthorize("hasAnyAuthority('system:user:edit')")
    @PutMapping("/changeStatus")
    public Integer changeStatus(@RequestBody User user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(loginService.getUsername());
        return userService.updateUserStatus(user);
    }


}
