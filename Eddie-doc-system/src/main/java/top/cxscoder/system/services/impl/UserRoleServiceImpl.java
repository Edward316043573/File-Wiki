package top.cxscoder.system.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.system.domain.entity.UserRole;
import top.cxscoder.system.mapper.UserRoleMapper;
import top.cxscoder.system.services.UserRoleService;

/**
 * @author: Wang Jianping
 * @date: 2023/12/19 15:29
 */

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
