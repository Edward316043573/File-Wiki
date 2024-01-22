package top.cxscoder.wiki.service.manage.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.repository.mapper.WikiPageFileMapper;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-03-06
 */
@Service
@Slf4j
public class WikiPageFileServiceImpl extends ServiceImpl<WikiPageFileMapper, WikiPageFile> implements WikiPageFileService {

    @Value("${wiki.upload-path}")
    private String uploadPath;
    @Value("${wiki.export-path:}")

    private String outputFolderPath;
    @Resource
    private WikiSpaceService wikiSpaceService;
    @Resource
    private WikiPageFileService wikiPageFileService;

    @Override
    public void previewFile(HttpServletResponse httpServletResponse, Long userFileId) throws IOException {
        // 找到目录
        WikiPageFile file = wikiPageFileService.lambdaQuery().eq(WikiPageFile::getPageId, userFileId).one();
        String filePath = uploadPath + File.separator + file.getFileUrl();
        // 下载 IOUtils.copy 把一个输入流写出到指定的输出流
        try (FileInputStream fis = new FileInputStream(filePath)) {
            IOUtils.copy(fis, httpServletResponse.getOutputStream());
        } catch (IOException e) {
            // 处理异常
            throw new ServiceException("下载文件失败" + e.getMessage());
        }
    }

    @Override
    public void previewHistoryFile(HttpServletResponse httpServletResponse, String url) throws IOException {
        try (FileInputStream fis = new FileInputStream(url)) {
            IOUtils.copy(fis, httpServletResponse.getOutputStream());
        } catch (IOException e) {
            // 处理异常
            throw new ServiceException("下载文件失败" + e.getMessage());
        }
    }

    @Override
    public String export(Long spaceId) throws IOException {
        LambdaQueryWrapper<WikiSpace> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WikiSpace::getId, spaceId);
        WikiSpace space = wikiSpaceService.getOne(queryWrapper);
        String spaceName = space.getName();
        String filePath = uploadPath + File.separator + spaceName;
        String outputFileName = outputFolderPath + File.separator + spaceName + ".pdf";// 输出文件路径
        PDDocument targetDocument = null;
        try {
            //目标文档
            targetDocument = new PDDocument();
            PDFMergerUtility merger = new PDFMergerUtility();
            //文档大纲
            PDDocumentOutline documentOutline = new PDDocumentOutline();
            //根目录项
            PDOutlineItem rootOutlineItem = new PDOutlineItem();
            //设置标题为目录
            rootOutlineItem.setTitle("目录");
            //将一个根目录项添加到文档大纲中
            documentOutline.addLast(rootOutlineItem);
            //合并
            mergePDFs(filePath, targetDocument, rootOutlineItem, merger);
            //将文档大纲设置到目标文档中
            targetDocument.getDocumentCatalog().setDocumentOutline(documentOutline);
            //保存文件
            File exportFile = new File(outputFolderPath);
            if (!exportFile.exists()) {
                exportFile.mkdirs();
            }
            targetDocument.save(outputFileName);
            log.info("合并完成");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (targetDocument != null){
                targetDocument.close();
            }
        }
        return outputFileName;
    }

    private static void mergePDFs(String folderPath, PDDocument targetDocument, PDOutlineItem parentOutlineItem, PDFMergerUtility merger) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    PDDocument sourceDocument = Loader.loadPDF(file);
                    merger.appendDocument(targetDocument,sourceDocument);
                    PDOutlineItem chapterItem = new PDOutlineItem();
                    chapterItem.setDestination(targetDocument.getPage(targetDocument.getNumberOfPages() - sourceDocument.getNumberOfPages()));
                    chapterItem.setTitle(file.getName());
                    parentOutlineItem.addLast(chapterItem);
                } else if (file.isDirectory()) {
                    PDOutlineItem parentItem = new PDOutlineItem();
                    parentItem.setTitle(file.getName());
                    mergePDFs(file.getAbsolutePath(), targetDocument, parentItem, merger);
                    if (parentItem.getFirstChild() != null) {
                        parentOutlineItem.addLast(parentItem);
                    }
                }
            }
        }
    }
}
