package top.cxscoder.system.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.domain.entity.UserRole;
import top.cxscoder.system.mapper.UserMapper;
import top.cxscoder.system.mapper.UserRoleMapper;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.system.services.UserService;

import javax.annotation.Resource;
import java.util.List;
/**
 * @author Edward
 * @date 2023-11-30 21:17
 * @copyright Copyright (c) 2023 Edward
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    LoginService loginService;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    UserRoleMapper userRoleMapper;
    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        if (!User.isAdmin(loginService.getLoginUserId()))
        {
            User user = userMapper.selectById(userId);
            if (ObjectUtils.isEmpty(user))
            {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 检查用户名是否唯一
     * @param user 用户信息
     * @return true：唯一 false：不唯一
     */
    @Override
    public boolean checkUserNameUnique(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,user.getUserName());
        List<User> users = userMapper.selectList(queryWrapper);
        return users.size() <= 0;
    }

    /**
     * 检查手机是否唯一
     * @param user 用户信息
     * @return true：唯一 false：不唯一
     */
    @Override
    public boolean checkPhoneUnique(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhonenumber,user.getPhonenumber());
        List<User> users = userMapper.selectList(queryWrapper);
        return users.size() <= 0;
    }

    /**
     * 检查邮箱是否唯一
     * @param user 用户信息
     * @return true：唯一 false：不唯一
     */
    @Override
    public boolean checkEmailUnique(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,user.getEmail());
        List<User> users = userMapper.selectList(queryWrapper);
        return users.size() <= 0;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(User user) {
        if (!ObjectUtils.isEmpty(user.getUserId()) && user.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    @Override
    public int resetPwd(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public int updateUserStatus(User user) {
        return userMapper.updateById(user);
    }

    @Override
    @Transactional
    public boolean addUser(User user) {

        if (!checkUserNameUnique(user))
        {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (!ObjectUtils.isEmpty(user.getPhonenumber()) && !checkPhoneUnique(user))
        {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (!ObjectUtils.isEmpty(user.getEmail()) && !checkEmailUnique(user))
        {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(loginService.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        int result = userMapper.insert(user);
        if (result == 0){
            throw new ServiceException("插入用户失败");
        }
        Long userId = user.getUserId();
        Long[] roleIds = user.getRoleIds();
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            result = userRoleMapper.insert(userRole);
            if (result == 0){
                throw new ServiceException("用户角色关联表插入失败");
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeUsersWithRole(List<Long> asList) {
        boolean b = removeByIds(asList);
        if (!b){
            throw new ServiceException("删除用户失败");
        }
        for (Long userId : asList) {
            LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRole::getUserId,userId);
            int res = userRoleMapper.delete(wrapper);
//            if (res == 0){
//                throw new ServiceException("删除关联表失败");
//            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updateUserWithRole(User user) {
       checkUserAllowed(user);
       checkUserDataScope(user.getUserId());
        // TODO 这个校验需要考虑下 当前的BUG是用户只有一个也会报存在，如果改条件又会和新增的逻辑不符合
//        if (!checkUserNameUnique(user))
//        {
//            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
//        }
//        else if (!ObjectUtils.isEmpty(user.getPhonenumber()) && !checkPhoneUnique(user))
//        {
//            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
//        }
//        else if (!ObjectUtils.isEmpty(user.getEmail()) && !checkEmailUnique(user))
//        {
//            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
//        }
        user.setUpdateBy(loginService.getUsername());
        updateById(user);
        //更新关联表
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,user.getUserId());
        userRoleMapper.delete(queryWrapper);
        Long userId = user.getUserId();
        Long[] roleIds = user.getRoleIds();
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            int result = userRoleMapper.insert(userRole);
            if (result == 0){
                throw new ServiceException("用户角色关联表更新 失败");
            }
        }
        return true;
    }
}
