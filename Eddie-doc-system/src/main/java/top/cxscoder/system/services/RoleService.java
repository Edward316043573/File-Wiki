package top.cxscoder.system.services;

import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.system.domain.DTO.RoleDTO;
import top.cxscoder.system.domain.entity.Role;

/**
 * @author Edward
 * @date 2023-11-30 16:56
 * @copyright Copyright (c) 2023 Edward
 */
public interface RoleService extends IService<Role> {
    void checkRoleDataScope(Long roleId);

    boolean checkRoleNameUnique(Role role);

    boolean checkRoleKeyUnique(Role role);

    void checkRoleAllowed(Role role);

    boolean addRole(RoleDTO roleDto);
}
