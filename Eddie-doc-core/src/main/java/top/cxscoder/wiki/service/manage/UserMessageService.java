package top.cxscoder.wiki.service.manage;


import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.wiki.repository.manage.entity.UserMessage;
import top.cxscoder.wiki.repository.support.consts.DocSysType;
import top.cxscoder.wiki.repository.support.consts.UserMsgType;

/**
 * <p>
 * 用户消息表 服务类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2020-06-23
 */
public interface UserMessageService extends IService<UserMessage> {
	
	void addWikiMessage(UserMessage userMessage);
	
	UserMessage createUserMessage(User user, Long pageId, String dataDesc, DocSysType sysType, UserMsgType msgType);
}
