package com.boddi.multidatasource;

import com.boddi.multidatasource.adapter.AbstractDataSourceAdapter;
import com.boddi.multidatasource.connection.WrapperConnection;
import com.boddi.multidatasource.enums.SQLStatementType;
import com.boddi.multidatasource.holder.HintManagerHolder;
import com.boddi.multidatasource.strategy.RoundRobinSlaveLoadBalanceStrategy;
import com.boddi.multidatasource.strategy.SlaveLoadBalanceStrategy;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WrapperDataSource extends AbstractDataSourceAdapter {
    
    @Getter
    private final String dataSourceName;

    @Getter
    private final DataSource dataSource;

    /**
     * 获取主或从节点的数据源名称.
     *
     * @param sqlStatementType SQL类型
     * @return 主或从节点的数据源
     */
    public DataSource getDataSource(final SQLStatementType sqlStatementType) {
       return dataSource;
    }

    public String getDataSourceName(final SQLStatementType sqlStatementType) {
        return dataSourceName;
    }


    @Override
    public Connection getConnection() throws SQLException {
        return new WrapperConnection(dataSourceName,this);
    }


}
