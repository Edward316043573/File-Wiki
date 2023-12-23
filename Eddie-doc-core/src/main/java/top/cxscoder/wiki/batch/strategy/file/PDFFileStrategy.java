package top.cxscoder.wiki.batch.strategy.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.service.WikiPageUploadService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import java.io.File;
import java.io.IOException;

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
    @Override
    public void file(String uploadPath, WikiPageFile wikiPageFile, MultipartFile file) throws IOException {
        String fileName = StringUtils.defaultString(file.getOriginalFilename(), "新建文档");
        //pageId表示文件所在空间的文件夹的id 如果不是文件夹则pageId为0 查询不到对应的文件夹
        Long pageId = wikiPageFile.getPageId();
        //查询传入文件所在的父文件夹
        WikiPage page = wikiPageService.getById(pageId);
        //获取传入文件的所在空间id
        //新建wiki 存储到数据库
        WikiPage wikiPage = new WikiPage();
        wikiPage.setName(fileName.substring(0, fileName.indexOf(".")));
        //获取传入文件的空间id
        Long spaceId = wikiPageFile.getId();
        //传入文件的所在的文件夹id，如果在文件夹外为0，不为0查询父文件夹传入父文件夹的id
        Long id = wikiPageFile.getPageId();
        if (null != page) {
            spaceId = page.getSpaceId();
            id = page.getId();
        }
        WikiSpace space = wikiSpaceService.getById(spaceId);
        String spaceName = space.getName();
        String pageName = page.getName();
        String filePath = uploadPath +File.separator+ spaceName;
        Long parentId = page.getParentId();
        while(parentId != 0 ){
            WikiPage parentPage = wikiPageService.getById(parentId);
            String parentName = parentPage.getName();
            pageName = parentName + File.separator + pageName ;
            parentId = parentPage.getParentId();
        }
        //拼接空间名称
        filePath = filePath +File.separator+ pageName;
        File parentFile = new File(filePath);
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }
        filePath = filePath + File.separator + fileName;
        wikiPage.setSpaceId(spaceId);
        wikiPage.setParentId(id);
        wikiPage.setEditorType(2);
        File dest = new File(filePath);
        file.transferTo(dest);
        RandomAccessFile is = new RandomAccessFile(dest, "r");
        PDFParser parser = new PDFParser(is);
        parser.parse();
        PDDocument doc = parser.getPDDocument();
        PDFTextStripper textStripper = new PDFTextStripper();
        String context = textStripper.getText(doc);
        wikipageUploadService.update(wikiPage, context, context);
    }
}
