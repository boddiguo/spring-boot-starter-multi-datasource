package com.boddi.multidatasource.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by fushihai on 17-3-30.
 */
public class DataSourcesConfig {

  /**
   * datasource list, only support one master multi slaves model
   */
  @NestedConfigurationProperty
  private List<DataSourceConfig> datasources;

  /**
   * define the db type, to indicate the sql dialect
   */
  private DatabaseType dbType = DatabaseType.MYSQL;


  public List<DataSourceConfig> getDatasources() {
    return datasources;
  }

  public void setDatasources(List<DataSourceConfig> datasources) {
    this.datasources = datasources;
  }

  public boolean enableMasterSlave() {
    return datasources.size() > 1;
  }

  /**
   * get the unique datasource property if disabled master-slave model
   *
   * @return
   */
  public DataSourceConfig uniqueDataSourceProperty() {
    if (enableMasterSlave())
      throw new IllegalStateException("error, multi datasources are defined");
    return datasources.get(0);
  }

  public DatabaseType getDbType() {
    return dbType;
  }

  public void setDbType(DatabaseType dbType) {
    this.dbType = dbType;
  }

  /**
   * init method
   */
  public void init() {
    // check if default driverClassName should be set to each datasource or not
    for (DataSourceConfig datasource : datasources) {
      if (!StringUtils.hasText(datasource.getDriverClassName()))
        datasource.setDriverClassName(this.dbType.driverClassName);
    }
  }

  public void validate() {
    if (CollectionUtils.isEmpty(datasources))
      throw new IllegalArgumentException("datasource configs cannot be null");

    for (DataSourceConfig datasource : datasources) {
      datasource.validate();
    }
  }

  public enum DatabaseType {
    MYSQL("mysql", "com.mysql.jdbc.Driver"), ORACLE("oracle", "oracle.jdbc.OracleDriver");

    private String code;

    private String driverClassName;

    DatabaseType(String code, String driverClassName) {
      this.code = code;
      this.driverClassName = driverClassName;
    }

    public String code() {
      return code;
    }

    public String driverClassName() {
      return driverClassName;
    }
  }

}