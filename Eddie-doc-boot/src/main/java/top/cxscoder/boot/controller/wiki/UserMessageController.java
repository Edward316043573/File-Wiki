package top.cxscoder.boot.controller.wiki;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.domain.dto.MessageDTO;
import top.cxscoder.wiki.domain.entity.UserMessage;
import top.cxscoder.wiki.security.DocUserDetails;
import top.cxscoder.wiki.security.DocUserUtil;
import top.cxscoder.wiki.service.manage.UserMessageService;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 用户消息控制器
 *
 * @author 暮光：城中城
 * @since 2020年6月25日
 */
@RestController
@RequestMapping("/wiki/user/message")
public class UserMessageController {
	
	@Resource
	UserMessageService userMessageService;

	@Resource
	LoginService loginService;
	/**
	 * 消息列表
	 *
	 * @param messageDTO   请求参数
	 * @return 数据列表
	 */
	@PostMapping("/list")
	public IPage<UserMessage> list(@RequestBody MessageDTO messageDTO) {
		User currentUser = loginService.getCurrentUser();
		IPage<UserMessage> page = new Page<>(messageDTO.getPage(), messageDTO.getPageSize());
		QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
		wrapper.eq("accept_user_id", currentUser.getUserId());
		wrapper.eq(messageDTO.getSysType() != null, "sys_type", messageDTO.getSysType());
		wrapper.orderByAsc("msg_status").orderByDesc("creation_time");
//		if (msgStatus != null && msgStatus >= 0) {
//			wrapper.eq("msg_status", msgStatus);
//		}
		wrapper.notIn("msg_status", 2);
		userMessageService.page(page, wrapper);
		return page;
	}
	
	/**
	 * 更新消息已读状态
	 *
	 * @param ids 消息IDS
	 * @return 是否成功
	 */
	@PostMapping("/read")
	public boolean read(String ids) {
		return this.update(ids, 1);

	}
	
	/**
	 * 删除消息
	 *
	 * @param ids 消息IDS
	 * @return 是否成功
	 */
	@PostMapping("/delete")
	public boolean delete(String ids) {
		return this.update(ids, 2);
	}
	
	/**
	 * 更新消息状态
	 *
	 * @param ids    消息IDS
	 * @param status 状态
	 */
	public boolean update(String ids, Integer status) {
		if (StringUtils.isBlank(ids)) {
			throw new ServiceException("没有要删除的消息");
		}
		DocUserDetails currentUser = DocUserUtil.getCurrentUser();
		QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
		wrapper.in("id", Arrays.asList(ids.split(",")));
		wrapper.eq("accept_user_id", currentUser.getUserId());
		UserMessage msgUp = new UserMessage();
		msgUp.setMsgStatus(status);
		return userMessageService.update(msgUp, wrapper);
	}
}
