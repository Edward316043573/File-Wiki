package top.cxscoder.boot.controller.wiki;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.office.documentserver.models.enums.Action;
import top.cxscoder.wiki.office.documentserver.models.enums.Type;
import top.cxscoder.wiki.office.documentserver.models.filemodel.FileModel;
import top.cxscoder.wiki.office.dto.PreviewOfficeFileDTO;
import top.cxscoder.wiki.office.services.configurers.FileConfigurer;
import top.cxscoder.wiki.office.services.configurers.wrappers.DefaultFileWrapper;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Tag(name = "office", description = "该接口为Onlyoffice文件操作接口，主要用来做一些文档的编辑，浏览等。")
@RestController
@Slf4j
@RequestMapping({"/office"})
public class OfficeController {

    @Value("${deployment.host}")
    private String deploymentHost;
    @Value("${server.port}")
    private String port;
//    @Value("${ufop.storage-type}")
//    private Integer storageType;

    @Value("${files.docservice.url.site}")
    private String docserviceSite;

    @Value("${files.docservice.url.api}")
    private String docserviceApiUrl;

    @Resource
    private LoginService loginService;

    @Resource
    private FileConfigurer<DefaultFileWrapper> fileConfigurer;

    @Resource
    private WikiPageService wikiPageService;

    @Resource
    private WikiPageFileService wikiPageFileService;

    @PostMapping("/previewofficefile")
    public JSONObject previewOfficeFile(HttpServletRequest request, @RequestBody PreviewOfficeFileDTO previewOfficeFileDTO) {
        try {
            String previewUrl = request.getScheme() + "://" + deploymentHost + ":"
                    + port + "/wiki/page/file/preview?"
                    + "userFileId=" + previewOfficeFileDTO.getUserFileId()
                    + "&isMin=false&shareBatchNum=undefined&extractionCode=undefined";
            User currentUser = loginService.getCurrentUser();
            WikiPageFile userFile = wikiPageFileService.lambdaQuery().eq(WikiPageFile::getPageId, previewOfficeFileDTO.getUserFileId()).one();
            Action action = Action.view;
            Type type = Type.desktop;
            Locale locale = new Locale("zh");
            top.cxscoder.wiki.office.entities.User user = new top.cxscoder.wiki.office.entities.User (currentUser);
            FileModel fileModel = fileConfigurer.getFileModel(
                    DefaultFileWrapper
                            .builder()
                            .userFile(userFile)
                            .type(type)
                            .lang(locale.toLanguageTag())
                            .action(action)
                            .user(user)
                            .actionData(previewUrl)
                            .build()
            );

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file",fileModel);
//            jsonObject.put("fileHistory", historyManager.getHistory(fileModel.getDocument()));  // get file history and add it to the model
            jsonObject.put("docserviceApiUrl", docserviceSite + docserviceApiUrl);
            jsonObject.put("reportName",userFile.getFileName());
            return jsonObject;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }



}