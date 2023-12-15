package top.cxscoder.wiki.domain.dto;

import lombok.Data;
import top.cxscoder.system.domain.VO.PageVo;

/**
 * @author Edward
 * @date 2023-12-13 17:23
 * @copyright Copyright (c) 2023 Edward
 */
@Data
public class WikiPageHistoryDTO extends PageVo {
    Long pageId;
}
