package top.cxscoder.boot.controller.wiki;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.wiki.anotation.AuthMan;
import top.cxscoder.wiki.framework.consts.SpaceType;
import top.cxscoder.wiki.json.DocResponseJson;
import top.cxscoder.wiki.json.ResponseJson;
import top.cxscoder.wiki.repository.manage.entity.UserMessage;
import top.cxscoder.wiki.repository.manage.entity.WikiPage;
import top.cxscoder.wiki.repository.manage.entity.WikiPageZan;
import top.cxscoder.wiki.repository.manage.entity.WikiSpace;
import top.cxscoder.wiki.repository.support.consts.DocSysType;
import top.cxscoder.wiki.repository.support.consts.UserMsgType;
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

    @PostMapping("/list")
    public ResponseJson<List<WikiPageZan>> list(@RequestBody WikiPageZan wikiPageZan) {

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         User currentUser = loginUser.getUser();
        WikiPage wikiPageSel = wikiPageService.getById(wikiPageZan.getPageId());
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            return DocResponseJson.warn("您没有获取该空间的点赞列表权限！");
        }
        UpdateWrapper<WikiPageZan> wrapper = new UpdateWrapper<>();
        wrapper.eq("page_id", wikiPageZan.getPageId());
        wrapper.eq(wikiPageZan.getCommentId() != null, "comment_id", wikiPageZan.getCommentId());
        wrapper.eq("yn", 1);
        List<WikiPageZan> zanList = wikiPageZanService.list(wrapper);
        return DocResponseJson.ok(zanList);
    }

    @PostMapping("/update")
    public ResponseJson<Object> update(@RequestBody WikiPageZan wikiPageZan) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        Long id = wikiPageZan.getId();
        Long pageId;
        if (id != null && id > 0) {
            WikiPageZan wikiPageZanSel = wikiPageZanService.getById(id);
            pageId = wikiPageZanSel.getPageId();
        } else if (wikiPageZan.getPageId() != null) {
            pageId = wikiPageZan.getPageId();
        } else {
            return DocResponseJson.warn("需指定所属页面！");
        }
        WikiPage wikiPageSel = wikiPageService.getById(pageId);
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            return DocResponseJson.warn("您没有该空间的点赞权限！");
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
        return DocResponseJson.ok();
    }
}

