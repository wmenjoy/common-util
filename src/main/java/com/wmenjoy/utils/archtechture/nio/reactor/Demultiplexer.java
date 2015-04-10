package com.wmenjoy.utils.archtechture.nio.reactor;

import java.util.List;

/**
 * 负责调度
 * @author jinliang.liu
 *
 */
public interface Demultiplexer {
    /**
     * 选择
     * @return
     */
    public List<Handler> select();
}
