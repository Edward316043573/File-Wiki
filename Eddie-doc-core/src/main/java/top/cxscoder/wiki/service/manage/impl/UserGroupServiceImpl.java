package top.cxscoder.wiki.service.manage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.domain.entity.UserGroup;
import top.cxscoder.wiki.repository.mapper.UserGroupMapper;
import top.cxscoder.wiki.service.manage.UserGroupService;

/**
 * <p>
 * 用户组 服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2021-02-08
 */
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup> implements UserGroupService {

}
