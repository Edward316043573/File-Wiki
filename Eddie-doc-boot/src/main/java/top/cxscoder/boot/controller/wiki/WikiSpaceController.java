package top.cxscoder.boot.controller.wiki;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.Menu;
import top.cxscoder.system.domain.entity.RoleMenu;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.system.services.MenuService;
import top.cxscoder.system.services.RoleMenuService;
import top.cxscoder.wiki.common.constant.UserSettingConst;
import top.cxscoder.wiki.domain.dto.SpaceAuthDTO;
import top.cxscoder.wiki.domain.dto.SpaceFavoruiteDTO;
import top.cxscoder.wiki.domain.dto.UserSettingDTO;
import top.cxscoder.wiki.domain.dto.WikiSpaceDTO;
import top.cxscoder.wiki.domain.entity.RoleSpace;
import top.cxscoder.wiki.domain.entity.UserSetting;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.domain.entity.WikiSpaceFavorite;
import top.cxscoder.wiki.domain.vo.UserSpaceAuthVo;
import top.cxscoder.wiki.domain.vo.WikiSpaceVo;
import top.cxscoder.wiki.framework.consts.WikiAuthType;
import top.cxscoder.wiki.service.manage.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档控制器
 *
 * @author 暮光：城中城
 * @since 2019年2月17日
 */
@Slf4j

//@AuthMan
@RestController
@RequestMapping("/wiki/space")
@RequiredArgsConstructor
public class WikiSpaceController {

    private final WikiSpaceService wikiSpaceService;
    private final UserGroupAuthService userGroupAuthService;
    private final WikiSpaceFavoriteService wikiSpaceFavoriteService;
    private final UserSettingService userSettingService;
    private final LoginService loginService;

    @Resource
    MenuService menuService;

    @Resource
    RoleMenuService roleMenuService;

    @Resource
    RoleSpaceService roleSpaceService;

