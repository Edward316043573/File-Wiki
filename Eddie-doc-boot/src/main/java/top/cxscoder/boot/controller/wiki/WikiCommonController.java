package top.cxscoder.boot.controller.wiki;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cxscoder.common.exception.ServiceException;
import top.cxscoder.system.domain.entity.User;
import top.cxscoder.system.security.LoginUser;
import top.cxscoder.system.services.UserService;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.framework.consts.Const;
import top.cxscoder.wiki.repository.mapper.WikiPageFileMapper;
import top.cxscoder.wiki.service.manage.UserInfoService;
import top.cxscoder.wiki.service.manage.WikiPageFileService;
import top.cxscoder.wiki.service.manage.WikiPageService;
import top.cxscoder.wiki.service.manage.WikiSpaceService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/wiki/common")
@RequiredArgsConstructor
public class WikiCommonController {

    private final WikiPageFileService wikiPageFileService;
    private final WikiPageService wikiPageService;
    private final WikiSpaceService wikiSpaceService;
    private final UserInfoService userInfoService;

    private final UserService userService;
    private final WikiPageFileMapper wikiPageFileMapper;

    //    @AuthMan
    @PostMapping("/user/base")
    public IPage<User> userBaseInfo(String search) {
        if (StringUtils.isBlank(search)) {
            return null;
        }
        //todo 用户表查询用户
//        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
//        queryWrapper.and(con -> con.and(conSub -> conSub.like("user_name", search).or().like("user_no", search)
//                .or().like("email", search)).and(conSub -> conSub.eq("del_flag", 0)));
//        queryWrapper.select("id", "user_name");
//        IPage<UserInfo> page = new Page<>(1, 20, false);
//        userInfoService.page(page, queryWrapper);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(con -> con.and(conSub -> conSub.like("user_name", search).or().like("user_no", search)
                .or().like("email", search)).and(conSub -> conSub.eq("del_flag", 0)));
        queryWrapper.select("id", "user_name");
        // 搜索最多返回20条
        IPage<User> page = new Page<>(1, 20, false);
        userService.page(page, queryWrapper);
        return page;
    }


    @GetMapping("/file")
    public void file(HttpServletResponse response, String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new ServiceException("请指定文件ID");
        }
        UpdateWrapper<WikiPageFile> wrapperFile = new UpdateWrapper<>();
        wrapperFile.eq("uuid", uuid);
        WikiPageFile pageFile = wikiPageFileService.getOne(wrapperFile);
        if (pageFile == null) {
            throw new ServiceException("未找到指定文件");
        }
        // 未登录访问文件，需要判断是否是开放空间的文件
        Long pageId = Optional.ofNullable(pageFile.getPageId()).orElse(0L);
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = loginUser.getUser();
        if (pageId > 0 && currentUser == null) {
            WikiPage wikiPage = wikiPageService.getById(pageId);
            WikiSpace wikiSpace = wikiSpaceService.getById(wikiPage.getSpaceId());
            if (wikiSpace.getOpenDoc() == 0) {
                throw new ServiceException("登陆后才可访问此文件");
            }
        }
        // 增加下载次数
        wikiPageFileMapper.addDownloadNum(pageFile.getId());
        try {
            String fileName = Optional.ofNullable(pageFile.getFileName()).orElse("");
            File file = new File(pageFile.getFileUrl());
            String fileSuffix = "";
            if (fileName.lastIndexOf(".") >= 0) {
                fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            }
            String mimeStr = Optional.ofNullable(Const.mimeMap.get(fileSuffix)).orElse("text/plain");
            response.setContentType(mimeStr);
            response.setHeader("Content-disposition", "inline;filename=" + URLEncoder.encode(fileName, "UTF-8"));
//			response.setHeader("Content-disposition", "inline;filename=" + fileName);
//			response.setHeader("Content-Disposition", "inline; fileName=" + fileName + ";filename*=utf-8''" + URLEncoder.encode(fileName, "UTF-8"));
            InputStream inputStream = Files.newInputStream(file.toPath());
            OutputStream os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
            os.close();
            inputStream.close();
        } catch (Exception e) {
            log.info("失败：{}", e.getMessage());
        }
        throw new ServiceException("登陆后才可访问此文件");
    }
}

