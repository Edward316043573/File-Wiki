package top.cxscoder.wiki.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *      ģ����Ϣ
 * </p>
 *
 * @author Sh1yu
 * @since 2023-08-24
 */
@Data
@TableName("wiki_page_template")
public class WikiPageTemplate implements Serializable {
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;
    private Long spaceId;
    private Long pageId;
    private String tagName;
    private Boolean shareStatus;
    private Date created;
    private Long createUserId;
    private String createUser;
    private Integer yn;
}
