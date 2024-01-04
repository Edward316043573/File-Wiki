package top.cxscoder.wiki.batch.strategy.file;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.service.WikiPageUploadService;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageHistoryService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author: Wang Jianping
 * @date: 2023/12/15 10:13
 */


@Component
@RequiredArgsConstructor
@Slf4j
public class PDFFileStrategy implements IFileStrategy {
    @Override
    public String getCondition() {
        return "pdf";
    }

    private final WikiPageService wikiPageService;
    private final WikiPageUploadService wikipageUploadService;

    private final WikiSpaceService wikiSpaceService;

    private final WikiPageFileService wikiPageFileService;
    private final LoginService loginService;

    private final WikiPageHistoryService wikiPageHistoryService;
    @Value("${wiki.history-path:}")
    public String historyPath;

    @Override
    public void file(String uploadPath, WikiPageFile wikiPageFile, MultipartFile file) throws IOException {
        // 创建一个 WikiPage对象
        User currentUser = loginService.getCurrentUser();
        String originalFilename = file.getOriginalFilename();
        String fileName = StringUtils.defaultString(originalFilename.substring(0, originalFilename.indexOf(".")), "新建文档");
        Long pageId = wikiPageFile.getPageId();
        LambdaQueryWrapper<WikiPage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WikiPage::getParentId, pageId);
        List<WikiPage> wikiPages = wikiPageService.list(wrapper);
        List<WikiPage> wikiPageList = wikiPages.stream().filter(w -> w.getName().equals(fileName)).collect(Collectors.toList());
        if (wikiPageList.size() > 0) {
            WikiPage wikiPage = wikiPageList.get(0);
            LambdaQueryWrapper<WikiPageFile> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WikiPageFile::getPageId, wikiPage.getId());
            WikiPageFile pageFile = wikiPageFileService.getOne(queryWrapper);
            //上传了一个同名文件，
            //1，更新wiki_page表的更新时间以及使用者
            wikiPage.setUpdateTime(new Date());
            wikiPage.setUpdateUserName(currentUser.getUserName());
            wikiPageService.updateById(wikiPage);
            //操作后台文件
            WikiSpace wikiSpace = wikiSpaceService.getById(wikiPage.getSpaceId());
            StringBuffer filePathBuffer = new StringBuffer();
            filePathBuffer.append(wikiSpace.getName()).append(File.separator);
            Long parentId = pageId;
            Stack<String> s = new Stack<>();
            while (parentId != 0) {
                WikiPage parentPage = wikiPageService.getById(parentId);
                String parentName = parentPage.getName();
                s.push(parentName);
                parentId = parentPage.getParentId();
            }
            while (!s.isEmpty()) {
                filePathBuffer.append(s.pop()).append(File.separator);
            }
            String filePath = filePathBuffer + file.getOriginalFilename();
            String historyFilePath = filePathBuffer + pageFile.getUuid() + ".pdf";
            File originFile = new File(uploadPath + File.separator + filePath);
            File historyFile = new File(historyPath + File.separator + historyFilePath);
            // 如果文件不存在则创建父目录
            if (!historyFile.getParentFile().exists()) {
                historyFile.getParentFile().mkdirs();
            }
            // 转存文件
            try {
                Files.move(originFile.toPath(), historyFile.toPath());
                // 关闭文件流
                file.getInputStream().close();
                // 重新打开输入流，读取新上传的文件
                try ( InputStream newInputStream = file.getInputStream()){
                    Files.copy(newInputStream, originFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
           catch (IOException e){
                throw new ServiceException("文件上传异常");
            }
            // 上传文件将原文件覆盖
            // 关闭新输入流
//            //转存文件
//            Files.move(originFile.toPath(), historyFile.toPath());
//            //上传文件将原文件覆盖
//            file.transferTo(originFile);
            //2.更新wiki_page_file表,同样修改user和time
            pageFile.setUpdateTime(new Date());
            pageFile.setUpdateUserName(currentUser.getUserName());
            String simpleUUID = IdUtil.simpleUUID();
            pageFile.setUuid(simpleUUID);
            wikiPageFileService.updateById(pageFile);
            //3.更新history表
            String encodedPath = Base64.getEncoder().encodeToString(historyFilePath.getBytes());
            wikiPageHistoryService.saveRecord(wikiSpace.getId(), pageFile.getPageId(), encodedPath);
        } else {
            //正常上传文件
            WikiPage wikiPage = new WikiPage();
            wikiPage.setName(fileName);
            wikiPage.setParentId(pageId);
            wikiPage.setSpaceId(wikiPageFile.getSpaceId());
            wikiPage.setEditorType(3);
            /* 解析PDF内容 */
            // 1. 找到文件路径 文件路径格式 上传路径/空间ID/页面层次结构
            WikiSpace wikiSpace = wikiSpaceService.getById(wikiPage.getSpaceId());
            StringBuffer filePathBuffer = new StringBuffer();
            filePathBuffer.append(wikiSpace.getName()).append(File.separator);
            Long parentId = pageId;
            Stack<String> s = new Stack<>();
            while (parentId != 0) {
                WikiPage parentPage = wikiPageService.getById(parentId);
                String parentName = parentPage.getName();
                s.push(parentName);
                parentId = parentPage.getParentId();
            }
            while (!s.isEmpty()) {
                filePathBuffer.append(s.pop()).append(File.separator);
            }
            String filePath = filePathBuffer + file.getOriginalFilename();

            // 2.调用wikipageUploadService.update(wikiPage, context, context);

            File dest = new File(uploadPath + File.separator + filePath);
            // 如果文件不存在则创建父目录
            if (!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs();
            }
            // 如果文件不存在则创建父目录
            try(InputStream newInputStream = file.getInputStream()){
                Files.copy(newInputStream, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                throw new ServiceException("上传文件异常");
            }
            // 上传文件将原文件覆盖

            // 关闭新输入流
//            newInputStream.close();
//            file.transferTo(dest);

//            RandomAccessFile is = new RandomAccessFile(dest, "r");
//            PDFParser parser = new PDFParser(is);
//            parser.parse(); // TODO 效率低
//            PDDocument doc = parser.getPDDocument();
//            PDFTextStripper textStripper = new PDFTextStripper();
//            String context = textStripper.getText(doc); // TODO 效率低
            wikipageUploadService.update(wikiPage, "", "pdf文件");
            // 存WikiPageFile
            String UUID = IdUtil.fastUUID();
            WikiPageFile uploadFile = new WikiPageFile();
            uploadFile.setFileName(fileName);
            uploadFile.setPageId(wikiPage.getId());
            uploadFile.setSpaceId(wikiPageFile.getId());
            uploadFile.setFileUrl(filePath);
            uploadFile.setUuid(UUID);
            uploadFile.setCreateUserId(currentUser.getUserId());
            uploadFile.setCreateUserName(currentUser.getUserName());
            uploadFile.setUpdateUserId(currentUser.getUserId());
            uploadFile.setUpdateUserName(currentUser.getUserName());
            uploadFile.setCreateTime(new Date());
            uploadFile.setUpdateTime(new Date());
            uploadFile.setDelFlag(0);
            uploadFile.setFileSize(file.getSize());
            uploadFile.setDownloadNum(0);
            wikiPageFileService.save(uploadFile);
            //修改history 表
        }
    }
}
