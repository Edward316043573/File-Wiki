package top.cxscoder.wiki.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import top.cxscoder.system.domain.VO.PageVo;

import java.util.Date;

/**
 * @author Edward
 * @date 2023-12-11 14:48
 * @copyright Copyright (c) 2023 Edward
 */
@Data
public class WikiPageDTO extends PageVo {
    /**
     * 主键自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 空间ID
     */
    private Long spaceId;

    /**
     * 名字
     */
    private String name;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 节点类型 0=有子节点 1=终节点
     */
    private Integer nodeType;

    /**
     * 赞的数量
     */
    private Integer zanNum;

    /**
     * 编辑类型 0=可编辑 1=不允许编辑
     */
    private Integer editType;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建人名字
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人ID
     */
    private Long updateUserId;

    /**
     * 修改人名字
     */
    private String updateUserName;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 0=有效 1=删除
     */
    private Integer delFlag;

    /**
     * 阅读数
     */
    private Integer viewNum;

    /**
     * 顺序
     */
    private Integer seqNo;

    /**
     * 编辑框类型 1=HTML 2=Markdown
     */
    private Integer editorType;

    String content;

    String preview;
}
