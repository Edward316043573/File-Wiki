package top.cxscoder.wiki.batch.strategy.file;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.service.WikiPageUploadService;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Stack;

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
    @Override
    public void file(String uploadPath, WikiPageFile wikiPageFile, MultipartFile file ) throws IOException {
        // 创建一个 WikiPage对象
        String originalFilename = file.getOriginalFilename();
        String fileName = StringUtils.defaultString(originalFilename.substring(0, originalFilename.indexOf(".")), "新建文档");
        WikiPage wikiPage = new WikiPage();
        wikiPage.setName(fileName);
        wikiPage.setParentId(wikiPageFile.getPageId());
        wikiPage.setSpaceId(wikiPageFile.getSpaceId());
        wikiPage.setEditorType(3);
        /* 解析PDF内容 */
        // 1. 找到文件路径 文件路径格式 上传路径/空间ID/页面层次结构
        WikiSpace wikiSpace = wikiSpaceService.getById(wikiPage.getSpaceId());
        StringBuffer filePathBuffer = new StringBuffer();
        filePathBuffer.append(uploadPath).append(File.separator).append(wikiSpace.getName()).append(File.separator);
        Long parentId = wikiPageFile.getPageId();
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
        String filePath = filePathBuffer.append(file.getOriginalFilename()).toString();
        // 2.调用wikipageUploadService.update(wikiPage, context, context);
        File dest = new File(filePath);
        // 如果文件不存在则创建父目录
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        RandomAccessFile is = new RandomAccessFile(dest, "r");
        PDFParser parser = new PDFParser(is);
        parser.parse(); // TODO 效率低
        PDDocument doc = parser.getPDDocument();
        PDFTextStripper textStripper = new PDFTextStripper();
        String context = textStripper.getText(doc); // TODO 效率低
        wikipageUploadService.update(wikiPage, context, context);
        // 存WikiPageFile
        String UUID = IdUtil.fastUUID();
        WikiPageFile uploadFile = new WikiPageFile();
        uploadFile.setFileName(fileName);
        uploadFile.setPageId(wikiPage.getId());
        uploadFile.setSpaceId(wikiPageFile.getId());
        uploadFile.setFileUrl(filePath);
        uploadFile.setUuid(UUID);
        User currentUser = loginService.getCurrentUser();
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

    }
}
