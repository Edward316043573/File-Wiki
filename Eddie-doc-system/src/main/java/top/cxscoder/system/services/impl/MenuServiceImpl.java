package top.cxscoder.system.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import top.cxscoder.system.domain.entity.Menu;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.mapper.MenuMapper;
import top.cxscoder.system.services.MenuService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Edward
 * @date 2023-12-01 20:58
 * @copyright Copyright (c) 2023 Edward
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Resource
    MenuMapper menuMapper;



    /**
     * 查询当前用户的菜单
     * @param menu 菜单信息
     * @param loginUserId 当前用户ID
     * @return 菜单列表
     */
    @Override
    public List<Menu> selectMenuList(Menu menu, Long loginUserId) {

        List<Menu> menuList = null;
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();

        // 管理员显示所有菜单信息
        User user = new User();
        if (user.isAdmin(loginUserId))
        {
            queryWrapper.eq(!ObjectUtils.isEmpty(menu.getVisible()),Menu::getVisible,menu.getVisible())
                    .eq(!ObjectUtils.isEmpty(menu.getStatus()),Menu::getStatus,menu.getStatus())
                    .like(!ObjectUtils.isEmpty(menu.getMenuName()),Menu::getMenuName,menu.getMenuName());
            menuList = menuMapper.selectList(queryWrapper);
        }
        else
        {
            // 非管理员查对应的菜单
            menu.getParams().put("userId", loginUserId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }


    @Override
    public List<Menu> selectMenuList(Long loginUserId) {
        return selectMenuList(new Menu(),loginUserId);
    }


    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkMenuNameUnique(Menu menu) {
        Long menuId = ObjectUtils.isEmpty(menu.getMenuId()) ? -1L : menu.getMenuId();
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getMenuName,menu.getMenuName())
                        .eq(Menu::getParentId,menu.getParentId());
        Menu info = menuMapper.selectOne(queryWrapper);

        if (!ObjectUtils.isEmpty(info) && info.getMenuId().longValue() != menuId.longValue())
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        // 查找parent_id为当前菜单id的结果 有的话则返回false
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId,menuId);
        List<Menu> menus = menuMapper.selectList(wrapper);
        return menus.size() > 0;
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        return menuMapper.checkMenuExistRole(menuId) > 0;
    }


}
