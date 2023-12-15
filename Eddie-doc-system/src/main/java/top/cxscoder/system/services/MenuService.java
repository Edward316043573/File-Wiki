package top.cxscoder.system.services;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.system.domain.entity.Menu;

/**
 * @author Edward
 * @date 2023-11-30 16:57
 * @copyright Copyright (c) 2023 Edward
 */
public interface MenuService extends IService<Menu> {
    IPage<Menu> selectMenuList(Menu menu, Long loginUserId, Page<Menu> menuPage);

    IPage<Menu> selectMenuList(Long loginUserId);

    /**
     * 检查菜单名字是否唯一
     * @param menu 菜单信息
     * @return
     */
    boolean checkMenuNameUnique(Menu menu);

    boolean hasChildByMenuId(Long menuId);

    boolean checkMenuExistRole(Long menuId);

}
