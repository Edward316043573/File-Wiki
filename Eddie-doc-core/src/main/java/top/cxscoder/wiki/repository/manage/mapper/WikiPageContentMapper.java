package top.cxscoder.wiki.repository.manage.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.cxscoder.wiki.repository.manage.entity.WikiPageContent;
import top.cxscoder.wiki.repository.manage.param.SearchByEsParam;
import top.cxscoder.wiki.repository.manage.vo.SpaceNewsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-02-24
 */
public interface WikiPageContentMapper extends BaseMapper<WikiPageContent> {
	
	List<SpaceNewsVo> getNewsList(SearchByEsParam param);
}
