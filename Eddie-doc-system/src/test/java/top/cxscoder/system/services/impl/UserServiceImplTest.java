package top.cxscoder.system.services.impl;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.mapper.UserMapper;
import top.cxscoder.system.services.UserService;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author Edward
 * @date 2023-11-30 21:18
 * @copyright Copyright (c) 2023 Edward
 */
@SpringBootTest
public class UserServiceImplTest {

    @Resource
    UserMapper userMapper;

    @Resource
    UserService userService;
    @Test
    public void test1() {
        System.out.println();
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
    }
}