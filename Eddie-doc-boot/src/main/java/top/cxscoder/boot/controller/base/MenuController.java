package top.cxscoder.boot.controller.base;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.system.domain.entity.Menu;
import top.cxscoder.system.services.MenuService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜单信息
 *
 * @author Edward
 * @date 2023-11-30 19:24
 * @copyright Copyright (c) 2023 Edward
 */
@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Resource
    private MenuService menuService;

    @Resource
    private LoginService loginService;

    /** 是否菜单外链（是） */
    public static final String YES_FRAME = "0";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 获取菜单列表
     */
    @PreAuthorize("hasAnyAuthority('system:menu:list')")
    @GetMapping("/list")
    public List<Menu> list(Menu menu)
    {
        List<Menu> menus = menuService.selectMenuList(menu, loginService.getLoginUserId());
        return menus;
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("hasAnyAuthority('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public Menu getInfo(@PathVariable Long menuId)
    {
        return menuService.getById(menuId);
    }


    /**
     * 新增菜单
     */
    @PreAuthorize("hasAnyAuthority('system:menu:add')")
    @PostMapping
    public boolean add(@Validated @RequestBody Menu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            throw new ServiceException("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (YES_FRAME.equals(menu.getIsFrame()) && StringUtils.startsWithAny(menu.getPath(), HTTP, HTTPS))
        {
            throw new ServiceException("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(loginService.getUsername());
        return menuService.save(menu);
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("hasAnyAuthority('system:menu:edit')")
    @PutMapping
    public boolean edit(@Validated @RequestBody Menu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (YES_FRAME.equals(menu.getIsFrame()) && StringUtils.startsWithAny(menu.getPath(), HTTP, HTTPS))
        {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(loginService.getUsername());
        // TODO 测试更新
        return menuService.update(menu,null);
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("hasAnyAuthority('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public boolean remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            throw new ServiceException("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            throw new ServiceException("菜单已分配,不允许删除");
        }
        return menuService.removeById(menuId);
    }
}