    //	@PreAuthorize("hasAnyAuthority('wiki:space:list')")
    @PostMapping("/list")
    public List<WikiSpaceVo> list(@RequestBody WikiSpaceDTO wikiSpaceDTO) {
        User currentUser = loginService.getCurrentUser();
        LambdaQueryWrapper<WikiSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WikiSpace::getDelFlag, 0);
        wrapper.eq(wikiSpaceDTO.getId() != null, WikiSpace::getId, wikiSpaceDTO.getId());
        wrapper.and(con -> con.and(conSub -> conSub.eq(WikiSpace::getType, 3).eq(WikiSpace::getCreateUserId, currentUser.getUserId())).or().in(WikiSpace::getType, 1, 2));
        // 收藏的空间
        List<WikiSpaceFavorite> favoriteList = wikiSpaceFavoriteService.myFavoriteSpaceList();
        Set<Long> favoriteSpaceIds = favoriteList.stream().map(WikiSpaceFavorite::getSpaceId).collect(Collectors.toSet());
        // 只展示收藏的空间
        if (!Objects.equals(wikiSpaceDTO.getIgnoreFavorite(), 1)) {
            String onlyShowFavorite = userSettingService.getMySettingValue(UserSettingConst.WIKI_ONLY_SHOW_FAVORITE);
            if (Objects.equals(onlyShowFavorite, "1")) {
                if (favoriteSpaceIds.isEmpty()) {
                    return null;
                }
                wrapper.in(WikiSpace::getId, favoriteSpaceIds);
            }
        }
        long pageNum = Optional.ofNullable(wikiSpaceDTO.getPage()).orElse(1L);
        long pageSize = Optional.ofNullable(wikiSpaceDTO.getPageSize()).orElse(500L);
        pageNum = Math.min(Math.max(pageNum, 1L), 1000);
        pageSize = Math.min(Math.max(pageSize, 10L), 100);
        IPage<WikiSpace> page = new Page<>(pageNum, pageSize, Objects.equals(pageNum, 1L));
        wikiSpaceService.page(page, wrapper);
        // 设置收藏状态
        List<WikiSpaceVo> spaceVoList = page.getRecords().stream().map(WikiSpaceVo::new).collect(Collectors.toList());
        for (WikiSpaceVo spaceVo : spaceVoList) {
            spaceVo.setFavorite(favoriteSpaceIds.contains(spaceVo.getId()) ? 1 : 0);
        }
        return spaceVoList;
    }

    //	@PreAuthorize("hasAnyAuthority('wiki:space:list')")
    @PostMapping("/update")
    public WikiSpace update(@RequestBody WikiSpace wikiSpace) {
        Long id = wikiSpace.getId();
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        if (id != null && id > 0) {
            WikiSpace wikiSpaceSel = wikiSpaceService.getById(id);
            // 不是创建人不能修改空间
            if (!Objects.equals(currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
                throw new ServiceException("您没有权限修改该空间");
            }
            wikiSpace.setUuid(null);
            wikiSpace.setEditType(null);
            wikiSpaceService.updateById(wikiSpace);
        } else {
            wikiSpace.setUuid(IdUtil.simpleUUID());
            wikiSpace.setCreateTime(new Date());
            wikiSpace.setCreateUserId(currentUser.getUserId());
            wikiSpace.setCreateUserName(currentUser.getUserName());
            wikiSpaceService.save(wikiSpace);
        }
        return wikiSpace;
    }

    @PostMapping("/setting/update")
    public void settingUpdate(@RequestBody UserSettingDTO userSettingDTO) {
        User currentUser = loginService.getCurrentUser();
        QueryWrapper<UserSetting> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUser.getUserId());
        wrapper.eq("name", userSettingDTO.getName());
        UserSetting userSettingSel = userSettingService.getOne(wrapper);
        UserSetting userSettingUp = new UserSetting();
        if (userSettingSel != null) {
            userSettingUp.setId(userSettingSel.getId());
        } else {
            userSettingUp.setCreateTime(new Date());
        }
        userSettingUp.setName(userSettingDTO.getName());
        userSettingUp.setValue(userSettingDTO.getValue());
        userSettingUp.setDelFlag(0);
        userSettingUp.setUserId(currentUser.getUserId());
        userSettingService.saveOrUpdate(userSettingUp);
    }

    @PostMapping("/setting/list")
    public Map<String, String> settingList() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        QueryWrapper<UserSetting> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUser.getUserId());
        wrapper.eq("name", UserSettingConst.WIKI_ONLY_SHOW_FAVORITE);
        wrapper.eq("del_flag", 0);
        List<UserSetting> settingList = userSettingService.list(wrapper);
        if (CollectionUtils.isEmpty(settingList)) {
            return null;
        }
        Map<String, String> userSettingMap = settingList.stream().collect(Collectors.toMap(UserSetting::getName, UserSetting::getValue));
        return userSettingMap;
    }

    @PostMapping("/favorite/update")
    public void groupAuth(@RequestBody SpaceFavoruiteDTO spaceFavoruiteDTO) {
        User currentUser = loginService.getCurrentUser();
        QueryWrapper<WikiSpaceFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("space_id", spaceFavoruiteDTO.getSpaceId());
        wrapper.eq("user_id", currentUser.getUserId());
        WikiSpaceFavorite favoriteSel = wikiSpaceFavoriteService.getOne(wrapper);
        WikiSpaceFavorite favoriteUp = new WikiSpaceFavorite();
        if (favoriteSel != null) {
            favoriteUp.setId(favoriteSel.getId());
        } else {
            favoriteUp.setCreateTime(new Date());
        }
        favoriteUp.setDelFlag(spaceFavoruiteDTO.getDelFlag());
        favoriteUp.setUserId(currentUser.getUserId());
        favoriteUp.setSpaceId(spaceFavoruiteDTO.getSpaceId());
        wikiSpaceFavoriteService.saveOrUpdate(favoriteUp);
    }

    @PostMapping("/auth/assign")
    public void authAssign(@RequestBody SpaceAuthDTO spaceAuthDTO) {
        // 判断是否具有授权的权限
        User currentUser = loginService.getCurrentUser();
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(spaceAuthDTO.getSpaceId());
        // 只有空间创建人可以管理该空间对用户组的授权
        if (!Objects.equals(currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            throw new ServiceException("您没有权限管理该空间的权限");
        }
        // 先删除该空间的所有用户的权限
        LambdaQueryWrapper<RoleSpace> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(RoleSpace::getSpaceId, spaceAuthDTO.getSpaceId());
        roleSpaceService.remove(updateWrapper);
        // 在创建权限
        List<UserSpaceAuthVo> authVoList = JSON.parseArray(spaceAuthDTO.getAuthList(), UserSpaceAuthVo.class);
        // 更新RoleSpace表
        Set<Long> roleSet = authVoList.stream().map(UserSpaceAuthVo::getRoleId).collect(Collectors.toSet());
        List<RoleSpace> roleSpaceList = new LinkedList<>();
        roleSet.forEach(r -> {
            roleSpaceList.add(new RoleSpace(r, spaceAuthDTO.getSpaceId()));
        });
        roleSpaceService.saveBatch(roleSpaceList);
        // 更新RoleMenu表
        for (UserSpaceAuthVo authVo : authVoList) {
            List<RoleMenu> userAuthList = new LinkedList<>();
            this.createUserAuth(userAuthList, authVo.getEditPage(), WikiAuthType.EDIT_PAGE, authVo.getRoleId());
            this.createUserAuth(userAuthList, authVo.getDeletePage(), WikiAuthType.DELETE_PAGE, authVo.getRoleId());
            this.createUserAuth(userAuthList, authVo.getPageFileUpload(), WikiAuthType.PAGE_FILE_UPLOAD, authVo.getRoleId());
            this.createUserAuth(userAuthList, authVo.getPageFileDelete(), WikiAuthType.PAGE_FILE_DELETE, authVo.getRoleId());
            this.createUserAuth(userAuthList, authVo.getPageAuthManage(), WikiAuthType.PAGE_AUTH_MANAGE, authVo.getRoleId());
            if (!userAuthList.isEmpty()) {
                roleMenuService.saveBatch(userAuthList);
            }
        }
    }

    @PostMapping("/auth/list")
    public List<UserSpaceAuthVo> authList(@RequestBody SpaceAuthDTO spaceAuthDTO) {
        // 判断是否具有授权的权限
        User currentUser = loginService.getCurrentUser();
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(spaceAuthDTO.getSpaceId());
        // 只有空间创建人可以管理该空间对用户组的授权
        if (!Objects.equals(currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            throw new ServiceException("您没有权限管理该空间的权限");
        }
        // 找到当前空间下的所有角色
        List<RoleSpace> roleSpaceList = roleSpaceService.lambdaQuery().eq(RoleSpace::getSpaceId, spaceAuthDTO.getSpaceId()).list();
        if (CollectionUtils.isEmpty(roleSpaceList)) {
            return null;
        }
        // 查询角色对应权限 并组装结果集
        List<UserSpaceAuthVo> authVoList = new LinkedList<>();
        roleSpaceList.forEach(r -> {
            // 根据角色ID获取权限列表
            List<Menu> menus = menuService.selectMenuListByRoleId(r.getRoleId());
            Set<String> permsSet = menus.stream().map(Menu::getPerms).collect(Collectors.toSet());
            UserSpaceAuthVo authVo = new UserSpaceAuthVo();
            authVo.setEditPage(this.haveAuth(permsSet, WikiAuthType.EDIT_PAGE));
            authVo.setDeletePage(this.haveAuth(permsSet, WikiAuthType.DELETE_PAGE));
            authVo.setPageFileUpload(this.haveAuth(permsSet, WikiAuthType.PAGE_FILE_UPLOAD));
            authVo.setPageFileDelete(this.haveAuth(permsSet, WikiAuthType.PAGE_FILE_DELETE));
            authVo.setPageAuthManage(this.haveAuth(permsSet, WikiAuthType.PAGE_AUTH_MANAGE));
            authVo.setRoleId(r.getRoleId());
            authVoList.add(authVo);
        });
        return authVoList;
    }


    private Integer haveAuth(Set<String> permsSet, WikiAuthType wikiAuthType) {
        return permsSet.contains(wikiAuthType.getCode()) ? 1 : 0;
    }

    private void createUserAuth(List<RoleMenu> userAuthList, Integer authValue, WikiAuthType authType, Long roleId) {
        // 获取当前authType的menuId
        Menu menu = menuService.lambdaQuery().eq(Menu::getPerms, authType.getCode()).one();
        if (ObjectUtils.isEmpty(menu)) {
            throw new ServiceException("权限表中没有配置" + authType.getCode() + "权限，请检查权限列表");
        }
        // 把roleMenu表中的该条权限的记录删除
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId).eq(RoleMenu::getMenuId, menu.getMenuId());
        roleMenuService.remove(wrapper);
        // 如果authValue == 1 则在RoleMenu插入一条记录
        if (Objects.equals(authValue, 1)) {
            userAuthList.add(new RoleMenu(roleId, menu.getMenuId()));
        }
    }
}

