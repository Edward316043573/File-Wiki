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
import top.cxscoder.wiki.service.WikiPageUploadService;
import top.cxscoder.wiki.service.manage.WikiPageService;

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
    @Override
    public void file(String uploadPath, WikiPageFile wikiPageFile, MultipartFile file) throws IOException {
        String fileName = StringUtils.defaultString(file.getOriginalFilename(), "新建文档");
        //pageId表示文件所在空间的文件夹的id 如果不是文件夹内泽pageId为0 查询不到对应的文件夹
        Long pageId = wikiPageFile.getPageId();
        //查询传入文件所在的父文件夹
        WikiPage page = wikiPageService.getById(pageId);
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
        wikiPage.setSpaceId(spaceId);
        wikiPage.setParentId(id);
        wikiPage.setEditorType(2);
        String filePath = uploadPath + File.separator + fileName;
        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        RandomAccessFile is = new RandomAccessFile(dest, "r");
        PDFParser parser = new PDFParser(is);
        // TODO 效率慢
        parser.parse();
        PDDocument doc = parser.getPDDocument();
        PDFTextStripper textStripper = new PDFTextStripper();
        // TODO 效率慢
        String context = textStripper.getText(doc);
        wikipageUploadService.update(wikiPage, context, context);
    }
}
