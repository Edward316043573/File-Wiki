package top.cxscoder.wiki.service.manage.impl;

import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.wiki.domain.entity.WikiPageHistory;
import top.cxscoder.wiki.repository.mapper.WikiPageHistoryMapper;
import top.cxscoder.wiki.service.manage.WikiPageHistoryService;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2020-09-05
 */
@Service
public class WikiPageHistoryServiceImpl extends ServiceImpl<WikiPageHistoryMapper, WikiPageHistory> implements WikiPageHistoryService {
	private static final Logger logger = LoggerFactory.getLogger(WikiPageHistoryServiceImpl.class);
	
	@Override
	public WikiPageHistory saveRecord(Long spaceId, Long pageId, String content) {
		LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User currentUser = loginUser.getUser();
		WikiPageHistory entity = new WikiPageHistory();
		entity.setPageId(pageId);
		entity.setCreateTime(new Date());
		entity.setDelFlag(0);
		try {
			entity.setContent(ZipUtil.gzip(content, StandardCharsets.UTF_8.name()));
		} catch (Exception e) {
			logger.error("创建历史记录失败", e);
			throw new ServiceException("创建历史记录失败：" + e.getMessage(), e);
		}
		entity.setCreateUserId(currentUser.getUserId());
		entity.setCreateUserName(currentUser.getUserName());
		this.save(entity);
		return entity;
	}
}
