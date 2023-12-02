package top.cxscoder.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * 用户状态
 *
 * @author Edward
 * @date 2023-12-01 0:19
 * @copyright Copyright (c) 2023 Edward
 */
@Getter
@AllArgsConstructor
public enum UserStatus {
    OK("0", "正常"), DISABLE("1", "停用"), DELETED("2", "删除");

    private final String code;

    private final String info;

}

