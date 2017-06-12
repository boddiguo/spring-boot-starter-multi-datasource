package com.boddi.multidatasource.adapter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import lombok.RequiredArgsConstructor;

/**
 * 数据源适配类.
 * 
 */
@RequiredArgsConstructor
public abstract class AbstractDataSourceAdapter extends AbstractUnsupportedOperationDataSource {
    
    private PrintWriter logWriter = new PrintWriter(System.out);
    
    @Override
    public final PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }
    
    @Override
    public final void setLogWriter(final PrintWriter out) throws SQLException {
        this.logWriter = out;
    }
    
    @Override
    public final Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }
    
    @Override
    public final Connection getConnection(final String username, final String password) throws SQLException {
        return getConnection();
    }
}
