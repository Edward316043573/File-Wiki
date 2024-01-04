package top.cxscoder.wiki.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Edward
 * @date 2024-01-04 13:25
 * @copyright Copyright (c) 2024 Edward
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("wiki_auth_role_space")
public class RoleSpace {
    Long roleId;
    Long spaceId;
}
