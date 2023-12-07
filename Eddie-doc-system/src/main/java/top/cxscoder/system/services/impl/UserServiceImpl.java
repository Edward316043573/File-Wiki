package top.cxscoder.system.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.mapper.UserMapper;
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

}
