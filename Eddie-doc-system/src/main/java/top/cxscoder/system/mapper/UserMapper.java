package top.cxscoder.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.cxscoder.system.domain.entity.User;

/**
 * @author Edward
 * @date 2023-11-30 20:15
 * @copyright Copyright (c) 2023 Edward
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
