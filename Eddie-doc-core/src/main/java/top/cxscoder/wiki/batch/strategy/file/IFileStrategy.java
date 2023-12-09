package top.cxscoder.wiki.batch.strategy.file;


import org.springframework.web.multipart.MultipartFile;
import top.cxscoder.wiki.batch.strategy.base.IConditionalStrategy;
import top.cxscoder.wiki.domain.entity.WikiPageFile;

import java.io.IOException;

/**
 * 条件控制文件归档策略接口
 *
 * @author Sh1yu
 * @since 20230717
 */
public interface IFileStrategy extends IConditionalStrategy {
    void file(String uploadPath, WikiPageFile wikiPageFile, MultipartFile file)throws IOException;
}
