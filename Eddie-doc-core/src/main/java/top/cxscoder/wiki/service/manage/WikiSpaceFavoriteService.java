package top.cxscoder.wiki.service.manage;

import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.wiki.domain.entity.WikiSpaceFavorite;

import java.util.List;

/**
 * <p>
 * 用户空间收藏记录表 服务类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2021-02-09
 */
public interface WikiSpaceFavoriteService extends IService<WikiSpaceFavorite> {
	
	List<WikiSpaceFavorite> myFavoriteSpaceList();
}
