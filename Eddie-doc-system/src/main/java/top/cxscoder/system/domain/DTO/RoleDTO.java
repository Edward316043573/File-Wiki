package top.cxscoder.system.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.cxscoder.system.domain.VO.PageVo;

/**
 * @author Edward
 * @date 2023-12-06 19:03
 * @copyright Copyright (c) 2023 Edward
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO extends PageVo {

    /** 角色名称 */
    private String roleName;

    /** 角色权限 */
    private String roleKey;

    /** 角色状态（0正常 1停用） */
    private String status;

}
