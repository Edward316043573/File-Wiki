package top.cxscoder.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Edward
 * @date 2023-11-30 16:08
 * @copyright Copyright (c) 2023 Edward
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenu {
    /** 角色ID */
    private Long roleId;

    /** 菜单ID */
    private Long menuId;
}
