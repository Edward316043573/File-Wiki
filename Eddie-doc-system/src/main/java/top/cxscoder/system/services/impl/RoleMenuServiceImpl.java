package top.cxscoder.system.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.system.domain.entity.RoleMenu;
import top.cxscoder.system.mapper.RoleMenuMapper;
import top.cxscoder.system.services.RoleMenuService;

/**
 * @author: Wang Jianping
 * @date: 2023/12/26 20:11
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
}
