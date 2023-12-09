package top.cxscoder.wiki.service.manage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.domain.entity.UserGroupRelation;
import top.cxscoder.wiki.repository.mapper.UserGroupRelationMapper;
import top.cxscoder.wiki.service.manage.UserGroupRelationService;

/**
 * <p>
 * 用户和用户组关系表 服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2021-02-08
 */
@Service
public class UserGroupRelationServiceImpl extends ServiceImpl<UserGroupRelationMapper, UserGroupRelation> implements UserGroupRelationService {

}
