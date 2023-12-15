package top.cxscoder.system.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.cxscoder.system.domain.VO.PageVo;

/**
 * @author Edward
 * @date 2023-12-06 11:43
 * @copyright Copyright (c) 2023 Edward
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends PageVo {
    /** 用户账号 */
    private String userName;

    /** 手机号码 */
    private String phonenumber;

    /** 用户邮箱 */
    private String email;

    /** 帐号状态（0正常 1停用） */
    private String status;
}
