package top.cxscoder.wiki.service.manage;


import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.wiki.domain.entity.WikiPageFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-03-06
 */
public interface WikiPageFileService extends IService<WikiPageFile> {

    void previewFile(HttpServletResponse httpServletResponse, Long userFileId) throws IOException;

    void previewHistoryFile(HttpServletResponse httpServletResponse, String url) throws IOException;
    String export(Long spaceId);
}
