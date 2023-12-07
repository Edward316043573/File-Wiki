package top.cxscoder.wiki.service.manage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.repository.manage.entity.UserGroupAuth;
import top.cxscoder.wiki.repository.manage.mapper.UserGroupAuthMapper;
import top.cxscoder.wiki.service.manage.UserGroupAuthService;

/**
 * <p>
 * 用户组在各项目内的授权关系 服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2021-02-09
 */
@Service
public class UserGroupAuthServiceImpl extends ServiceImpl<UserGroupAuthMapper, UserGroupAuth> implements UserGroupAuthService {

}
