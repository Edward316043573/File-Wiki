package top.cxscoder.wiki.service.manage.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.system.services.LoginService;
import top.cxscoder.wiki.domain.entity.UserMessage;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.repository.mapper.WikiPageMapper;
import top.cxscoder.wiki.domain.vo.WikiPageTemplateInfoVo;
import top.cxscoder.wiki.common.constant.DocSysType;
import top.cxscoder.wiki.common.constant.UserMsgType;
import top.cxscoder.wiki.service.manage.UserMessageService;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 暮光：城中城
 * @since 2019-06-05
 */
@Service
public class WikiPageServiceImpl extends ServiceImpl<WikiPageMapper, WikiPage> implements WikiPageService {
	
	@Resource
	WikiPageMapper wikiPageMapper;
	@Resource
	UserMessageService userMessageService;

	@Value("${wiki.upload-path:}")
	String filePath;

	@Resource
	WikiSpaceService wikiSpaceService;

	@Resource
	WikiPageFileService wikiPageFileService;

	@Resource
	LoginService loginService;
	
	@Override
	public void changeParent(WikiPage wikiPage, Integer beforeSeq, Integer afterSeq) {
		WikiPage wikiPageSel = this.getById(wikiPage.getId());
		if (beforeSeq != null && beforeSeq >= 0) {
			// 在此seq之前
			wikiPageMapper.updateAfterSeq(wikiPageSel.getSpaceId(), wikiPage.getParentId(), beforeSeq);
			wikiPage.setSeqNo(beforeSeq);
		} else if (afterSeq != null && afterSeq >= 0) {
			// 在此seq之后
			wikiPageMapper.updateAfterSeq(wikiPageSel.getSpaceId(), wikiPage.getParentId(), afterSeq + 1);
			wikiPage.setSeqNo(afterSeq + 1);
		} else {
			// 放在末尾
			Integer lastSeq = wikiPageMapper.getLastSeq(wikiPageSel.getSpaceId(), wikiPage.getParentId());
			lastSeq = Optional.ofNullable(lastSeq).orElse(0);
			wikiPage.setSeqNo(lastSeq + 1);
		}
		this.updateById(wikiPage);
		// 重置当前分支的所有节点seq值
		wikiPageMapper.updateChildrenSeq(wikiPageSel.getSpaceId(), wikiPage.getParentId());
		// 给相关人发送消息
		LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User currentUser = loginUser.getUser();
		UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPageSel.getId(), wikiPageSel.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_PARENT);
		userMessage.setAffectUserId(wikiPageSel.getCreateUserId());
		userMessage.setAffectUserName(wikiPageSel.getCreateUserName());
		userMessageService.addWikiMessage(userMessage);
	}
	
	@Override
	public void deletePage(WikiPage wikiPage) {
		// 给相关人发送消息
		User currentUser = loginService.getCurrentUser();
		UserMessage userMessage = userMessageService.createUserMessage(currentUser, wikiPage.getId(), wikiPage.getName(), DocSysType.WIKI, UserMsgType.WIKI_PAGE_DELETE);
		userMessage.setAffectUserId(wikiPage.getCreateUserId());
		userMessage.setAffectUserName(wikiPage.getCreateUserName());
		userMessageService.addWikiMessage(userMessage);
		// 递归删除
		this.deletePageAndSon(wikiPage);
	}

	@Transactional
	public void deletePageAndSon(WikiPage wikiPage) {
		//获取删除文件的路径，判断删除的是文件还是文件夹
		Long pageId = wikiPage.getId();
		LambdaQueryWrapper<WikiPageFile> queryWrapper= new LambdaQueryWrapper<>();
		queryWrapper.eq(WikiPageFile::getPageId,pageId);
		wikiPage = getById(wikiPage);
		WikiPageFile file= wikiPageFileService.getOne(queryWrapper);
		String fileName = wikiPage.getName();
		String path;
		//删除文件
		if (file != null){
			String fileUrl = file.getFileUrl();
			path = filePath + File.separator + fileUrl;
		}
		//删除文件夹
		else {
			WikiSpace wikiSpace = wikiSpaceService.getById(wikiPage.getSpaceId());
			StringBuffer filePathBuffer = new StringBuffer();
			filePathBuffer.append(wikiSpace.getName()).append(File.separator);
			Long parentId = wikiPage.getParentId();
			Stack<String> s = new Stack<>();
			while(parentId != 0 ){
				WikiPage parentPage = getById(parentId);
				String parentName = parentPage.getName();
				s.push(parentName);
				parentId = parentPage.getParentId();
			}
			while(!s.isEmpty()) {
				filePathBuffer.append(s.pop()).append(File.separator);
			}
			path = filePath + File.separator + filePathBuffer + fileName;
		}
		File deleteFile = new File(path);
		try {
			deleteFile(deleteFile);
		} catch (Exception e) {
			throw new ServiceException("删除文件失败");
		}
		this.removeById(wikiPage);
		if (file!=null){
			wikiPageFileService.removeById(file);
		}
		QueryWrapper<WikiPage> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", 0);
		wrapper.eq("parent_id", wikiPage.getId());
		List<WikiPage> wikiPageList = this.list(wrapper);
		if (CollectionUtil.isEmpty(wikiPageList)) {
			return;
		}
		// 递归删除子页面
		for (WikiPage page : wikiPageList) {
			wikiPage.setId(page.getId());
			this.deletePageAndSon(wikiPage);
		}
	}

	public List<WikiPageTemplateInfoVo> wikiPageTemplateInfos(Long spaceId){
		return wikiPageMapper.getWikiPageTemplateInfos(spaceId);
	}


	//递归删除文件夹
	private void deleteFile(File file) {
		if (file.exists()) {//判断文件是否存在
			if (file.isFile()) {//判断是否是文件
				file.delete();//删除文件
			} else if (file.isDirectory()) {//否则如果它是一个目录
				File[] files = file.listFiles();//声明目录下所有的文件 files[];
				for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
					this.deleteFile(files[i]);//把每个文件用这个方法进行迭代
				}
				file.delete();//删除文件夹
			}
		}
	}
}
