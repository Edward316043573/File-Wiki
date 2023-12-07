package top.cxscoder.wiki.repository.manage.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.cxscoder.wiki.repository.manage.entity.WikiPageTemplate;
import top.cxscoder.wiki.repository.manage.vo.WikiPageTemplateInfoVo;
import top.cxscoder.wiki.repository.manage.vo.WikiTemplateTagVo;

import java.util.List;

/**
 * <p>
 *  模板的mapper接口
 * </p>
 *
 * @author sh1yu
 * @since 2023-08-24
 */
public interface WikiPageTemplateMapper extends BaseMapper<WikiPageTemplate> {

    /**
     * <p>
     *      查询所有的模板
     * </p>
     */
    List<WikiPageTemplateInfoVo> getAllTemplate(@Param("user") Long user, @Param("name") String name, @Param("share") boolean share, @Param("tags") List tags, @Param("pageNum") Long pageNum);

    /*
    *  查询所有标签
    */
    List<WikiTemplateTagVo> getAllTags(@Param("user") Long user, @Param("open") boolean open);


    /*
     *  查询模板总数4分页
     */
    Long getAllTemplateCount(@Param("user") Long user, @Param("name") String name, @Param("share") boolean share, @Param("tags") List tags);

}
