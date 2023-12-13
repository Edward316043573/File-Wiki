package top.cxscoder.boot.controller.wiki;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.anotation.AuthMan;
import top.cxscoder.wiki.common.constant.DocSysType;
import top.cxscoder.wiki.common.constant.UserMsgType;
import top.cxscoder.wiki.domain.entity.UserMessage;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageZan;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.framework.consts.SpaceType;
import top.cxscoder.wiki.service.manage.UserMessageService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiPageZanService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

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
@RequestMapping("/wiki/page/zan")
@RequiredArgsConstructor
public class WikiPageZanController {

    private final WikiPageZanService wikiPageZanService;
    private final WikiSpaceService wikiSpaceService;
    private final WikiPageService wikiPageService;
    private final UserMessageService userMessageService;

    private final LoginService loginService;

    @PostMapping("/list")
    public List<WikiPageZan> list(@RequestBody WikiPageZan wikiPageZan) {
        User currentUser = loginService.getCurrentUser();
        WikiPage wikiPageSel = wikiPageService.getById(wikiPageZan.getPageId());
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            throw new ServiceException("您没有获取该空间的点赞列表权限！");
        }
        UpdateWrapper<WikiPageZan> wrapper = new UpdateWrapper<>();
        wrapper.eq("page_id", wikiPageZan.getPageId());
        wrapper.eq(wikiPageZan.getCommentId() != null, "comment_id", wikiPageZan.getCommentId());
        wrapper.eq("yn", 1);
        List<WikiPageZan> zanList = wikiPageZanService.list(wrapper);
        return zanList;
    }

    @PostMapping("/update")
    @Transactional
    public void update(@RequestBody WikiPageZan wikiPageZan) {
        User currentUser = loginService.getCurrentUser();
        Long id = wikiPageZan.getId();
        Long pageId;
        if (id != null && id > 0) {
            WikiPageZan wikiPageZanSel = wikiPageZanService.getById(id);
            pageId = wikiPageZanSel.getPageId();
        } else if (wikiPageZan.getPageId() != null) {
            pageId = wikiPageZan.getPageId();
        } else {
            throw new ServiceException("需指定所属页面！");
        }
        WikiPage wikiPageSel = wikiPageService.getById(pageId);
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            throw new ServiceException("您没有获取该空间的点赞列表权限！");
        }
        wikiPageZanService.zanPage(wikiPageZan);
        // 给相关人发送消息
        UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPageSel.getId(), wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_ZAN);
        if (!Objects.equals(wikiPageZan.getYn(), 1)) {
            userMessage.setMsgType(UserMsgType.WIKI_PAGE_ZAN_CANCEL.getType());
        }
        userMessage.setAffectUserId(wikiPageSel.getCreateUserId());
        userMessage.setAffectUserName(wikiPageSel.getCreateUserName());
        userMessageService.addWikiMessage(userMessage);
    }
}

