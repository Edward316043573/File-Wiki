package top.cxscoder.boot.controller.wiki;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.wiki.anotation.AuthMan;
import top.cxscoder.wiki.batch.BatchDocImportManager;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.enums.PageFileSource;
import top.cxscoder.wiki.service.WikiPageFileServiceEx;
import top.cxscoder.wiki.service.manage.WikiPageFileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

}

