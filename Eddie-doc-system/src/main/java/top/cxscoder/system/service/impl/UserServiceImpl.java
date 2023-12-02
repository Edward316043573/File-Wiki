package top.cxscoder.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.system.entity.User;
import top.cxscoder.system.mapper.UserMapper;
import top.cxscoder.system.service.UserService;

/**
 * @author Edward
 * @date 2023-11-30 21:17
 * @copyright Copyright (c) 2023 Edward
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
