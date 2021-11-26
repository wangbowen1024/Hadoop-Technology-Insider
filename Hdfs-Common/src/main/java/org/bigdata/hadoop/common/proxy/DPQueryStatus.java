package org.bigdata.hadoop.common.proxy;

/**
 * 查询状态接口
 */
public interface DPQueryStatus {
    /**
     * 判断文件是否存在
     * @param filename
     * @return
     */
    boolean exist(String filename);
}
