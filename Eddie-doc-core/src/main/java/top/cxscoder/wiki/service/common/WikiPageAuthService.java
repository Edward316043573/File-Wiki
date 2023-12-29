package top.cxscoder.wiki.service.common;


import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.framework.consts.SpaceType;
import top.cxscoder.wiki.framework.consts.WikiAuthType;
import top.cxscoder.wiki.repository.mapper.UserGroupAuthMapper;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

/**
 * wiki页面权限服务
 *
 * @author 暮光：城中城
 * @since 2020-06-16
 */
@Service
@RequiredArgsConstructor
public class WikiPageAuthService {

    private final UserGroupAuthMapper userGroupAuthMapper;
    @Resource
    private LoginService loginService;
    /**
     * 是否具有编辑权限
     *
     * @param wikiSpaceSel
     * @param editType
     * @param pageId
     * @param currentUserId
     * @return
     */
    //todo 修改页面可编辑的权限
    public String canEdit(WikiSpace wikiSpaceSel, Integer editType, Long pageId, Long currentUserId) {
        if (wikiSpaceSel == null || Objects.equals(editType, 1)) {
            return "当前页面不允许编辑！";
        }
        // 私人空间不允许调用接口获取文章
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUserId, wikiSpaceSel.getCreateUserId())) {
            return "您没有权限修改该空间的文章！";
        }
        // 空间不是自己的，也没有权限
        if (SpaceType.isOthersPersonal(wikiSpaceSel.getType(), currentUserId, wikiSpaceSel.getCreateUserId())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            // 检查权限
            if (CollectionUtils.isEmpty(authorities)) {
                return "您没有修改该文章的权限！";
            }
            boolean pageAuth = authorities.stream().anyMatch(auth ->
                    Objects.equals(auth.getAuthority(), WikiAuthType.EDIT_PAGE.getCode()));
            if (!pageAuth) {
                return "您没有修改该文章的权限！";
            }
        }
        return null;
    }

    /**
     * 是否具有权限编辑权限
     *
     * @param wikiSpaceSel
     * @param pageId
     * @param currentUserId
     * @return
     */
    public String canConfigAuth(WikiSpace wikiSpaceSel, Long pageId, Long currentUserId) {
        if (!SpaceType.isPersonal(wikiSpaceSel.getType())) {
            return "只有个人空间才可以编辑权限";
        }
        if (!Objects.equals(currentUserId, wikiSpaceSel.getCreateUserId())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            // 检查权限
            if (CollectionUtils.isEmpty(authorities)) {
                return "您没有修改权限的权限！";
            }
            boolean pageAuth = authorities.stream().anyMatch(auth ->
                    Objects.equals(auth.getAuthority(), WikiAuthType.PAGE_AUTH_MANAGE.getCode()));
            if (!pageAuth) {
                return "您不是创建人或没有权限！";
            }
        }
        return null;
    }

    /**
     * 是否具有附件上传权限
     *
     * @param wikiSpaceSel
     * @param pageId
     * @param currentUserId
     * @return
     */
    public String canUploadFile(WikiSpace wikiSpaceSel, Long pageId, Long currentUserId) {
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUserId, wikiSpaceSel.getCreateUserId())) {
            return "您没有该空间的文件上传权限！";
        }
        // 空间不是自己的，也没有权限
        if (SpaceType.isOthersPersonal(wikiSpaceSel.getType(), currentUserId, wikiSpaceSel.getCreateUserId())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            // 检查权限
            if (CollectionUtils.isEmpty(authorities)) {
                return "您没有修改该文章的权限！";
            }
            boolean pageAuth = authorities.stream().anyMatch(auth ->
                    Objects.equals(auth.getAuthority(), WikiAuthType.PAGE_FILE_UPLOAD.getCode()));
            if (!pageAuth) {
                return "您没有修改该文章的权限！";
            }
        }
        return null;
    }

    /**
     * 是否具有附件删除权限
     *
     * @param wikiSpaceSel
     * @param pageId
     * @param currentUserId
     * @return
     */
    public String canDeleteFile(WikiSpace wikiSpaceSel, Long pageId, Long currentUserId) {
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUserId, wikiSpaceSel.getCreateUserId())) {
            return "您没有该空间的文件上传权限！";
        }
        // 空间不是自己的，也没有权限
        if (SpaceType.isOthersPersonal(wikiSpaceSel.getType(), currentUserId, wikiSpaceSel.getCreateUserId())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            // 检查权限
            if (CollectionUtils.isEmpty(authorities)) {
                return "您没有修改该文章的权限！";
            }
            boolean pageAuth = authorities.stream().anyMatch(auth ->
                    Objects.equals(auth.getAuthority(), WikiAuthType.PAGE_FILE_DELETE.getCode()));
            if (!pageAuth) {
                return "您没有修改该文章的权限！";
            }
        }
        return null;
    }

    /**
     * 是否具有删除权限
     *
     * @param wikiSpaceSel
     * @param editType
     * @param pageId
     * @param currentUserId
     * @return
     */
    public String canDelete(WikiSpace wikiSpaceSel, Integer editType, Long pageId, Long currentUserId) {
        if (wikiSpaceSel == null || Objects.equals(editType, 1)) {
            return "当前页面不允许编辑！";
        }
        // 私人空间不允许调用接口获取文章
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUserId, wikiSpaceSel.getCreateUserId())) {
            return "您没有权限修改该空间的文章！";
        }
        // 空间不是自己的，也没有权限
        if (SpaceType.isOthersPersonal(wikiSpaceSel.getType(), currentUserId, wikiSpaceSel.getCreateUserId())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (CollectionUtils.isEmpty(authorities)) {
                return "您没有删除该文章的权限！";
            }
            boolean pageAuth = authorities.stream().anyMatch(auth ->
                    Objects.equals(auth.getAuthority(), WikiAuthType.DELETE_PAGE.getCode()));
            if (!pageAuth) {
                return "您没有修改该文章的权限！";
            }
        }
        return null;
    }
}
