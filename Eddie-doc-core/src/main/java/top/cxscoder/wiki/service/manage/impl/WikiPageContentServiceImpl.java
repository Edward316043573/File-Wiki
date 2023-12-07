package top.cxscoder.wiki.service.manage.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.repository.manage.entity.WikiPageContent;
import top.cxscoder.wiki.repository.manage.mapper.WikiPageContentMapper;
import top.cxscoder.wiki.service.manage.WikiPageContentService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-02-24
 */
@Service
public class WikiPageContentServiceImpl extends ServiceImpl<WikiPageContentMapper, WikiPageContent> implements WikiPageContentService {

}
