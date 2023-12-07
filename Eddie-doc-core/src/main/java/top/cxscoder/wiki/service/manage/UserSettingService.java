package top.cxscoder.wiki.service.manage;


import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.wiki.repository.manage.entity.UserSetting;

/**
 * <p>
 * 用户设置表 服务类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2021-02-09
 */
public interface UserSettingService extends IService<UserSetting> {
	
	String getMySettingValue(String name);
}
