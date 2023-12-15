package top.cxscoder.system.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.cxscoder.system.domain.VO.PageVo;

/**
 * @author Edward
 * @date 2023-12-15 17:28
 * @copyright Copyright (c) 2023 Edward
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO extends PageVo {

    /** 菜单名称 */
    private String menuName;

    /** 菜单状态（0正常 1停用） */
    private String status;
}
