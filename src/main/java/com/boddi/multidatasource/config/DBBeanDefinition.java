package com.boddi.multidatasource.config;

import com.boddi.multidatasource.MasterSlaveDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.boddi.multidatasource.WrapperDataSource;

import javax.sql.DataSource;

import java.util.List;


public final class DBBeanDefinition {

  private static final String BEAN_NAME_SEPARATOR = "-";

  private static final String TRANSACTION_MANAGER_BEAN_NAME_PREFIX = "TransactionManager";

  private final String groupName;

  public DBBeanDefinition(final String groupName) {
    this.groupName = groupName;
  }

  public String defineDataSource(BeanDefinitionRegistry registry, DataSourceConfig dataSourceConfig) {
    final String beanName = getBeanDefinitionName(DataSource.class, groupName);
    defineDataSource(registry, dataSourceConfig, beanName);
    return beanName;
  }

  public String defineMasterDataSource(BeanDefinitionRegistry registry, DataSourceConfig dataSourceConfig) {
    final String beanName = getBeanDefinitionName(DataSource.class, groupName)+BEAN_NAME_SEPARATOR+"MASTER";
    defineDataSource(registry, dataSourceConfig, beanName);
    return beanName;
  }

  public String defineSlaveDataSource(BeanDefinitionRegistry registry, DataSourceConfig dataSourceConfig, String slaveIndex) {
    final String beanName = getBeanDefinitionName(DataSource.class, groupName)+BEAN_NAME_SEPARATOR+"SLAVE"+BEAN_NAME_SEPARATOR+slaveIndex;
    defineDataSource(registry, dataSourceConfig, beanName);
    return beanName;
  }

  private void defineDataSource(final BeanDefinitionRegistry registry, final DataSourceConfig dataSourceConfig,
      final String beanName) {
    final BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class)
                                                               .addPropertyValue("url", dataSourceConfig.getUrl())
                                                               .addPropertyValue("driverClassName", dataSourceConfig.getDriverClassName())
                                                               .addPropertyValue("username", dataSourceConfig.getUsername())
                                                               .addPropertyValue("password", dataSourceConfig.getPassword())
                                                               .addPropertyValue("initialSize", dataSourceConfig.getInitialSize())
                                                               .addPropertyValue("minIdle", dataSourceConfig.getMinIdle())
                                                               .addPropertyValue("maxActive", dataSourceConfig.getMaxActive())
                                                               .addPropertyValue("maxWait", dataSourceConfig.getMaxWait())
                                                               .addPropertyValue("timeBetweenEvictionRunsMillis", dataSourceConfig.getTimeBetweenEvictionRunsMillis())
                                                               .addPropertyValue("minEvictableIdleTimeMillis", dataSourceConfig.getMinEvictableIdleTimeMillis())
                                                               .addPropertyValue("testOnBorrow", dataSourceConfig.getTestOnBorrow())
                                                               .addPropertyValue("testOnReturn", dataSourceConfig.getTestOnReturn())
                                                               .addPropertyValue("validationQuery", dataSourceConfig.getValidationQuery())
                                                               .addPropertyValue("testWhileIdle", dataSourceConfig.getTestWhileIdle())
                                                               .addPropertyValue("poolPreparedStatements", dataSourceConfig.getPoolPreparedStatements())
                                                               .addPropertyValue("maxPoolPreparedStatementPerConnectionSize",
            dataSourceConfig.getMaxPoolPreparedStatementPerConnectionSize())
                                                               .addPropertyValue("keepAlive", dataSourceConfig.getKeepAlive())
                                                               .getBeanDefinition();
    registry.registerBeanDefinition(beanName, beanDefinition);
  }

  // 3.TransactionManager
  public String defineTransactionManager(BeanDefinitionRegistry registry, String dataSourceBeanName) {
    final String beanName = getBeanDefinitionName(TRANSACTION_MANAGER_BEAN_NAME_PREFIX, groupName);
    final BeanDefinition beanDefinition = BeanDefinitionBuilder
        .genericBeanDefinition(DataSourceTransactionManager.class)
        .addPropertyReference("dataSource", dataSourceBeanName).getBeanDefinition();
    registry.registerBeanDefinition(beanName, beanDefinition);
    return beanName;
  }

  public static String getBeanDefinitionName(Class clazz, String groupName) {
    return getBeanDefinitionName(clazz.getSimpleName(), groupName);
  }

  public static String getBeanDefinitionName(String prefix, String groupName) {
    return new StringBuilder(prefix).append(BEAN_NAME_SEPARATOR).append(groupName).toString();
  }

  public String defineMasterSlaveDataSource(final BeanDefinitionRegistry registry,
      final String masterDataSourceName, final List<String> slaveDataSourceNames) {
    final String beanName = getBeanDefinitionName(DataSource.class, groupName);

    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MasterSlaveDataSource.class);
    beanDefinitionBuilder.addConstructorArgValue(groupName).addConstructorArgReference(masterDataSourceName);
    for (String slaveDataSourceName : slaveDataSourceNames) {
      beanDefinitionBuilder.addConstructorArgReference(slaveDataSourceName);
    }
    final BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
    beanDefinition.setPrimary(true);
    registry.registerBeanDefinition(beanName, beanDefinition);
    return beanName;
  }

  public String defineWrapperDataSource(BeanDefinitionRegistry registry, final String dataSourceName) {
    final String beanName = getBeanDefinitionName(DataSource.class, groupName)+BEAN_NAME_SEPARATOR+"WRAPPER";
    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(WrapperDataSource.class);
    beanDefinitionBuilder.addConstructorArgValue(groupName).addConstructorArgReference(dataSourceName);
    final BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
    registry.registerBeanDefinition(beanName, beanDefinition);
    return beanName;
  }
}