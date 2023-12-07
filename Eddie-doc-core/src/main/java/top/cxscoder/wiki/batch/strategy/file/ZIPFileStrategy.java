package top.cxscoder.wiki.batch.strategy.file;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.wiki.batch.BatchDocImportManager;
import top.cxscoder.wiki.batch.entry.DocEntry;
import top.cxscoder.wiki.batch.entry.MediaEntry;
import top.cxscoder.wiki.exception.ConfirmException;
import top.cxscoder.wiki.repository.manage.entity.WikiPage;
import top.cxscoder.wiki.repository.manage.entity.WikiPageFile;
import top.cxscoder.wiki.service.WikiPageUploadService;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;


/**
 * ZIP格式文件上传策略
 *
 * @author Sh1yu
 * @since 2023年7月13日
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ZIPFileStrategy implements IFileStrategy {
    private final BatchDocImportManager batchDocImportManger;
    private final WikiPageFileService wikiPageFileService;
    private final WikiPageUploadService wikiPageUploadService;
    private final WikiPageService wikiPageService;

    @Override
    public void file(String uploadPath, WikiPageFile wikiPageFile, MultipartFile file) throws IOException {
        Long pageID = wikiPageFile.getPageId();
        WikiPage page = wikiPageService.getById(pageID);
        String path = uploadPath + "/" + DateTime.now().toString("yyyy/MM/dd") + "/";
        File dir = new File(path);
        dir.mkdirs();
        File localReplica = new File(path + file.hashCode() + file.getOriginalFilename());
        localReplica.createNewFile();
        file.transferTo(localReplica);
        File unzip = ZipUtil.unzip(localReplica, CharsetUtil.CHARSET_GBK);
        File[] files = unzip.listFiles();
        ArrayList<DocEntry> docEntries = batchDocImportManger.combDependency(files);
        for (DocEntry docEntry : docEntries) {
            WikiPage wikiPage = new WikiPage();
            wikiPage.setName(docEntry.getName());
            Long spaceId = wikiPageFile.getId();
            Long id = wikiPageFile.getPageId();
            if (null != page) {
                spaceId = page.getSpaceId();
                id = page.getId();
            }
            wikiPage.setSpaceId(spaceId);
            wikiPage.setParentId(id);
            wikiPage.setEditorType(2);
            String context = docEntry.getContext();
            wikiPageUploadService.update(wikiPage, context, context);
            LinkedList<MediaEntry> medias = docEntry.getMedias();
            for (MediaEntry media : medias) {
                File mediaFile = new File(media.getOldFileLink());
                WikiPageFile mediaWikiPageFile = new WikiPageFile();
                mediaWikiPageFile.setPageId(wikiPage.getId());
                LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
User currentUser = loginUser.getUser();

                String savePath = uploadPath + "/" + DateTime.now().toString("yyyy/MM/dd") + "/";
                File newFile = new File(savePath);
                if (!newFile.exists() && !newFile.mkdirs()) {
                    log.warn("创建文件夹失败{}", savePath);
                    throw new ConfirmException("创建文件夹失败");
                }
                String simpleUUID = IdUtil.simpleUUID();
                savePath += simpleUUID + "." + FileUtil.getSuffix(media.getOldFileLink());
                newFile = new File(savePath);
                try {
                    if (!mediaFile.exists()) {
                        continue;
                    }
                    FileUtil.copy(mediaFile, newFile, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("保存文件失败{}", savePath);
                    throw new ConfirmException("保存文件失败");
                }
                mediaWikiPageFile.setFileSize(FileUtil.size(mediaFile));
                mediaWikiPageFile.setUuid(simpleUUID);
                mediaWikiPageFile.setFileUrl(savePath);
                mediaWikiPageFile.setFileName(media.getOldFileName());
                mediaWikiPageFile.setCreateTime(new Date());
                mediaWikiPageFile.setCreateUserId(currentUser.getUserId());
                mediaWikiPageFile.setCreateUserName(currentUser.getUserName());
                mediaWikiPageFile.setDelFlag(0);
                wikiPageFileService.save(mediaWikiPageFile);
                mediaWikiPageFile.setFileUrl("zyplayer-doc-wiki/common/file?uuid=" + mediaWikiPageFile.getUuid());
                context = context.replace(media.getOldFileLinkName(), mediaWikiPageFile.getFileUrl());
            }
            wikiPageUploadService.update(wikiPage, context, context);
        }
    }

    @Override
    public String getCondition() {
        return "zip";
    }
}
