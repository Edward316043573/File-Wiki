package top.cxscoder.wiki.controller.vo;


import lombok.Data;
import top.cxscoder.wiki.repository.manage.entity.WikiPageComment;

import java.util.List;

/**
 * wiki页面评论信息
 *
 * @author 暮光：城中城
 * @since 2019-02-24
 */
@Data
public class WikiPageCommentVo extends WikiPageComment {
	private List<WikiPageComment> commentList;

}
