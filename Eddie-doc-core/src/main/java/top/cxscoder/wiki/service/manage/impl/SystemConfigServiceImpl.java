package top.cxscoder.wiki.service.manage.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.wiki.enums.SystemConfigEnum;
import top.cxscoder.wiki.exception.ConfirmException;
import top.cxscoder.wiki.repository.manage.entity.SystemConfig;
import top.cxscoder.wiki.repository.manage.mapper.SystemConfigMapper;
import top.cxscoder.wiki.service.manage.SystemConfigService;

import java.util.Date;

/**
 * <p>
 * 系统配置表 服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2022-12-01
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {
	
	/**
	 * 保存
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public SystemConfig saveRecord(SystemConfig systemConfig) {
		LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User currentUser = loginUser.getUser();
		systemConfig.setModified(new Date());
		if (currentUser != null) {
			systemConfig.setModifyUser(currentUser.getUserName());
			systemConfig.setModifyUserId(currentUser.getUserId());
		}
		if (systemConfig.getId() == null) {
			systemConfig.setYn(1);
			systemConfig.setCreated(new Date());
			if (currentUser != null) {
				systemConfig.setCreateUser(currentUser.getUserName());
				systemConfig.setCreateUserId(currentUser.getUserId());
			}
			this.save(systemConfig);
		} else {
			this.updateById(systemConfig);
		}
		return systemConfig;
	}
	
	/**
	 * 删除
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRecord(Long id) {
		SystemConfig systemConfig = getById(id);
		if (systemConfig == null) {
			throw new ConfirmException("未找到该记录");
		}
		this.removeById(id);
	}
	
	@Override
	public void deleteConfig(SystemConfigEnum configEnum) {
		this.remove(new QueryWrapper<SystemConfig>().lambda().eq(SystemConfig::getConfigKey, configEnum.getKey()));
	}
	
	@Override
	public String getConfigValue(SystemConfigEnum configEnum) {
		try {
			SystemConfig systemConfig = this.getOne(new QueryWrapper<SystemConfig>().lambda()
					.eq(SystemConfig::getConfigKey, configEnum.getKey()));
			if (systemConfig != null) {
				return systemConfig.getConfigValue();
			}
		} catch (Exception e) {
			log.error("获取系统配置失败");
		}
		return null;
	}
	
	@Override
	public <T> T getConfigValue(SystemConfigEnum configEnum, Class<T> cls) {
		String configValue = this.getConfigValue(configEnum);
		if (StringUtils.isNotBlank(configValue)) {
			try {
				return JSON.parseObject(configValue, cls);
			} catch (Exception e) {
				log.error("转换配置内容为对象失败", e);
			}
		}
		return null;
	}
	
	@Override
	public void setConfigValue(SystemConfigEnum configEnum, String value) {
		SystemConfig systemConfig = this.getOne(new QueryWrapper<SystemConfig>().lambda()
				.eq(SystemConfig::getConfigKey, configEnum.getKey()));
		SystemConfig systemConfigUp = new SystemConfig();
		systemConfigUp.setConfigKey(configEnum.getKey());
		systemConfigUp.setConfigValue(value);
		if (systemConfig != null) {
			systemConfigUp.setId(systemConfig.getId());
		}
		this.saveRecord(systemConfigUp);
	}
	
	@Override
	public void setConfigValue(SystemConfigEnum configEnum, Object value) {
		this.setConfigValue(configEnum, JSON.toJSONString(value));
	}
}
