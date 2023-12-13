package top.cxscoder.wiki.domain.dto;

import lombok.Data;
import top.cxscoder.system.domain.VO.PageVo;

/**
 * @author Edward
 * @date 2023-12-11 14:37
 * @copyright Copyright (c) 2023 Edward
 */
@Data
public class MessageDTO extends PageVo {
    Integer sysType;

    Integer msgStatus;
}
