package top.cxscoder.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.system.entity.Menu;
import top.cxscoder.system.mapper.MenuMapper;
import top.cxscoder.system.service.MenuService;

/**
 * @author Edward
 * @date 2023-12-01 20:58
 * @copyright Copyright (c) 2023 Edward
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
}
