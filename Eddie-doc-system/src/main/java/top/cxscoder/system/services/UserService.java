package top.cxscoder.system.services;

import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.system.domain.entity.User;

import java.util.List;

/**
 * @author Edward
 * @date 2023-11-30 16:55
 * @copyright Copyright (c) 2023 Edward
 */
public interface UserService extends IService<User> {


    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    void checkUserDataScope(Long userId);

    boolean checkUserNameUnique(User user);

    boolean checkPhoneUnique(User user);

    boolean checkEmailUnique(User user);

    void checkUserAllowed(User user);

    int resetPwd(User user);

    int updateUserStatus(User user);


    boolean addUser(User user);

    boolean removeUsersWithRole(List<Long> asList);

    boolean updateUserWithRole(User user);
}
