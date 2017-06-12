package com.boddi.multidatasource.config;


import com.boddi.multidatasource.enums.RWType;

import org.springframework.util.StringUtils;

public class DataSourceConfig {

  private String driverClassName;

  private String url;

  private String username;

  private String password;

  private Integer initialSize = 1;

  private Integer minIdle = 1;

  private Integer maxActive = 20;

  private Integer maxWait = 60000;

  private Integer timeBetweenEvictionRunsMillis = 60 * 1000;

  private Integer minEvictableIdleTimeMillis = 30 * 1000;

  private Boolean testOnBorrow = true;

  private Boolean testOnReturn = false;

  private String validationQuery = "SELECT 1";

  private Boolean testWhileIdle = true;

  private Boolean poolPreparedStatements = false;

  private Integer maxPoolPreparedStatementPerConnectionSize = -1;

  /**
   * define the current datasource is read(slave) or write(master) model
   */
  private RWType rwType = RWType.W;

  // for druiddatasource keep alive
  private Boolean keepAlive = true;

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public RWType getRwType() {
    return rwType;
  }

  public void setRwType(RWType rwType) {
    this.rwType = rwType;
  }

  public Integer getInitialSize() {
    return initialSize;
  }

  public void setInitialSize(Integer initialSize) {
    this.initialSize = initialSize;
  }

  public Integer getMinIdle() {
    return minIdle;
  }

  public void setMinIdle(Integer minIdle) {
    this.minIdle = minIdle;
  }

  public Integer getMaxActive() {
    return maxActive;
  }

  public void setMaxActive(Integer maxActive) {
    this.maxActive = maxActive;
  }

  public Integer getMaxWait() {
    return maxWait;
  }

  public void setMaxWait(Integer maxWait) {
    this.maxWait = maxWait;
  }

  public Integer getTimeBetweenEvictionRunsMillis() {
    return timeBetweenEvictionRunsMillis;
  }

  public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
    this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
  }

  public Integer getMinEvictableIdleTimeMillis() {
    return minEvictableIdleTimeMillis;
  }

  public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
    this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
  }

  public Boolean getTestOnBorrow() {
    return testOnBorrow;
  }

  public void setTestOnBorrow(Boolean testOnBorrow) {
    this.testOnBorrow = testOnBorrow;
  }

  public Boolean getTestOnReturn() {
    return testOnReturn;
  }

  public void setTestOnReturn(Boolean testOnReturn) {
    this.testOnReturn = testOnReturn;
  }

  public String getValidationQuery() {
    return validationQuery;
  }

  public void setValidationQuery(String validationQuery) {
    this.validationQuery = validationQuery;
  }

  public Boolean getTestWhileIdle() {
    return testWhileIdle;
  }

  public void setTestWhileIdle(Boolean testWhileIdle) {
    this.testWhileIdle = testWhileIdle;
  }

  public Boolean getPoolPreparedStatements() {
    return poolPreparedStatements;
  }

  public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
    this.poolPreparedStatements = poolPreparedStatements;
  }

  public Integer getMaxPoolPreparedStatementPerConnectionSize() {
    return maxPoolPreparedStatementPerConnectionSize;
  }

  public void setMaxPoolPreparedStatementPerConnectionSize(Integer maxPoolPreparedStatementPerConnectionSize) {
    this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
  }

  public Boolean getKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(final Boolean keepAlive) {
    this.keepAlive = keepAlive;
  }

  public void validate() {
    if (!StringUtils.hasText(url))
      throw new IllegalArgumentException("connection url cannot be null");
    if (!StringUtils.hasText(username))
      throw new IllegalArgumentException("username cannot be null");
    if (!StringUtils.hasText(password))
      throw new IllegalArgumentException("password cannot be null");

  }
}
