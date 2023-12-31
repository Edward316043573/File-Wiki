package top.cxscoder.boot.controller.base;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import top.cxscoder.common.advice.ResponseResult;
import top.cxscoder.common.security.LoginUser;
import top.cxscoder.common.services.LoginService;
import top.cxscoder.system.entity.LoginDTO;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Edward
 * @date 2023-11-30 23:56
 * @copyright Copyright (c) 2023 Edward
 */
@RestController
@Api(tags = "登录相关接口")
public class LoginController {

    @Resource
    LoginService loginService;

    @ApiOperation("登录接口")
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody LoginDTO loginDTO) {
        return loginService.Login(loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getCode());
    }

    @ApiOperation("登出接口")
    @PostMapping("/logout")
    public void logout(){
        loginService.logout();
    }
}
