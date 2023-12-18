package top.cxscoder.boot.controller.wiki;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.domain.SearchByEsParam;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageContent;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.domain.vo.SpaceNewsVo;
import top.cxscoder.wiki.domain.vo.WikiPageContentVo;
import top.cxscoder.wiki.domain.vo.WikiPageVo;
import top.cxscoder.wiki.repository.mapper.WikiPageContentMapper;
import top.cxscoder.wiki.service.manage.WikiPageContentService;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

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
@RestController
@RequestMapping("/wiki/open-api")
@RequiredArgsConstructor
public class WikiOpenApiController {

    private final WikiPageService wikiPageService;
    private final WikiSpaceService wikiSpaceService;
    private final WikiPageContentService wikiPageContentService;
    private final WikiPageFileService wikiPageFileService;
    private final WikiPageContentMapper wikiPageContentMapper;

    @Resource
    LoginService loginService;

    @PostMapping("/space/info")
    public WikiSpace spaceInfo(String space) {
        WikiSpace wikiSpace = this.getWikiSpace(space);
        if (wikiSpace == null) {
            throw new ServiceException("未找到该文档");
        }
        return wikiSpace;
    }

    @PostMapping("/page/news")
    public List<SpaceNewsVo> news(@RequestBody SearchByEsParam param, String space) {
        WikiSpace wikiSpace = this.getWikiSpace(space);
        if (wikiSpace == null) {
          throw new ServiceException("未找到该文档");
        }
        String keywords = param.getKeywords();
        if (StringUtils.isNotBlank(keywords)) {
            param.setKeywords("%" + keywords + "%");
        }
        // 分页查询
        param.setSpaceIds(Collections.singletonList(wikiSpace.getId()));
        List<SpaceNewsVo> spaceNewsVoList = wikiPageContentMapper.getNewsList(param);
        if (CollectionUtils.isNotEmpty(spaceNewsVoList)) {
            spaceNewsVoList.forEach(val -> {
                val.setSpace(wikiSpace.getUuid());
                val.setSpaceName(wikiSpace.getName());
                String preview = val.getPreviewContent();
                if (preview != null) {
                    if (preview.length() > 200) {
                        preview = preview.substring(0, 200);
                    }
                    if (keywords != null) {
                        preview = StringUtils.replaceIgnoreCase(preview, keywords, "<span style=\"color:red\">" + keywords + "</span>");
                    }
                }
                val.setPreviewContent(preview);
                String pageTitle = val.getPageTitle();
                if (pageTitle != null && keywords != null) {
                    pageTitle = StringUtils.replaceIgnoreCase(pageTitle, keywords, "<span style=\"color:red\">" + keywords + "</span>");
                }
                val.setPageTitle(pageTitle);
            });
        }
        return spaceNewsVoList;
    }

    @PostMapping("/page/list")
    public List<WikiPageVo> list(String space) {
        WikiSpace wikiSpace = this.getWikiSpace(space);
        if (wikiSpace == null) {
           throw new ServiceException("未找到该文档");
        }
        QueryWrapper<WikiPage> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", 0);
        wrapper.eq("space_id", wikiSpace.getId());
        List<WikiPage> wikiPageList = wikiPageService.list(wrapper);
        if (CollectionUtils.isEmpty(wikiPageList)) {
            return null;
        }
        Map<Long, List<WikiPageVo>> listMap = wikiPageList.stream().map(WikiPageVo::new).collect(Collectors.groupingBy(WikiPageVo::getParentId));
        List<WikiPageVo> nodePageList = listMap.get(0L);
        if (CollectionUtils.isNotEmpty(nodePageList)) {
            nodePageList = nodePageList.stream().sorted(Comparator.comparingInt(WikiPageVo::getSeqNo)).collect(Collectors.toList());
            this.setChildren(listMap, nodePageList);
        }
        return nodePageList;
    }

    @PostMapping("/page/detail")
    @Transactional
    public WikiPageContentVo  detail(String space, Long pageId) {
        WikiSpace wikiSpace = this.getWikiSpace(space);
        if (wikiSpace == null) {
            throw new ServiceException("未找到该文档");
        }
        WikiPage wikiPageSel = wikiPageService.getById(pageId);
        // 不存在或不属于该空间
        if (wikiPageSel == null || !Objects.equals(wikiPageSel.getSpaceId(), wikiSpace.getId())) {
            throw new ServiceException("未找到该文档");
        }
        UpdateWrapper<WikiPageContent> wrapper = new UpdateWrapper<>();
        wrapper.eq("page_id", pageId);
        WikiPageContent pageContent = wikiPageContentService.getOne(wrapper);
        UpdateWrapper<WikiPageFile> wrapperFile = new UpdateWrapper<>();
        wrapperFile.eq("page_id", pageId);
        wrapperFile.eq("del_flag", 0);
        List<WikiPageFile> pageFiles = wikiPageFileService.list(wrapperFile);
        for (WikiPageFile pageFile : pageFiles) {
            pageFile.setFileUrl("wiki/common/file?uuid=" + pageFile.getUuid());
        }
        // 高并发下会有覆盖问题，但不重要~
        int viewNum = Optional.ofNullable(wikiPageSel.getViewNum()).orElse(0);
        WikiPage wikiPageUp = new WikiPage();
        wikiPageUp.setId(wikiPageSel.getId());
        wikiPageUp.setViewNum(viewNum + 1);
        wikiPageService.updateById(wikiPageUp);
        // 修改返回值里的查看数+1
        wikiPageSel.setViewNum(viewNum + 1);
        WikiPageContentVo vo = new WikiPageContentVo();
        vo.setWikiPage(wikiPageSel);
        vo.setPageContent(pageContent);
        vo.setFileList(pageFiles);
        return vo;
    }

    private void setChildren(Map<Long, List<WikiPageVo>> listMap, List<WikiPageVo> nodePageList) {
        if (nodePageList == null || listMap == null) {
            return;
        }
        for (WikiPageVo page : nodePageList) {
            List<WikiPageVo> wikiPageVos = listMap.get(page.getId());
            if (CollectionUtils.isNotEmpty(wikiPageVos)) {
                wikiPageVos = wikiPageVos.stream().sorted(Comparator.comparingInt(WikiPageVo::getSeqNo)).collect(Collectors.toList());
                page.setChildren(wikiPageVos);
                this.setChildren(listMap, wikiPageVos);
            }
        }
    }

    /**
     * 获取空间信息
     *
     * @param space
     * @return
     */
    private WikiSpace getWikiSpace(String space) {
        QueryWrapper<WikiSpace> wrapperSpace = new QueryWrapper<>();
        wrapperSpace.eq("uuid", space);
        wrapperSpace.eq("del_flag", 0);
        WikiSpace wikiSpace = wikiSpaceService.getOne(wrapperSpace);
        // 不存在或未开放
        if (wikiSpace == null || wikiSpace.getOpenDoc() != 1) {
            return null;
        }
        return wikiSpace;
    }
}

