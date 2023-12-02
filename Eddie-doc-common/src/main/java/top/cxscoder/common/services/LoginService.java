package top.cxscoder.common.services;

import top.cxscoder.common.advice.ResponseResult;

import java.util.Map;

/**
 * @author Edward
 * @date 2023-12-01 13:29
 * @copyright Copyright (c) 2023 Edward
 */
public interface LoginService {

    Map<String,String> Login(String username, String password, String code);

    void logout();
}
