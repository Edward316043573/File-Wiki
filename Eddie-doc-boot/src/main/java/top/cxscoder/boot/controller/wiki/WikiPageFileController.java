package top.cxscoder.boot.controller.wiki;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.wiki.anotation.AuthMan;
import top.cxscoder.wiki.batch.BatchDocImportManager;
import top.cxscoder.wiki.enums.PageFileSource;
import top.cxscoder.wiki.json.DocResponseJson;
import top.cxscoder.wiki.json.ResponseJson;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.service.WikiPageFileServiceEx;

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
	
	@PostMapping("/delete")
	public void delete(@RequestBody WikiPageFile wikiPageFile) {
		String info = wikiPageFileServiceEx.delete(wikiPageFile);
		//todo 处理删除失败
//		if (null != info) {
//			return DocResponseJson.warn(info);
//		}
//		return DocResponseJson.ok();
	}
	
	@PostMapping("/wangEditor/upload")
	public Map<String, Object> wangEditorUpload(WikiPageFile wikiPageFile, @RequestParam("files") MultipartFile file) {
		Map<String, Object> resultMap = new HashMap<>();
		wikiPageFile.setFileSource(PageFileSource.PASTE_FILES.getSource());
		DocResponseJson<Object> DocResponseJson = wikiPageFileServiceEx.basicUpload(wikiPageFile, file);
		if (!DocResponseJson.isOk()) {
			resultMap.put("errno", 1);
			resultMap.put("message", DocResponseJson.getErrMsg());
		} else {
			resultMap.put("errno", 0);
			resultMap.put("data", new JSONObject().fluentPut("url", wikiPageFile.getFileUrl()));
		}
		return resultMap;
	}
	
	@PostMapping("/import/upload")
	public ResponseJson importUpload(WikiPageFile wikiPageFile, @RequestParam("files") MultipartFile file) {
		return batchDocImportManger.importBatchDoc(wikiPageFile, file);
	}
	
	@PostMapping("/upload")
	public ResponseJson upload(WikiPageFile wikiPageFile, @RequestParam("files") MultipartFile file) {
		wikiPageFile.setFileSource(PageFileSource.UPLOAD_FILES.getSource());
		return wikiPageFileServiceEx.basicUpload(wikiPageFile, file);
	}
}

