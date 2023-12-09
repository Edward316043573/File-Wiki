package top.cxscoder.wiki.service.manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.wiki.domain.entity.WikiSpaceFavorite;
import top.cxscoder.wiki.repository.mapper.WikiSpaceFavoriteMapper;
import top.cxscoder.wiki.service.manage.WikiSpaceFavoriteService;

import java.util.List;

/**
 * <p>
 * 用户空间收藏记录表 服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2021-02-09
 */
@Service
public class WikiSpaceFavoriteServiceImpl extends ServiceImpl<WikiSpaceFavoriteMapper, WikiSpaceFavorite> implements WikiSpaceFavoriteService {
	
	@Override
	public List<WikiSpaceFavorite> myFavoriteSpaceList() {
		LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User currentUser = loginUser.getUser();
		LambdaQueryWrapper<WikiSpaceFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
		favoriteWrapper.eq(WikiSpaceFavorite::getUserId, currentUser.getUserId());
		favoriteWrapper.eq(WikiSpaceFavorite::getDelFlag, 0);
		return this.list(favoriteWrapper);
	}
}
