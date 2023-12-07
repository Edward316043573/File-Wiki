package top.cxscoder.wiki.service.manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.wiki.repository.manage.entity.UserSetting;
import top.cxscoder.wiki.repository.manage.mapper.UserSettingMapper;
import top.cxscoder.wiki.repository.support.consts.UserSettingConst;
import top.cxscoder.wiki.service.manage.UserSettingService;

/**
 * <p>
 * 用户设置表 服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2021-02-09
 */
@Service
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements UserSettingService {
	
	@Override
	public String getMySettingValue(String name) {
		LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User currentUser = loginUser.getUser();
		LambdaQueryWrapper<UserSetting> settingWrapper = new LambdaQueryWrapper<>();
		settingWrapper.eq(UserSetting::getUserId, currentUser.getUserId());
		settingWrapper.eq(UserSetting::getName, UserSettingConst.WIKI_ONLY_SHOW_FAVORITE);
		settingWrapper.eq(UserSetting::getDelFlag, 0);
		UserSetting userSetting = this.getOne(settingWrapper);
		if (userSetting == null) return null;
		return userSetting.getValue();
	}
}
