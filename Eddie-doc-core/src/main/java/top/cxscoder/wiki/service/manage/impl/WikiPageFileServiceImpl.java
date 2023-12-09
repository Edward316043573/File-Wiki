package top.cxscoder.wiki.service.manage.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.repository.mapper.WikiPageFileMapper;
import top.cxscoder.wiki.service.manage.WikiPageFileService;

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

}
