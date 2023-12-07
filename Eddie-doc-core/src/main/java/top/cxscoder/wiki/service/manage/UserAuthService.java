package top.cxscoder.wiki.service.manage;


import com.baomidou.mybatisplus.extension.service.IService;
import top.cxscoder.wiki.repository.manage.entity.UserAuth;
import top.cxscoder.wiki.security.UserAuthInfo;

import java.util.List;

/**
 * <p>
 * 用户权限表 服务类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-05-31
 */
public interface UserAuthService extends IService<UserAuth> {
	
	List<UserAuthInfo> getUserAuthSet(Long userId);
	
	List<UserAuth> getModuleAuthList(Integer sysType, Integer sysModuleType, Long sysModuleId);
	
	List<UserAuth> getUserModuleAuthList(Long userId, Integer sysType, Integer sysModuleType, Long sysModuleId);
	
	boolean deleteModuleAuth(Integer sysType, Integer sysModuleType, Long sysModuleId);
	
	boolean deleteUserModuleAuth(Long userId, Integer sysType, Integer sysModuleType, Long sysModuleId);
	
}
