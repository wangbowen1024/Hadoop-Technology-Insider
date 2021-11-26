package org.bigdata.hadoop.common.proxy;

/**
 * 查询状态接口实现类
 */
public class DPQueryStatusImpl implements DPQueryStatus{
    /**
     * 判断是否存在，如果以a开头的文件名则存在，否则不存在
     */
    @Override
    public boolean exist(String filename) {
        return filename.startsWith("a");
    }
}
