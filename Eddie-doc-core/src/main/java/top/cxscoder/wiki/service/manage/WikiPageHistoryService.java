package top.cxscoder.wiki.service.manage;


import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.wiki.repository.manage.entity.WikiPageHistory;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2020-09-05
 */
public interface WikiPageHistoryService extends IService<WikiPageHistory> {
	
	/**
	 * 保存或更新
	 */
	WikiPageHistory saveRecord(Long spaceId, Long pageId, String content);
}
