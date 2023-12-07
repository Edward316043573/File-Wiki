package top.cxscoder.wiki.repository.manage.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.cxscoder.wiki.repository.manage.entity.UserGroup;
import top.cxscoder.wiki.repository.manage.entity.UserInfo;

import java.util.List;

/**
 * <p>
 * 用户组 Mapper 接口
 * </p>
 *
 * @author 暮光：城中城
 * @since 2021-02-08
 */
public interface UserGroupMapper extends BaseMapper<UserGroup> {
	
	@Select("select b.id, b.user_no, b.email, b.phone, b.sex, b.user_name, b.avatar from user_group_relation a join user_info b on b.id=a.user_id where a.group_id=#{groupId} and a.del_flag=0 and b.del_flag=0")
	List<UserInfo> groupUserList(@Param("groupId") Long groupId);
}
