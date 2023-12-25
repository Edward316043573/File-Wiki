package top.cxscoder.wiki.service.manage.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.repository.mapper.WikiPageFileMapper;
import top.cxscoder.wiki.service.manage.WikiPageFileService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
}
