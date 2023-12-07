package top.cxscoder.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.cxscoder.system.domain.entity.Menu;
import top.cxscoder.system.domain.entity.User;

import java.util.List;
import java.util.Set;

/**
 * @author Edward
 * @date 2023-11-30 20:15
 * @copyright Copyright (c) 2023 Edward
 */
public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> selectMenuListByUserId(Menu menu);

    Integer checkMenuExistRole(Long menuId);
}
