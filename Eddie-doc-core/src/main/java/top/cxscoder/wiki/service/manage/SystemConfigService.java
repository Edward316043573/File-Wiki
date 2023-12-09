package top.cxscoder.wiki.service.manage;

import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.wiki.enums.SystemConfigEnum;
import top.cxscoder.wiki.domain.entity.SystemConfig;

/**
 * <p>
 * 系统配置表 服务类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2022-12-01
 */
public interface SystemConfigService extends IService<SystemConfig> {
	
	/**
	 * 保存或更新
	 */
	SystemConfig saveRecord(SystemConfig systemConfig);
	
	/**
	 * 物理删除
	 */
	void deleteRecord(Long id);
	
	/**
	 * 删除配置项
	 */
	void deleteConfig(SystemConfigEnum configEnum);
	
	/**
	 * 获取配置值
	 */
	String getConfigValue(SystemConfigEnum configEnum);
	
	/**
	 * 获取配置值
	 */
	<T> T getConfigValue(SystemConfigEnum configEnum, Class<T> cls);
	
	/**
	 * 获取配置值
	 */
	void setConfigValue(SystemConfigEnum configEnum, String value);
	
	/**
	 * 获取配置值
	 */
	void setConfigValue(SystemConfigEnum configEnum, Object value);
}
