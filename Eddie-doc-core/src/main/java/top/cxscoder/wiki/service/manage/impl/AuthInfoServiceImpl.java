package top.cxscoder.wiki.service.manage.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.domain.entity.AuthInfo;
import top.cxscoder.wiki.repository.mapper.AuthInfoMapper;
import top.cxscoder.wiki.service.manage.AuthInfoService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2018-12-03
 */
@Service
public class AuthInfoServiceImpl extends ServiceImpl<AuthInfoMapper, AuthInfo> implements AuthInfoService {
	
	@Override
	public AuthInfo getByCode(String authCode) {
		return this.getOne(new QueryWrapper<AuthInfo>().eq("auth_name", authCode));
	}
}
