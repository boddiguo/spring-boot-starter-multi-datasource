package com.boddi.multidatasource.holder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HintManager implements AutoCloseable {
    
    @Getter
    private boolean masterRouteOnly;

    /**
     * 获取线索分片管理器实例.
     * 
     * @return 线索分片管理器实例
     */
    public static HintManager getInstance() {
        HintManager result = new HintManager();
        HintManagerHolder.setHintManager(result);
        return result;
    }

    /**
     * 设置数据库操作只路由至主库.
     */
    public void setMasterRouteOnly() {
        masterRouteOnly = true;
    }
    
    @Override
    public void close() {
        HintManagerHolder.clear();
    }
}
