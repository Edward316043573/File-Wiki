package top.cxscoder.boot.controller.wiki;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.common.constant.DocSysType;
import top.cxscoder.wiki.common.constant.UserMsgType;
import top.cxscoder.wiki.domain.DTO.WikiPageDTO;
import top.cxscoder.wiki.domain.SearchByEsParam;
import top.cxscoder.wiki.domain.entity.*;
import top.cxscoder.wiki.domain.vo.SpaceNewsVo;
import top.cxscoder.wiki.domain.vo.WikiPageContentVo;
import top.cxscoder.wiki.domain.vo.WikiPageTemplateInfoVo;
import top.cxscoder.wiki.domain.vo.WikiPageVo;
import top.cxscoder.wiki.enums.PageFileSource;
import top.cxscoder.wiki.framework.consts.SpaceType;
import top.cxscoder.wiki.repository.mapper.WikiPageContentMapper;
import top.cxscoder.wiki.repository.mapper.WikiPageMapper;
import top.cxscoder.wiki.service.WikiPageUploadService;
import top.cxscoder.wiki.service.common.WikiPageAuthService;
import top.cxscoder.wiki.service.manage.*;
import top.cxscoder.wiki.uitls.CachePrefix;
import top.cxscoder.wiki.uitls.CacheUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档控制器
 *
 * @author 暮光：城中城
 * @author Sh1yu
 * @since 2019年2月17日
 */
@Slf4j
//@AuthMan
@RestController
@RequestMapping("/wiki/page")
@RequiredArgsConstructor
public class WikiPageController {

    private final WikiPageService wikiPageService;
    private final WikiPageContentService wikiPageContentService;
    private final WikiPageContentMapper wikiPageContentMapper;
    private final WikiPageFileService wikiPageFileService;
    private final WikiPageZanService wikiPageZanService;
    private final WikiSpaceService wikiSpaceService;
    private final WikiPageAuthService wikiPageAuthService;
    private final WikiPageUploadService wikipageUploadService;
    private final UserMessageService userMessageService;
    private final WikiPageHistoryService wikiPageHistoryService;
    private final WikiPageMapper wikiPageMapper;
    private final WikiPageCommentService wikiPageCommentService;
    private final WikiPageTemplateService wikiPageTemplateService;
    private final LoginService loginService;

    @Value("${wiki.upload-path:}")
    private String uploadPath;

//    @PreAuthorize("hasAnyAuthority('wiki:page:list')")
    @PostMapping("/list")
    public List<WikiPageVo> list(@RequestBody WikiPage wikiPage) {
        User currentUser = loginService.getCurrentUser();
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPage.getSpaceId());
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            throw new ServiceException("您没有权限查看该空间的文章列表！");
        }

        List<WikiPageTemplateInfoVo> wikiPageList = wikiPageService.wikiPageTemplateInfos(wikiPage.getSpaceId());
        Map<Long, List<WikiPageVo>> listMap = wikiPageList.stream().map(WikiPageVo::new).collect(Collectors.groupingBy(WikiPageVo::getParentId));
        List<WikiPageVo> nodePageList = listMap.get(0L);
        if (CollectionUtils.isNotEmpty(nodePageList)) {
            nodePageList = nodePageList.stream().sorted(Comparator.comparingInt(WikiPageVo::getSeqNo)).collect(Collectors.toList());
            this.setChildren(listMap, nodePageList, "");
        }
        return nodePageList;
    }

