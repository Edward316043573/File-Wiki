package top.cxscoder.wiki.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.wiki.json.DocResponseJson;
import top.cxscoder.wiki.repository.manage.entity.UserMessage;
import top.cxscoder.wiki.repository.manage.entity.WikiPage;
import top.cxscoder.wiki.repository.manage.entity.WikiPageFile;
import top.cxscoder.wiki.repository.manage.entity.WikiSpace;
import top.cxscoder.wiki.repository.support.consts.DocSysType;
import top.cxscoder.wiki.repository.support.consts.UserMsgType;
import top.cxscoder.wiki.service.common.WikiPageAuthService;
import top.cxscoder.wiki.service.manage.UserMessageService;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 文档控制器
 *
 * @author 暮光：城中城
 * @author sh1yu
 * @since 2019年2月17日
 */
@Service
@RequiredArgsConstructor
public class WikiPageFileServiceEx {
    @Value("${zyplayer.doc.wiki.upload-path:}")
    private String uploadPath;

    private final WikiPageFileService wikiPageFileService;
    private final WikiSpaceService wikiSpaceService;
    private final WikiPageService wikiPageService;
    private final WikiPageAuthService wikiPageAuthService;
    private final UserMessageService userMessageService;

    public List<WikiPageFile> list(WikiPageFile wikiPageFile) {
        // TODO 检查space是否开放访问
        UpdateWrapper<WikiPageFile> wrapper = new UpdateWrapper<>();
        wrapper.eq("del_flag", 0);
        wrapper.eq("page_id", wikiPageFile.getPageId());
        List<WikiPageFile> fileList = wikiPageFileService.list(wrapper);
        for (WikiPageFile pageFile : fileList) {
            pageFile.setFileUrl("zyplayer-doc-wiki/common/file?uuid=" + pageFile.getUuid());
        }
        return fileList;
    }

    public String delete(WikiPageFile wikiPageFile) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
User currentUser = loginUser.getUser();
        Long id = wikiPageFile.getId();
        if (id == null || id <= 0) {
            return "需指定删除的附件！";
        }
        WikiPageFile pageFileSel = wikiPageFileService.getById(wikiPageFile.getId());
        WikiPage wikiPageSel = wikiPageService.getById(pageFileSel.getPageId());
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 权限判断
        String canDeleteFile = wikiPageAuthService.canDeleteFile(wikiSpaceSel, pageFileSel.getPageId(), currentUser.getUserId());
        if (canDeleteFile != null) {
            return canDeleteFile;
        }
        wikiPageFile.setDelFlag(1);
        wikiPageFile.setUpdateUserId(currentUser.getUserId());
        wikiPageFile.setUpdateUserName(currentUser.getUserName());
        wikiPageFile.setUpdateTime(new Date());
        wikiPageFileService.updateById(wikiPageFile);
        // 给相关人发送消息
        UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPageSel.getId(), wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_FILE_DEL);
        userMessage.setAffectUserId(wikiPageSel.getCreateUserId());
        userMessage.setAffectUserName(wikiPageSel.getCreateUserName());
        userMessageService.addWikiMessage(userMessage);
        return null;
    }
    
    public DocResponseJson<Object> basicUpload(WikiPageFile wikiPageFile, MultipartFile file) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
User currentUser = loginUser.getUser();
        Long pageId = wikiPageFile.getPageId();
        if (pageId == null || pageId <= 0) {
            return DocResponseJson.warn("未指定附件关联的文档");
        }
        WikiPage wikiPageSel = wikiPageService.getById(pageId);
        WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
        // 权限判断
        String canUploadFile = wikiPageAuthService.canUploadFile(wikiSpaceSel, wikiPageSel.getId(), currentUser.getUserId());
        if (canUploadFile != null) {
            return DocResponseJson.warn(canUploadFile);
        }
        String info = this.uploadFile(wikiPageFile, file, 0);
        if (null != info) {
            return DocResponseJson.warn(info);
        }
        // 给相关人发送消息
        UserMessage userMessage = userMessageService.createUserMessage(currentUser, pageId, wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_UPLOAD);
        userMessage.setAffectUserId(wikiPageSel.getCreateUserId());
        userMessage.setAffectUserName(wikiPageSel.getCreateUserName());
        userMessageService.addWikiMessage(userMessage);
        return DocResponseJson.ok(wikiPageFile);
    }

    /**
     * 单纯的文件上传方法
     *
     * @param wikiPageFile
     * @param file
     * @return
     */
    public String uploadFile(WikiPageFile wikiPageFile, MultipartFile file, Integer flag) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        String fileName = file.getOriginalFilename();
        String fileSuffix = "";
        if (fileName != null && fileName.lastIndexOf(".") >= 0) {
            fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        }
        String path = uploadPath + "/" + DateTime.now().toString("yyyy/MM/dd") + "/";
        File newFile = new File(path);
        if (!newFile.exists() && !newFile.mkdirs()) {
            return "创建文件夹失败";
        }
        String simpleUUID = IdUtil.simpleUUID();
        path += simpleUUID + fileSuffix;
        newFile = new File(path);
        try {
            file.transferTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
            return "保存文件失败";
        }
        wikiPageFile.setFileSize(file.getSize());
        wikiPageFile.setUuid(simpleUUID);
        wikiPageFile.setFileUrl(path);
        wikiPageFile.setFileName(fileName);
        wikiPageFile.setCreateTime(new Date());
        wikiPageFile.setCreateUserId(currentUser.getUserId());
        wikiPageFile.setCreateUserName(currentUser.getUserName());
        wikiPageFile.setDelFlag(flag);
        wikiPageFileService.save(wikiPageFile);
        wikiPageFile.setFileUrl("zyplayer-doc-wiki/common/file?uuid=" + wikiPageFile.getUuid());
        return null;
    }
}
