package top.cxscoder.system.services;


import top.cxscoder.system.domain.entity.User;

import java.util.Map;

/**
 * @author Edward
 * @date 2023-12-01 13:29
 * @copyright Copyright (c) 2023 Edward
 */
public interface LoginService {

    Map<String,String> Login(String username, String password, String code);

    void logout();

    Long getLoginUserId();

    String getUsername();

    User getCurrentUser();
}
