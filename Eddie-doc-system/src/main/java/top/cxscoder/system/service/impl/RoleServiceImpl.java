package top.cxscoder.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.system.entity.Role;
import top.cxscoder.system.mapper.RoleMapper;
import top.cxscoder.system.service.RoleService;

/**
 * @author Edward
 * @date 2023-12-01 20:58
 * @copyright Copyright (c) 2023 Edward
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
