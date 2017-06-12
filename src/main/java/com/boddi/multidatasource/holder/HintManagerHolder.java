package com.boddi.multidatasource.holder;

import com.google.common.base.Preconditions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 线索分片管理器的本地线程持有者.
 *
 * @author zhangliang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HintManagerHolder {
    
    private static final ThreadLocal<HintManager> HINT_MANAGER_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置线索分片管理器.
     *
     * @param hintManager 线索分片管理器
     */
    public static void setHintManager(final HintManager hintManager) {
        Preconditions.checkState(null == HINT_MANAGER_HOLDER.get(), "HintManagerHolder has previous value, please clear first.");
        HINT_MANAGER_HOLDER.set(hintManager);
    }
    
    /**
     * 判断是否数据库操作只路由至主库.
     * 
     * @return 是否数据库操作只路由至主库
     */
    public static boolean isMasterRouteOnly() {
        return null != HINT_MANAGER_HOLDER.get() && HINT_MANAGER_HOLDER.get().isMasterRouteOnly();
    }

    
    /**
     * 清理线索分片管理器的本地线程持有者.
     */
    public static void clear() {
        HINT_MANAGER_HOLDER.remove();
    }
}
