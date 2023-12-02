package top.cxscoder.boot.controller.base;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.cxscoder.system.entity.LoginDTO;
import top.cxscoder.system.entity.User;
import top.cxscoder.system.service.UserService;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/list")
//    @PreAuthorize("hasAuthority('test')")
    public List<User> testController() {
        System.out.println("测试");
        List<User> list = userService.list();
        System.out.println(list);
        return list;
    }

}
