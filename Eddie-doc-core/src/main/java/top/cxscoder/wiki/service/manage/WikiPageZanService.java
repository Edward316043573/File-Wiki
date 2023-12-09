package top.cxscoder.wiki.service.manage;


import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.wiki.domain.entity.WikiPageZan;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-03-05
 */
public interface WikiPageZanService extends IService<WikiPageZan> {
	void zanPage(WikiPageZan wikiPageZan);
}
