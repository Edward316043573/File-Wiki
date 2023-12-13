package top.cxscoder.boot.controller.wiki;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.anotation.AuthMan;
import top.cxscoder.wiki.common.constant.DocSysType;
import top.cxscoder.wiki.common.constant.UserMsgType;
import top.cxscoder.wiki.domain.entity.UserMessage;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageComment;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.framework.consts.SpaceType;
import top.cxscoder.wiki.json.DocResponseJson;
import top.cxscoder.wiki.json.ResponseJson;
import top.cxscoder.wiki.service.manage.UserMessageService;
import top.cxscoder.wiki.service.manage.WikiPageCommentService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 文档控制器
 *
 * @author 暮光：城中城
 * @since 2019年2月17日
 */
@Slf4j
@AuthMan
@RestController
@RequestMapping("/wiki/page/comment")
@RequiredArgsConstructor
public class WikiPageCommentController {

    @Resource
    private final WikiPageCommentService wikiPageCommentService;
    @Resource
    private final WikiSpaceService wikiSpaceService;
    @Resource
    private final WikiPageService wikiPageService;
    @Resource
    private final UserMessageService userMessageService;

    @Resource
    private LoginService loginService;

    //	@PreAuthorize("hasAnyAuthority('wiki:comment:list')")
    @PostMapping("/list")
    public List<WikiPageComment> list(@RequestBody WikiPageComment pageComment) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        WikiPage wikiPageSel = wikiPageService.getById(pageComment.getPageId());
        // 页面已删除
        if (wikiPageSel == null || Objects.equals(wikiPageSel.getDelFlag(), 1)) {
            return null;
        }
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 空间已删除
        if (wikiSpaceSel == null || Objects.equals(wikiSpaceSel.getDelFlag(), 1)) {
            return null;
        }
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            throw new ServiceException("您没有查看该空间的评论权！");
        }
        UpdateWrapper<WikiPageComment> wrapper = new UpdateWrapper<>();
        wrapper.eq("del_flag", 0);
        wrapper.eq("page_id", pageComment.getPageId());
        wrapper.eq(pageComment.getParentId() != null, "parent_id", pageComment.getParentId());
        List<WikiPageComment> pageCommentList = wikiPageCommentService.list(wrapper);
        // 取消二级评论，全展示在一级
//		Map<Long, List<WikiPageComment>> listMap = pageCommentList.stream().filter(val -> val.getParentId() != null)
//				.collect(Collectors.groupingBy(WikiPageComment::getParentId));
//		List<WikiPageCommentVo> commentList = pageCommentList.stream().filter(val -> val.getParentId() == null)
//				.map(val -> mapper.map(val, WikiPageCommentVo.class)).collect(Collectors.toList());
//		for (WikiPageCommentVo commentVo : commentList) {
//			commentVo.setCommentList(listMap.get(commentVo.getId()));
//		}
        return pageCommentList;
    }

    //	@PreAuthorize("hasAnyAuthority('wiki:comment:delete')")
    @DeleteMapping("/{id}}")
    @Transactional
    public ResponseJson<Object> delete(@PathVariable Long id) {
        WikiPageComment pageCommentSel = wikiPageCommentService.getById(id);
        WikiPage wikiPageSel = wikiPageService.getById(pageCommentSel.getPageId());
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        if (!Objects.equals(pageCommentSel.getCreateUserId(), currentUser.getUserId())) {
            if (!Objects.equals(currentUser.getUserId(), wikiPageSel.getCreateUserId())) {
                return DocResponseJson.warn("只有评论人或页面创建人才有权限删除此评论！");
            }
        }
        WikiPageComment pageComment = new WikiPageComment();
        pageComment.setId(id);
        pageComment.setDelFlag(1);
        wikiPageCommentService.updateById(pageComment);
        // 给相关人发送消息
        UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPageSel.getId(), wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_COMMENT_DEL);
        userMessage.setAffectUserId(wikiPageSel.getCreateUserId());
        userMessage.setAffectUserName(wikiPageSel.getCreateUserName());
        userMessageService.addWikiMessage(userMessage);
        return DocResponseJson.ok();
    }

    //	@PreAuthorize("hasAnyAuthority('wiki:comment:update')")
    @PostMapping("/update")
	@Transactional
    public void update(@RequestBody WikiPageComment pageComment) {
        User currentUser = loginService.getCurrentUser();
        Long id = pageComment.getId();
        Long pageId;
        if (id != null && id > 0) {
            WikiPageComment pageCommentSel = wikiPageCommentService.getById(id);
            if (!Objects.equals(pageCommentSel.getCreateUserId(), currentUser.getUserId())) {
                throw new ServiceException("只能修改自己的评论！");
            }
            pageId = pageCommentSel.getPageId();
        } else if (pageComment.getPageId() != null) {
            pageId = pageComment.getPageId();
        } else {
            throw new ServiceException("需指定所属页面！");
        }
        WikiPage wikiPageSel = wikiPageService.getById(pageId);
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            throw new ServiceException("您没有该空间的评论权！");
        }
        // 空间不是自己的，也没有权限，感觉评论没必要加权限，先去掉
//		if (SpaceType.isOthersPersonal(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
//			boolean pageAuth = DocUserUtil.haveCustomAuth(WikiAuthType.COMMENT_PAGE.getName(), pageId);
//			if (!pageAuth) {
//				return DocResponseJson.warn("您没有评论该文章的权限！");
//			}
//		}
        if (id != null && id > 0) {
            pageComment.setDelFlag(0);
            wikiPageCommentService.updateById(pageComment);
        } else {
            pageComment.setCreateTime(new Date());
            pageComment.setCreateUserId(currentUser.getUserId());
            pageComment.setCreateUserName(currentUser.getUserName());
            wikiPageCommentService.save(pageComment);
        }
        // 给相关人发送消息
        UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPageSel.getId(), wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_COMMENT);
        userMessage.setAffectUserId(wikiPageSel.getCreateUserId());
        userMessage.setAffectUserName(wikiPageSel.getCreateUserName());
        userMessageService.addWikiMessage(userMessage);
    }
}

