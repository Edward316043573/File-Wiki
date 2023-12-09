package top.cxscoder.wiki.batch.strategy.comb;

import top.cxscoder.wiki.domain.entity.DocEntry;
import top.cxscoder.wiki.batch.strategy.base.IConditionalStrategy;

import java.io.File;
import java.util.ArrayList;


/**
 * 条件控制依赖梳理策略接口
 *
 * @author Sh1yu
 * @since 20230717
 */
public interface ICombDependencyStrategy extends IConditionalStrategy {
    void comb(ArrayList<DocEntry> docs, File file);
}