//    @PreAuthorize("hasAnyAuthority('wiki:page:detail')")
    @PostMapping("/detail")
    public WikiPageContentVo detail(@RequestBody WikiPage wikiPage) {
        User currentUser = loginService.getCurrentUser();
        // TODO 加缓存提高速度
        WikiPage wikiPageSel = wikiPageService.getById(wikiPage.getId());
        // 页面已删除
        if (wikiPageSel == null || Objects.equals(wikiPageSel.getDelFlag(), 1)) {
            throw new ServiceException("该页面不存在或已删除！");
        }
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 空间已删除
        // 加缓存提高速度
        if (wikiSpaceSel == null || Objects.equals(wikiSpaceSel.getDelFlag(), 1)) {
            throw new ServiceException("该页面不存在或已删除！");
        }
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
            throw new ServiceException("您没有权限查看该空间的文章详情！");
        }
        LambdaQueryWrapper<WikiPageContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WikiPageContent::getPageId, wikiPage.getId());
        WikiPageContent pageContent = wikiPageContentService.getOne(wrapper);
        // 查询附件
        LambdaQueryWrapper<WikiPageFile> wrapperFile = new LambdaQueryWrapper<>();
        wrapperFile.eq(WikiPageFile::getPageId, wikiPage.getId());
        wrapperFile.eq(WikiPageFile::getDelFlag, 0);
        wrapperFile.eq(WikiPageFile::getFileSource, PageFileSource.UPLOAD_FILES.getSource());
        List<WikiPageFile> pageFiles = wikiPageFileService.list(wrapperFile);
        for (WikiPageFile pageFile : pageFiles) {
            pageFile.setFileUrl("wiki/common/file?uuid=" + pageFile.getUuid());
        }
        LambdaQueryWrapper<WikiPageZan> wrapperZan = new LambdaQueryWrapper<>();
        wrapperZan.eq(WikiPageZan::getPageId, wikiPage.getId());
        wrapperZan.eq(WikiPageZan::getCreateUserId, currentUser.getUserId());
        wrapperZan.eq(WikiPageZan::getYn, 1);
        WikiPageZan pageZan = wikiPageZanService.getOne(wrapperZan);
        WikiPageContentVo vo = new WikiPageContentVo();
        vo.setWikiPage(wikiPageSel);
        vo.setPageContent(pageContent);
        vo.setFileList(pageFiles);
        vo.setSelfZan((pageZan != null) ? 1 : 0);
        vo.setSelfUserId(currentUser.getUserId());
        // 上传附件、编辑、权限设置、删除 的权限
        String canEdit = wikiPageAuthService.canEdit(wikiSpaceSel, wikiPageSel.getEditType(), wikiPageSel.getId(), currentUser.getUserId());
        String canDelete = wikiPageAuthService.canDelete(wikiSpaceSel, wikiPageSel.getEditType(), wikiPageSel.getId(), currentUser.getUserId());
        String canUploadFile = wikiPageAuthService.canUploadFile(wikiSpaceSel, wikiPageSel.getId(), currentUser.getUserId());
        String canDeleteFile = wikiPageAuthService.canDeleteFile(wikiSpaceSel, wikiPageSel.getId(), currentUser.getUserId());
        String canConfigAuth = wikiPageAuthService.canConfigAuth(wikiSpaceSel, wikiPageSel.getId(), currentUser.getUserId());
        // 如果为空代表有权限，否则表示没权限
        vo.setCanEdit((canEdit == null) ? 1 : 0);
        vo.setCanDelete((canDelete == null) ? 1 : 0);
        vo.setCanDeleteFile((canDeleteFile == null) ? 1 : 0);
        vo.setCanUploadFile((canUploadFile == null) ? 1 : 0);
        vo.setCanConfigAuth((canConfigAuth == null) ? 1 : 0);
        // 高并发下会有覆盖问题，但不重要~
        int viewNum = Optional.ofNullable(wikiPageSel.getViewNum()).orElse(0);
        WikiPage wikiPageUp = new WikiPage();
        wikiPageUp.setId(wikiPageSel.getId());
        wikiPageUp.setViewNum(viewNum + 1);
        wikiPageService.updateById(wikiPageUp);
        // 修改返回值里的查看数+1
        wikiPageSel.setViewNum(viewNum + 1);
        return vo;
    }

    @PostMapping("/changeParent")
    public void changeParent(@RequestBody WikiPage wikiPage, Integer beforeSeq, Integer afterSeq) {
        User currentUser = loginService.getCurrentUser();
        WikiPage wikiPageSel = wikiPageService.getById(wikiPage.getId());
        // 编辑权限判断
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        String canEdit = wikiPageAuthService.canEdit(wikiSpaceSel, wikiPageSel.getEditType(), wikiPageSel.getId(), currentUser.getUserId());
        if (canEdit != null) {
//            return DocResponseJson.warn(canEdit);
            throw new ServiceException(canEdit);
        }
        WikiPage wikiPageUp = new WikiPage();
        wikiPageUp.setId(wikiPage.getId());
        wikiPageUp.setParentId(wikiPage.getParentId());
        wikiPageUp.setUpdateTime(new Date());
        wikiPageUp.setUpdateUserId(currentUser.getUserId());
        wikiPageUp.setUpdateUserName(currentUser.getUserName());
        wikiPageService.changeParent(wikiPageUp, beforeSeq, afterSeq);
    }

    @DeleteMapping("/delete/{pageId}")
    @Transactional
    public void delete(@PathVariable Long pageId) {
        User currentUser = loginService.getCurrentUser();
        WikiPage wikiPageSel = wikiPageService.getById(pageId);
        // 删除权限判断
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // TODO 权限管理
        String canDelete = wikiPageAuthService.canDelete(wikiSpaceSel, wikiPageSel.getEditType(), wikiPageSel.getId(), currentUser.getUserId());
        if (canDelete != null) {
            throw new ServiceException(canDelete);
        }
        // 执行删除
        WikiPage wikiPage = new WikiPage();
        wikiPage.setId(pageId);
        wikiPage.setDelFlag(1);
        wikiPage.setName(wikiPageSel.getName());
        wikiPage.setUpdateTime(new Date());
        wikiPage.setUpdateUserId(currentUser.getUserId());
        wikiPage.setUpdateUserName(currentUser.getUserName());
        wikiPageService.deletePage(wikiPage);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("space_id", wikiPageSel.getSpaceId());
        queryWrapper.eq("page_id", wikiPageSel.getId());
        wikiPageTemplateService.remove(queryWrapper);
    }

    @PostMapping("/update")
    public Object update(@RequestBody WikiPageDTO wikiPageDTO) {
        WikiPage wikiPage = BeanUtil.copyProperties(wikiPageDTO, WikiPage.class);
        Object info = wikipageUploadService.update(wikiPage, wikiPageDTO.getContent(), wikiPageDTO.getPreview());
        if (null != info) {
            if (info instanceof WikiPage) {
                return info;
            }
            throw new ServiceException((String)info);
        }
        throw new ServiceException("状态异常");
    }

    public boolean isLassoDoll(WikiPage wikiPage, Long moveToPageId) {
        if (0L != moveToPageId) {
            if (wikiPage.getId().equals(moveToPageId)) {
                return true;
            }
            UpdateWrapper<WikiPage> wrapper = new UpdateWrapper<>();
            wrapper.eq("parent_id", wikiPage.getId());
            wrapper.eq("space_id", wikiPage.getSpaceId());
            //处理子节点也需要进行移动
            List<WikiPage> wikiPageList = wikiPageService.list(wrapper);
            for (WikiPage page : wikiPageList) {
                if (isLassoDoll(page, moveToPageId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PostMapping("/move")
    @Transactional
    public void move(@RequestBody WikiPage wikiPage, Long moveToPageId, Long moveToSpaceId) {
        if (isLassoDoll(wikiPage, moveToPageId)) {
//            return DocResponseJson.warn("不能移动自己到自己或自己的子节点下");
            throw new ServiceException("不能移动自己到自己或自己的子节点下");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        //获取原page信息
        WikiPage wikiPageSel = wikiPageService.getById(wikiPage.getId());
        wikiPageSel.setSpaceId(moveToSpaceId);
        wikiPageSel.setParentId(moveToPageId);
        wikiPageSel.setUpdateTime(new Date());
        wikiPageSel.setUpdateUserId(currentUser.getUserId());
        wikiPageSel.setUpdateUserName(currentUser.getUserName());
        wikiPageService.updateById(wikiPageSel);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("page_id",wikiPage.getId());
        queryWrapper.eq("space_id",wikiPage.getSpaceId());
        WikiPageTemplate bindTemplate = wikiPageTemplateService.getOne(queryWrapper);
        if(null != bindTemplate){
            bindTemplate.setSpaceId(moveToSpaceId);
            wikiPageTemplateService.updateById(bindTemplate);
        }

        UpdateWrapper<WikiPage> wrapper = new UpdateWrapper<>();
        wrapper.eq("parent_id", wikiPage.getId());
        wrapper.eq("space_id", wikiPage.getSpaceId());
        //处理子节点也需要进行移动
        List<WikiPage> wikiPageList = wikiPageService.list(wrapper);
        for (WikiPage page : wikiPageList) {
            move(page, wikiPageSel.getId() , moveToSpaceId);
        }
        // 给相关人发送消息
        UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPageSel.getId(), wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_MOVE);
        userMessageService.addWikiMessage(userMessage);
    }

    @PostMapping("/copy")
    public void copy(@RequestBody WikiPage wikiPage, Long moveToPageId, Long moveToSpaceId) {
        if (isLassoDoll(wikiPage, moveToPageId)) {
//            return DocResponseJson.warn("不能移动自己到自己或自己的子节点下");
            throw new ServiceException("不能移动自己到自己或自己的子节点下");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        //获取原page信息
        WikiPage wikiPageSel = wikiPageService.getById(wikiPage.getId());
        Integer lastSeq = wikiPageMapper.getLastSeq(wikiPage.getSpaceId(), moveToPageId);
        lastSeq = Optional.ofNullable(lastSeq).orElse(99999);
        wikiPageSel.setSeqNo(lastSeq + 1);
        wikiPageSel.setId(null);
        wikiPageSel.setSpaceId(moveToSpaceId);
        wikiPageSel.setParentId(moveToPageId);
        wikiPageSel.setCreateTime(new Date());
        wikiPageSel.setUpdateTime(new Date());
        wikiPageSel.setCreateUserId(currentUser.getUserId());
        wikiPageSel.setCreateUserName(currentUser.getUserName());
        wikiPageService.save(wikiPageSel);
        // 重置当前分支的所有节点seq值
        wikiPageMapper.updateChildrenSeq(wikiPage.getSpaceId(), moveToPageId);
        // 详情处理
        UpdateWrapper<WikiPageContent> wrapper = new UpdateWrapper<>();
        wrapper.eq("page_id", wikiPage.getId());
        WikiPageContent pageContent = wikiPageContentService.getOne(wrapper);
        pageContent.setId(null);
        pageContent.setPageId(wikiPageSel.getId());
        pageContent.setCreateTime(new Date());
        pageContent.setCreateUserId(currentUser.getUserId());
        pageContent.setCreateUserName(currentUser.getUserName());
        wikiPageContentService.save(pageContent);
        //文件
        UpdateWrapper<WikiPageFile> wrapperFile = new UpdateWrapper<>();
        wrapperFile.eq("page_id", wikiPageSel.getId());
        List<WikiPageFile> pageFiles = wikiPageFileService.list(wrapperFile);
        for (WikiPageFile pageFile : pageFiles) {
            pageFile.setId(null);
            pageFile.setUuid(IdUtil.simpleUUID());
            pageFile.setPageId(wikiPageSel.getId());
            wikiPageFileService.save(pageFile);
        }
        //点赞
        UpdateWrapper<WikiPageZan> wrapperZan = new UpdateWrapper<>();
        wrapperZan.eq("page_id", wikiPage.getId());
        List<WikiPageZan> list = wikiPageZanService.list(wrapperZan);
        for (WikiPageZan wikiPageZan : list) {
            wikiPageZan.setId(null);
            wikiPageZan.setPageId(wikiPageSel.getId());
            wikiPageZanService.save(wikiPageZan);
        }
        //评论
        UpdateWrapper<WikiPageComment> commentWrapper = new UpdateWrapper<>();
        commentWrapper.eq("page_id", wikiPageSel.getId());
        List<WikiPageComment> pageCommentList = wikiPageCommentService.list(commentWrapper);
        for (WikiPageComment wikiPageComment : pageCommentList) {
            wikiPageComment.setId(null);
            wikiPageComment.setPageId(wikiPageSel.getId());
            wikiPageCommentService.save(wikiPageComment);
        }
        //处理子节点也需要进行复制
        UpdateWrapper<WikiPage> childWrapper = new UpdateWrapper<>();
        childWrapper.eq("parent_id", wikiPage.getId());
        childWrapper.eq("space_id", wikiPage.getSpaceId());
        List<WikiPage> wikiPageList = wikiPageService.list(childWrapper);
        for (WikiPage page : wikiPageList) {
            copy(page, wikiPageSel.getId(), moveToSpaceId);
        }
        // 给相关人发送消息
        UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPageSel.getId(), wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_COPY);
        userMessageService.addWikiMessage(userMessage);
    }

    @PostMapping("/rename")
    public WikiPage rename(@RequestBody WikiPage wikiPage) {
        User currentUser = loginService.getCurrentUser();
        if (StringUtils.isBlank(wikiPage.getName())) {
//            return DocResponseJson.warn("标题不能为空！");
            throw new ServiceException("标题不能为空！");
        }
        if (StringUtils.isBlank(wikiPage.getId() + "")) {
//            return DocResponseJson.warn("不能为新建的文档改名！");
            throw new ServiceException("不能为新建的文档改名！");
        }
        //查询原文件夹的名字
        Long pageId = wikiPage.getId();

        WikiPage page = wikiPageService.getById(wikiPage.getId());
        Long spaceId = page.getSpaceId();
        String originFileName = page.getName();
        String newFileName = wikiPage.getName();

        // 1. 找到文件路径 文件路径格式 上传路径/空间ID/页面层次结构
        WikiSpace wikiSpace = wikiSpaceService.getById(spaceId);
        StringBuffer filePathBuffer = new StringBuffer();
        filePathBuffer.append(wikiSpace.getName()).append(File.separator);
        Long parentId = page.getParentId();
        Stack<String> s = new Stack<>();
        while(parentId != 0 ){
            WikiPage parentPage = wikiPageService.getById(parentId);
            String parentName = parentPage.getName();
            s.push(parentName);
            parentId = parentPage.getParentId();
        }
        while(!s.isEmpty()) {
            filePathBuffer.append(s.pop()).append(File.separator);
        }
        String originFilePath = filePathBuffer + originFileName;
        String newFilePath = filePathBuffer + newFileName;
        File originFile = new File(uploadPath+File.separator+originFilePath);
        File newFile = new File(uploadPath+File.separator+newFilePath);
        originFile.renameTo(newFile);

        //修改数据库中的文件夹名字
        page.setName(newFileName);
        wikiPageService.updateById(page);
        //查询该文件夹下的所有文件
        LambdaQueryWrapper<WikiPageFile> wrapper = new LambdaQueryWrapper<>();
        String tempPath = originFilePath.replace(File.separator,File.separator+File.separator);
        wrapper.likeRight(WikiPageFile::getFileUrl,tempPath);
        List<WikiPageFile> wikiPageFiles = wikiPageFileService.list(wrapper);
        wikiPageFiles= wikiPageFiles.stream().map(wikiPageFile -> {

            wikiPageFile.setFileUrl(wikiPageFile.getFileUrl().replace(originFilePath,newFilePath));
            return wikiPageFile;
        }).collect(Collectors.toList());
        for (WikiPageFile wikiPageFile : wikiPageFiles) {
            wikiPageFileService.updateById(wikiPageFile);
        }
        WikiPage wikiPageSel = wikiPageService.getById(pageId);
        // 编辑权限判断
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        String canEdit = wikiPageAuthService.canEdit(wikiSpaceSel, wikiPageSel.getEditType(), wikiPageSel.getId(), currentUser.getUserId());
        if (canEdit != null) {
            throw new ServiceException(canEdit);
        }
        spaceId = wikiPageSel.getSpaceId();
        WikiPage oldWikiPage = wikiPageService.getById(pageId);
        oldWikiPage.setName(wikiPage.getName());
        wikiPage.setUpdateTime(new Date());
        wikiPage.setUpdateUserId(currentUser.getUserId());
        wikiPage.setUpdateUserName(currentUser.getUserName());
        wikiPageService.updateById(oldWikiPage);
        UpdateWrapper<WikiPageContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("page_id", wikiPage.getId());
        WikiPageContent pageContent = wikiPageContentService.getOne(updateWrapper);
        // 给相关人发送消息
        UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPageSel.getId(), wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_UPDATE);
        userMessageService.addWikiMessage(userMessage);
        try {
            // 创建历史记录
            wikiPageHistoryService.saveRecord(spaceId, wikiPage.getId(), pageContent.getContent());
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        }
        return wikiPage;
    }

    @PostMapping("/unlock")
    public void unlock(Long pageId) {
        String lockKey = CachePrefix.WIKI_LOCK_PAGE + pageId;
        User pageLockUser = CacheUtil.get(lockKey);
        if (pageLockUser != null) {
            User currentUser = loginService.getCurrentUser();
            if (Objects.equals(pageLockUser.getUserId(), currentUser.getUserId())) {
                CacheUtil.remove(lockKey);
            }
        }
    }

    @PostMapping("/lock")
    public void editLock(Long pageId) {
        User currentUser = loginService.getCurrentUser();
        String lockKey = CachePrefix.WIKI_LOCK_PAGE + pageId;
        User pageLockUser = CacheUtil.get(lockKey);
        if (pageLockUser != null) {
            if (!Objects.equals(pageLockUser.getUserId(), currentUser.getUserId())) {
//                return DocResponseJson.warn("当前页面正在被：" + pageLockUser.getUsername() + " 编辑");
                throw new ServiceException("当前页面正在被：" + pageLockUser.getUserName() + " 编辑");
            }
        }
        CacheUtil.put(lockKey, new User(currentUser.getUserId(), currentUser.getUserName()));
    }

    @PostMapping("/searchByEs")
    public List<SpaceNewsVo> searchByEs(SearchByEsParam param) {
        param.setNewsType(1);
        return this.news(param);
    }

    @PostMapping("/download")
    public void download(Long pageId, HttpServletResponse response) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        WikiPage wikiPageSel = wikiPageService.getById(pageId);
        // 页面已删除
        if (wikiPageSel == null || Objects.equals(wikiPageSel.getDelFlag(), 1)) {
//            return DocResponseJson.warn("该页面不存在或已删除！");
            throw new ServiceException("该页面不存在或已删除！");
        }
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 空间已删除
        if (wikiSpaceSel == null || Objects.equals(wikiSpaceSel.getDelFlag(), 1)) {
//            return DocResponseJson.warn("该页面不存在或已删除！");
            throw new ServiceException("该页面不存在或已删除！");
        }
        // 私人空间
        if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
//            return DocResponseJson.warn("您没有权限查看该空间的文章详情！");
            throw new ServiceException("您没有权限查看该空间的文章详情！");
        }
        UpdateWrapper<WikiPageContent> wrapper = new UpdateWrapper<>();
        wrapper.eq("page_id", pageId);
        WikiPageContent pageContent = wikiPageContentService.getOne(wrapper);
        if (pageContent == null || StringUtils.isBlank(pageContent.getContent())) {
//            return DocResponseJson.warn("文档内容为空，不能导出！");
            throw new ServiceException("您没有权限查看该空间的文章详情！");
        }
        try {
            String content = pageContent.getContent();
            String fileName = URLEncoder.encode(wikiPageSel.getName(), "UTF-8");
            content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n" +
                    "<html lang=\"zh\">\n" +
                    "<head>\n" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                    "<title>" + fileName + "</title>\n" +
                    "</head>\n" +
                    "<body>" +
                    content +
                    "</body>\n" +
                    "</html>";
            // 写入流
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".docx");
            ServletOutputStream outputStream = response.getOutputStream();
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
            mdp.addAltChunk(AltChunkType.Xhtml, content.getBytes(StandardCharsets.UTF_8));
            mdp.convertAltChunks();
            XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true);
            wordMLPackage.save(outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
//        return DocResponseJson.warn("导出失败");
        throw new ServiceException("导出失败");
    }




    @PostMapping("/news")
    public List<SpaceNewsVo> news(@RequestBody SearchByEsParam param) {
        // 空间不是自己的
        Map<Long, WikiSpace> wikiSpaceMap = this.getCanVisitWikiSpace(param.getSpaceId());
        if (wikiSpaceMap.isEmpty()) {
            return null;
        }
        param.setSpaceIds(new ArrayList<>(wikiSpaceMap.keySet()));
        String keywords = param.getKeywords();
        if (StringUtils.isNotBlank(keywords)) {
            param.setKeywords("%" + keywords + "%");
        }
        // 分页查询
        List<SpaceNewsVo> spaceNewsVoList = wikiPageContentMapper.getNewsList(param);
        if (CollectionUtils.isEmpty(spaceNewsVoList)) {
            return null;
        }
        spaceNewsVoList.forEach(val -> {
            val.setSpaceName(wikiSpaceMap.get(val.getSpaceId()).getName());
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
        return spaceNewsVoList;
    }

    private Map<Long, WikiSpace> getCanVisitWikiSpace(Long spaceId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        List<WikiSpace> spaceList;
        // 空间不是自己的
        if (spaceId == null || spaceId <= 0) {
            QueryWrapper<WikiSpace> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            queryWrapper.ne("type", SpaceType.privateSpace);
            spaceList = wikiSpaceService.list(queryWrapper);
        } else {
            WikiSpace wikiSpaceSel = wikiSpaceService.getById(spaceId);
            if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
                return Collections.emptyMap();
            }
            spaceList = Collections.singletonList(wikiSpaceSel);
        }
        return spaceList.stream().collect(Collectors.toMap(WikiSpace::getId, val -> val));
    }

    private void setChildren(Map<Long, List<WikiPageVo>> listMap, List<WikiPageVo> nodePageList, String path) {
        if (nodePageList == null || listMap == null) {
            return;
        }
        for (WikiPageVo page : nodePageList) {
            String nowPath = path + "/" + page.getName();
            page.setPath(nowPath);
            List<WikiPageVo> wikiPageVos = listMap.get(page.getId());
            if (CollectionUtils.isNotEmpty(wikiPageVos)) {
                wikiPageVos = wikiPageVos.stream().sorted(Comparator.comparingInt(WikiPageVo::getSeqNo)).collect(Collectors.toList());
                page.setChildren(wikiPageVos);
                this.setChildren(listMap, wikiPageVos, nowPath);
            }
        }
    }
}

