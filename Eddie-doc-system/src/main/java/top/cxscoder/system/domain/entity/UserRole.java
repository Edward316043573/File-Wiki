package top.cxscoder.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Edward
 * @date 2023-11-30 16:04
 * @copyright Copyright (c) 2023 Edward
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user_role")

public class UserRole {
    /** 用户ID */
    private Long userId;

    /** 角色ID */
    private Long roleId;
}
