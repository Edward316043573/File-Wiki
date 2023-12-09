package top.cxscoder.boot.controller.wiki;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.wiki.json.DocResponseJson;
import top.cxscoder.wiki.json.ResponseJson;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageContent;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiPageTemplate;
import top.cxscoder.wiki.domain.vo.WikiPageTemplateInfoVo;
import top.cxscoder.wiki.domain.vo.WikiTemplateTagVo;
import top.cxscoder.wiki.service.WikiPageUploadService;
import top.cxscoder.wiki.service.manage.WikiPageContentService;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiPageTemplateService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 *     模板控制器
 * </p>
 *
 * @author Sh1yu
 * @since 2023-08-24
 */
@Slf4j
@RestController
@RequestMapping("/wiki/template")
@RequiredArgsConstructor
public class WikiPageTemplateController {

    private final WikiPageService wikiPageService;
    private final WikiPageContentService wikiPageContentService;
    private final WikiPageUploadService wikipageUploadService;
    private final WikiPageTemplateService wikiPageTemplateService;
    private final WikiPageFileService wikiPageFileService;


    @PostMapping("/add")
    public ResponseJson<Object> addTemplate(WikiPageTemplate wikiPageTemplate) {
        WikiPageTemplate exist = wikiPageTemplateService.getWikiPageTemplateBySpaceAndPage(wikiPageTemplate.getSpaceId(), wikiPageTemplate.getPageId());
        if (null == exist) {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = loginUser.getUser();
            if (wikiPageTemplate.getTagName().isEmpty()) {
                wikiPageTemplate.setTagName("无标签");
            }
            wikiPageTemplate.setCreated(new Date());
            wikiPageTemplate.setCreateUser(currentUser.getUserName());
            wikiPageTemplate.setCreateUserId(currentUser.getUserId());
            wikiPageTemplateService.save(wikiPageTemplate);
        } else {
            exist.setTagName(wikiPageTemplate.getTagName());
            exist.setShareStatus(wikiPageTemplate.getShareStatus());
            wikiPageTemplateService.updateById(exist);
        }
        return DocResponseJson.ok();
    }

    @PostMapping("/allTags")
    public ResponseJson<Object> allTags(boolean open) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
User currentUser = loginUser.getUser();

        List<WikiTemplateTagVo> allTags = wikiPageTemplateService.getAllTags(currentUser.getUserId(),open);
        return DocResponseJson.ok(allTags);
    }

    @PostMapping("/filterAll")
    public ResponseJson<Object> filterAll(String name, boolean open, HttpServletRequest request, Long pageNum) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
User currentUser = loginUser.getUser();
        List tagList = new ArrayList();
        Set<Map.Entry<String, String[]>> entries = request.getParameterMap().entrySet();
        entries.forEach(param -> {
            if (param.getKey().contains("].tagName") && !"".equals(param.getValue()[0])) {
                tagList.add(param.getValue()[0]);
            }
        });
        if (tagList.isEmpty()) {
            tagList.add("");
        }
        List<WikiPageTemplateInfoVo> wikiPageTemplateInfoVos = wikiPageTemplateService.filterAll(currentUser.getUserId(), name, open, tagList, pageNum);
        Long total = wikiPageTemplateService.total(currentUser.getUserId(), name, open, tagList);
        DocResponseJson<Object> ok = DocResponseJson.ok(wikiPageTemplateInfoVos);
        ok.setTotal(total);
        return ok;
    }

    @PostMapping("/use")
    public ResponseJson<Object> use(Long spaceId, Long parentId, String templateId) {
        WikiPageTemplate template = wikiPageTemplateService.getById(templateId);
        WikiPage wikiTemplatePage = wikiPageService.getById(template.getPageId());
        WikiPage wikiPage = new WikiPage();
        wikiPage.setParentId(parentId);
        wikiPage.setSpaceId(spaceId);
        wikiPage.setEditorType(wikiTemplatePage.getEditorType());
        wikiPage.setName(wikiTemplatePage.getName() + "副本");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("page_id", wikiTemplatePage.getId());
        WikiPageContent pageContent = wikiPageContentService.getOne(queryWrapper);
        UpdateWrapper<WikiPageFile> wrapperFile = new UpdateWrapper<>();
        wrapperFile.eq("page_id", wikiTemplatePage.getId());
        List<WikiPageFile> pageFiles = wikiPageFileService.list(wrapperFile);
        String content = pageContent.getContent();
        for (WikiPageFile pageFile : pageFiles) {
            pageFile.setId(null);
            String uuid = IdUtil.simpleUUID();
            content.replace(pageFile.getUuid(),uuid);
            pageFile.setUuid(uuid);
        }
        Object info = wikipageUploadService.update(wikiPage, content, content);
        if (null != info) {
            if (info instanceof WikiPage) {
                for (WikiPageFile pageFile : pageFiles) {
                    pageFile.setPageId(((WikiPage)info).getId());
                    wikiPageFileService.save(pageFile);
                }
                return DocResponseJson.ok(info);
            }
            DocResponseJson.warn((String) info);
        }
        return DocResponseJson.ok("状态异常");
    }

}

