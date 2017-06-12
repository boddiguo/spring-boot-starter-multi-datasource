
package com.boddi.multidatasource.adapter;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * 声明不支持操作的数据源对象.
 * 
 */
public abstract class AbstractUnsupportedOperationDataSource extends WrapperAdapter implements DataSource {
    
    @Override
    public final int getLoginTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("unsupported getLoginTimeout()");
    }
    
    @Override
    public final void setLoginTimeout(final int seconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("unsupported setLoginTimeout(int seconds)");
    }
}
