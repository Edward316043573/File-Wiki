package top.cxscoder.wiki.service.manage.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-03-06
 */
@Service
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
        IOUtils.copy(new FileInputStream(filePath), httpServletResponse.getOutputStream());
    }

    @Override
    public void previewHistoryFile(HttpServletResponse httpServletResponse, String url) throws IOException {
        IOUtils.copy(new FileInputStream(url), httpServletResponse.getOutputStream());
    }

    @Override
    public String export(Long spaceId) {
        LambdaQueryWrapper<WikiSpace> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WikiSpace::getId,spaceId);
        WikiSpace space = wikiSpaceService.getOne(queryWrapper);
        String spaceName = space.getName();
        String filePath = uploadPath + File.separator + spaceName;
        String outputFileName = outputFolderPath + File.separator + spaceName + ".pdf";// 输出文件路径
        try {
            //目标文档
            PDDocument targetDocument = new PDDocument();
            //文档大纲
            PDDocumentOutline documentOutline = new PDDocumentOutline();
            //根目录项
            PDOutlineItem rootOutlineItem = new PDOutlineItem();
            //设置标题为目录
            rootOutlineItem.setTitle("目录");
            //将一个根目录项添加到文档大纲中
            documentOutline.addLast(rootOutlineItem);
            //合并
            mergePDFs(filePath, targetDocument, rootOutlineItem);
            //将文档大纲设置到目标文档中
            targetDocument.getDocumentCatalog().setDocumentOutline(documentOutline);
            //保存文件
            File exportFile = new File(outputFolderPath);
            if (!exportFile.exists()){
                exportFile.mkdirs();
            }
            targetDocument.save(outputFileName);
            targetDocument.close();

            System.out.println("PDF 合并和目录索引生成完成。");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFileName;
    }

    private static void mergePDFs(String folderPath, PDDocument targetDocument, PDOutlineItem parentOutlineItem)
            throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            List<File> fileList = Arrays.asList(files);
            Collections.sort(fileList, Comparator.comparingLong(File::lastModified));
            for (File file : fileList) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    PDDocument sourceDocument = PDDocument.load(file);
                    for (int i = 0; i < sourceDocument.getNumberOfPages(); i++) {
                        targetDocument.addPage(sourceDocument.getPage(i));
                    }

                    // 创建目录项（书签）
                    PDPageXYZDestination dest = new PDPageXYZDestination();
                    dest.setPage(targetDocument.getPage(targetDocument.getNumberOfPages() - sourceDocument.getNumberOfPages()));
                    PDRectangle cropBox = targetDocument.getPage(targetDocument.getNumberOfPages() - sourceDocument.getNumberOfPages()).getCropBox();
                    int left = 0; // 设置左边位置
                    int top = (int) cropBox.getHeight(); // 设置顶部位置
                    float zoom = 1; // 设置缩放比例
                    dest.setLeft(left);
                    dest.setTop(top);
                    dest.setZoom(zoom);
                    PDOutlineItem chapterItem = new PDOutlineItem();
                    chapterItem.setDestination(dest);
                    chapterItem.setTitle(file.getName());
                    // 将目录项添加到父级目录项中
                    parentOutlineItem.addLast(chapterItem);
                } else if (file.isDirectory()) {
                    // 创建父级目录项（章节）
                    PDOutlineItem parentItem = new PDOutlineItem();
                    parentItem.setTitle(file.getName());

                    // 递归处理子文件夹，并将父级目录项作为参数传递
                    mergePDFs(file.getAbsolutePath(), targetDocument, parentItem);

                    // 检查子级目录项是否为空
                    if (parentItem.getFirstChild() != null) {
                        // 添加父级目录项到整个文档目录
                        parentOutlineItem.addLast(parentItem);
                    }
                }
            }
        }
    }
}
