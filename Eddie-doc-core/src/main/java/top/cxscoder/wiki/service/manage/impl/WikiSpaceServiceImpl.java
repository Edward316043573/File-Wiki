package top.cxscoder.wiki.service.manage.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.domain.entity.WikiSpace;
import top.cxscoder.wiki.repository.mapper.WikiSpaceMapper;
import top.cxscoder.wiki.service.manage.WikiSpaceService;


@Service
public class WikiSpaceServiceImpl extends ServiceImpl<WikiSpaceMapper, WikiSpace> implements WikiSpaceService {

}
