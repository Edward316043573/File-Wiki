package top.cxscoder.boot.controller.wiki;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.wiki.anotation.AuthMan;
import top.cxscoder.wiki.batch.BatchDocImportManager;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.enums.PageFileSource;
import top.cxscoder.wiki.service.WikiPageFileServiceEx;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文档控制器
 *
 * @author 暮光：城中城
 * @author Sh1yu
 * @since 2019年2月17日
 */
@Slf4j
@AuthMan
@RestController
@RequestMapping("/wiki/page/file")
@RequiredArgsConstructor
public class WikiPageFileController {
	
	private final WikiPageFileServiceEx wikiPageFileServiceEx;
	private final BatchDocImportManager batchDocImportManger;
	private final WikiPageFileService wikiPageFileService;

	@Value("${wiki.upload-path:}")
	private String uploadPath;
	@Value("${wiki.export-path:}")

	private String outputFolderPath;

	private final WikiSpaceService wikiSpaceService;
	private final WikiPageService wikiPageService;
	@PostMapping("/delete")
	public void delete(@RequestBody WikiPageFile wikiPageFile) {
		String info = wikiPageFileServiceEx.delete(wikiPageFile);
		if (null != info) {
//			return DocResponseJson.warn(info);
			throw new ServiceException(info);
		}
	}
	
	@PostMapping("/wangEditor/upload")
	public Map<String, Object> wangEditorUpload(@RequestBody WikiPageFile wikiPageFile, @RequestParam("files") MultipartFile file) {
		Map<String, Object> resultMap = new HashMap<>();
		wikiPageFile.setFileSource(PageFileSource.PASTE_FILES.getSource());
//		DocResponseJson<Object> DocResponseJson = wikiPageFileServiceEx.basicUpload(wikiPageFile, file);
		wikiPageFileServiceEx.basicUpload(wikiPageFile, file);
//		if (!DocResponseJson.isOk()) {
//			resultMap.put("errno", 1);
//			resultMap.put("message", DocResponseJson.getErrMsg());
//		} else {
//			resultMap.put("errno", 0);
//			resultMap.put("data", new JSONObject().fluentPut("url", wikiPageFile.getFileUrl()));
//		}
		return resultMap;
	}
	
	@PostMapping("/import/upload")
	public void importUpload(WikiPageFile wikiPageFile, @RequestParam("files") MultipartFile file) {
		batchDocImportManger.importBatchDoc(wikiPageFile, file);
	}
	
	@PostMapping("/upload")
	public WikiPageFile upload(@RequestBody WikiPageFile wikiPageFile, @RequestParam("files") MultipartFile file) {
		wikiPageFile.setFileSource(PageFileSource.UPLOAD_FILES.getSource());
		return wikiPageFileServiceEx.basicUpload(wikiPageFile, file);
	}

	@GetMapping("/preview")
	public void preview(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Long userFileId) throws IOException {
		WikiPageFile userFile = wikiPageFileService.lambdaQuery().eq(WikiPageFile::getPageId, userFileId).one();
		String extendName = FileUtil.extName(userFile.getFileUrl());
		String fileName = userFile.getFileName() + "." + extendName;
		try {
			fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

//		httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名
		httpServletResponse.addHeader("Content-Disposition", "attachment;filename=" + fileName);// 设置文件名
		String mimeType = HttpUtil.getMimeType(fileName);
		httpServletResponse.setHeader("Content-Type", mimeType);
		// TODO 媒体文件可以分块查看

		// 调用文件下载方法
		wikiPageFileService.previewFile(httpServletResponse,userFileId);
	}

	@GetMapping("/download")
	public Map<String, String> download(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Long spaceId) throws IOException {
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
		httpServletResponse.addHeader("Content-Disposition", "attachment;filename=" + outputFileName);// 设置文件名
		String mimeType = HttpUtil.getMimeType(outputFileName);
		httpServletResponse.setHeader("Content-Type", mimeType);
		// TODO 媒体文件可以分块查看
		// 下载
		IOUtils.copy(new FileInputStream(outputFileName), httpServletResponse.getOutputStream());
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("path",outputFileName);
        return resultMap;
	}

	private static void mergePDFs(String folderPath, PDDocument targetDocument, PDOutlineItem parentOutlineItem)
			throws IOException {
		File folder = new File(folderPath);
		File[] files = folder.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".pdf")) {
					PDDocument sourceDocument = PDDocument.load(file);
					for (int i = 0; i < sourceDocument.getNumberOfPages(); i++) {
						targetDocument.addPage(sourceDocument.getPage(i));
					}

					// 创建目录项（书签）
					PDPageXYZDestination dest = new PDPageXYZDestination();
					dest.setPage(targetDocument.getPage(targetDocument.getNumberOfPages() - 1));
					PDRectangle cropBox = targetDocument.getPage(targetDocument.getNumberOfPages() - 1).getCropBox();
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

