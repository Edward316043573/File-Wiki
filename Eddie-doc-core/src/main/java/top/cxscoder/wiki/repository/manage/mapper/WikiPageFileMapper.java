package top.cxscoder.wiki.repository.manage.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.cxscoder.wiki.repository.manage.entity.WikiPageFile;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-03-06
 */
public interface WikiPageFileMapper extends BaseMapper<WikiPageFile> {
	
	@Update("update wiki_page_file set download_num=download_num+1 where id=#{id}")
	void addDownloadNum(@Param("id") Long id);
}
