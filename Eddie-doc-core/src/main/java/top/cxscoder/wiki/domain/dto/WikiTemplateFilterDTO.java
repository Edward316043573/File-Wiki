package top.cxscoder.wiki.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.cxscoder.wiki.domain.vo.WikiTemplateTagVo;

import java.util.List;

/**
 * @author Edward
 * @date 2023-12-10 23:19
 * @copyright Copyright (c) 2023 Edward
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WikiTemplateFilterDTO {
    String name;

    boolean open;

    List<WikiTemplateTagVo> tags;

    Long pageNum;
}
