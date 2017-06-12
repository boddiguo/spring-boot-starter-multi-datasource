/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.boddi.multidatasource.connection;

import com.boddi.multidatasource.MasterSlaveDataSource;
import com.boddi.multidatasource.WrapperDataSource;
import com.boddi.multidatasource.enums.SQLStatementType;
import com.boddi.multidatasource.holder.HintManagerHolder;
import com.boddi.multidatasource.statement.WrapperPreparedStatement;
import com.boddi.multidatasource.statement.WrapperStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支持读写分离的连接
 */
@RequiredArgsConstructor
public final class WrapperConnection extends AbstractConnectionAdapter {

    private static Logger logger = LoggerFactory.getLogger(WrapperStatement.class);

    @Getter(AccessLevel.PACKAGE)
    private final String dataSourceName;

    @Getter(AccessLevel.PACKAGE)
    private final WrapperDataSource dataSource;

    private final Map<String, Connection> connectionMap = new HashMap<>();
    
    /**
     * 根据数据源名称获取相应的数据库连接.
     * 
     * @param sqlStatementType SQL语句类型
     * @return 数据库连接
     */
    public Connection getConnection(final SQLStatementType sqlStatementType) throws SQLException {
        Connection result = getConnectionInternal(sqlStatementType);
        replayMethodsInvocation(result);
        return result;
    }

    private void closeConnection(final Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (final SQLException ignored) {
            }
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getConnection(SQLStatementType.SELECT).getMetaData();
    }
    
    private Connection getConnectionInternal( final SQLStatementType sqlStatementType) throws SQLException {
        Optional<Connection> connectionOptional = fetchCachedConnectionBySqlStatementType(dataSourceName, sqlStatementType);
        if (connectionOptional.isPresent()) {
            return connectionOptional.get();
        }
        DataSource realDataSource = dataSource.getDataSource(sqlStatementType);
        String realDataSourceName = dataSource.getDataSourceName(sqlStatementType);
        logger.info("connection the realDataSourceName:" + realDataSourceName);
        Connection result = realDataSource.getConnection();
        connectionMap.put(realDataSourceName, result);
        return result;
    }
    
    private Optional<Connection> fetchCachedConnectionBySqlStatementType(final String dataSourceName, final SQLStatementType sqlStatementType) {
        if (connectionMap.containsKey(dataSourceName)) {
            return Optional.of(connectionMap.get(dataSourceName));
        }
        String masterDataSourceName = getMasterDataSourceName(dataSourceName);
        if (connectionMap.containsKey(masterDataSourceName)) {
            return Optional.of(connectionMap.get(masterDataSourceName));
        }
        if (MasterSlaveDataSource.isDML(sqlStatementType)) {
            return Optional.absent();
        }
        String slaveDataSourceName = getSlaveDataSourceName(dataSourceName);
        if (connectionMap.containsKey(slaveDataSourceName)) {
            return Optional.of(connectionMap.get(slaveDataSourceName));
        }
        return Optional.absent();
    }

    private String getMasterDataSourceName(final String dataSourceName) {
        return Joiner.on("-").join(dataSourceName, "MULTI", "MASTER");
    }

    private String getSlaveDataSourceName(final String dataSourceName) {
        return Joiner.on("-").join(dataSourceName, "MULTI", "SLAVE");
    }
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return new WrapperPreparedStatement(this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return new WrapperPreparedStatement(this, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return new WrapperPreparedStatement(this, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return new WrapperPreparedStatement(this, sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return new WrapperPreparedStatement(this, sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return new WrapperPreparedStatement(this, sql, columnNames);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new WrapperStatement(this);
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return new WrapperStatement(this, resultSetType, resultSetConcurrency);
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return new WrapperStatement(this, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public Collection<Connection> getConnections() {
        return connectionMap.values();
    }

    @Override
    public void close() throws SQLException {
        super.close();
        HintManagerHolder.clear();
        MasterSlaveDataSource.resetDMLFlag();
    }

}
