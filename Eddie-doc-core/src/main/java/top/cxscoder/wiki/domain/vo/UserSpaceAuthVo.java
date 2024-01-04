package top.cxscoder.wiki.domain.vo;

import lombok.Data;

/**
 * 用户空间权限信息
 *
 * @author 暮光：城中城
 * @since 2021-02-09
 */
@Data
public class UserSpaceAuthVo {
    private Long roleId;
    private Integer editPage;
    private Integer commentPage;
    private Integer deletePage;
    private Integer pageFileUpload;
    private Integer pageFileDelete;
    private Integer pageAuthManage;

}
