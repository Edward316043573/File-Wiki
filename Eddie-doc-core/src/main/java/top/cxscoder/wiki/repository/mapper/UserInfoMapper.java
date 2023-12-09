package top.cxscoder.wiki.repository.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.cxscoder.wiki.domain.entity.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 暮光：城中城
 * @since 2018-12-03
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {
	
	@Select("show tables")
	List<String> getTableList();
	
	@Select("${sql}")
	List<String> executeSql(@Param("sql") String sql);
	
	@Select("SHOW COLUMNS FROM ${tableName}")
	List<Map<String, Object>> getTableColumnList(@Param("tableName") String tableName);
	
	@Select("SHOW INDEX FROM ${tableName}")
	List<Map<String, Object>> getTableIndexList(@Param("tableName") String tableName);
}
