package top.cxscoder.boot.controller.wiki;

import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.anotation.AuthMan;
import top.cxscoder.wiki.domain.dto.WikiPageHistoryDTO;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageHistory;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.framework.consts.SpaceType;
import top.cxscoder.wiki.service.manage.WikiPageHistoryService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 文档控制器
 *
 * @author 暮光：城中城
 * @since 2019年2月17日
 */
@Slf4j
@AuthMan
@RestController
@RequestMapping("/wiki/page/history")
@RequiredArgsConstructor
public class WikiPageHistoryController {
	
	private final WikiPageHistoryService wikiPageHistoryService;
	private final WikiSpaceService wikiSpaceService;
	private final WikiPageService wikiPageService;

	@Resource
	private LoginService loginService;

	@PostMapping("/list")
	public IPage<WikiPageHistory> list(@RequestBody WikiPageHistoryDTO param) {
		User currentUser = loginService.getCurrentUser();
		WikiPage wikiPageSel = wikiPageService.getById(param.getPageId());
		// 私人空间
		if (wikiPageSel == null || Objects.equals(wikiPageSel.getDelFlag(), 1)) {
			return null;
		}
		WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
		// 空间已删除
		if (wikiSpaceSel == null || Objects.equals(wikiSpaceSel.getDelFlag(), 1)) {
			return null;
		}
		// 私人空间
		if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
			throw new ServiceException("您没有权限查看该空间的文章详情！");
		}
		LambdaQueryWrapper<WikiPageHistory> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(WikiPageHistory::getPageId, param.getPageId());
		wrapper.eq(WikiPageHistory::getDelFlag, 0);
		wrapper.orderByDesc(WikiPageHistory::getId);
		wrapper.select(WikiPageHistory::getId, WikiPageHistory::getCreateUserId, WikiPageHistory::getCreateUserName
				, WikiPageHistory::getPageId, WikiPageHistory::getCreateTime);
		IPage<WikiPageHistory> page = new Page<>(param.getPage(), 30, false);
		wikiPageHistoryService.page(page, wrapper);
		return page;
	}
	
	@PostMapping("/detail")
	public String detail(Long id) {
		WikiPageHistory wikiPageHistory = wikiPageHistoryService.getById(id);
		if (wikiPageHistory == null) {
			throw new ServiceException("未找到相关记录");
		}
		User currentUser = loginService.getCurrentUser();
		WikiPage wikiPageSel = wikiPageService.getById(wikiPageHistory.getPageId());
		WikiSpace wikiSpaceSel = wikiSpaceService.getById(wikiPageSel.getSpaceId());
		// 私人空间
		if (SpaceType.isOthersPrivate(wikiSpaceSel.getType(), currentUser.getUserId(), wikiSpaceSel.getCreateUserId())) {
			throw new ServiceException("您没有权限查看该空间的文章详情！");
		}
		try {
			byte[] bytes = ZipUtil.unGzip(wikiPageHistory.getContent());
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.error("解析文档内容失败", e);
			throw new ServiceException("解析文档内容失败：" + e.getMessage());
		}
	}
}

