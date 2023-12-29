package top.cxscoder.wiki.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 最新文档信息
 *
 * @author 暮光：城中城
 * @since 2019-06-14
 */
@Data
public class SpaceNewsVo {

	private String space;
	private Long spaceId;
	private Long pageId;
	private Integer zanNum;
	private Integer viewNum;
	private String createUserName;
	private String updateUserName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date updateTime;

	private String spaceName;
	private String pageTitle;
	private String previewContent;

}
