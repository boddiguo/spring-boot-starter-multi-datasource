package com.boddi.multidatasource;

import com.boddi.multidatasource.connection.WrapperConnection;
import com.boddi.multidatasource.enums.SQLStatementType;
import com.boddi.multidatasource.holder.HintManagerHolder;
import com.boddi.multidatasource.strategy.RoundRobinSlaveLoadBalanceStrategy;
import com.boddi.multidatasource.strategy.SlaveLoadBalanceStrategy;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 支持读写分离的数据源.
 *
 */
public final class MasterSlaveDataSource extends WrapperDataSource {
    
    private static final ThreadLocal<Boolean> DML_FLAG = new ThreadLocal<Boolean>() {
        
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Getter
    private List<DataSource> slaveDataSources;

    @Getter
    @Setter
    private final SlaveLoadBalanceStrategy slaveLoadBalanceStrategy = new RoundRobinSlaveLoadBalanceStrategy();

    public MasterSlaveDataSource(final String dataSourceName, final DataSource dataSource, final List<DataSource> slaveDataSources) {
      super(dataSourceName, dataSource);
      this.slaveDataSources = slaveDataSources;
    }

  public static boolean isDML(final SQLStatementType sqlStatementType) {
        return SQLStatementType.SELECT != sqlStatementType || DML_FLAG.get() || HintManagerHolder.isMasterRouteOnly();
    }
    
    /**
     * 获取主或从节点的数据源名称.
     *
     * @param sqlStatementType SQL类型
     * @return 主或从节点的数据源
     */
    public DataSource getDataSource(final SQLStatementType sqlStatementType) {
        if (isDML(sqlStatementType)) {
            DML_FLAG.set(true);
            return getDataSource();
        }
        return slaveLoadBalanceStrategy.getDataSource(getDataSourceName(), slaveDataSources);
    }

    public String getDataSourceName(final SQLStatementType sqlStatementType) {
        if (isDML(sqlStatementType)) {
            DML_FLAG.set(true);
            return getDataSourceName() + "-MULTI-MASTER";
        } else {
            return getDataSourceName() + "-MULTI-SLAVE";
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
        return new WrapperConnection(getDataSourceName(),this);
    }


    /**
     * 重置更新标记.
     */
    public static void resetDMLFlag() {
        DML_FLAG.remove();
    }

}
