package top.cxscoder.system.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Edward
 * @date 2023-12-06 11:41
 * @copyright Copyright (c) 2023 Edward
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVo implements Serializable {
    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 分页大小
     */
    private Integer pageSize = 10;
}
