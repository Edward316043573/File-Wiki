package top.cxscoder.wiki.service.manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.domain.entity.WikiPageTemplate;
import top.cxscoder.wiki.repository.mapper.WikiPageTemplateMapper;
import top.cxscoder.wiki.domain.vo.WikiPageTemplateInfoVo;
import top.cxscoder.wiki.domain.vo.WikiTemplateTagVo;
import top.cxscoder.wiki.service.manage.WikiPageTemplateService;

import java.util.List;
/**
 * <p>
 *     模板服务实现类
 * </p>
 *
 * @author Sh1yu
 * @since 2023-08-24
 */
@Service
public class WikiPageTemplateServiceImpl extends ServiceImpl<WikiPageTemplateMapper, WikiPageTemplate> implements WikiPageTemplateService {
    @Override
    public List<WikiTemplateTagVo> getAllTags(Long user, boolean open) {
        return getBaseMapper().getAllTags(user,open);
    }

    @Override
    public WikiPageTemplate getWikiPageTemplateBySpaceAndPage(Long spaceId, Long pageId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("space_id", spaceId);
        queryWrapper.eq("page_id", pageId);
        return getBaseMapper().selectOne(queryWrapper);
    }

    @Override
    public List<WikiPageTemplateInfoVo> filterAll(Long user, String name, boolean open, List<String> tags, Long pageNum) {
        long offset = 0L;
        if (null != pageNum && pageNum != 0L) {
            offset = (pageNum - 1) * 8;
        }
        return getBaseMapper().getAllTemplate(user, StringUtils.isBlank(name) ? null : "%" + name + "%", open, tags, offset);
    }

    @Override
    public Long total(Long user, String name, boolean open, List<String> tags) {
        return getBaseMapper().getAllTemplateCount(user, name, open, tags);
    }
}
