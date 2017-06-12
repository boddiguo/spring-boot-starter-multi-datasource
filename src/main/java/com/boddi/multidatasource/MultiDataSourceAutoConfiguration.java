package com.boddi.multidatasource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.boddi.multidatasource.config.DBGroupProperties;
import com.boddi.multidatasource.config.DataSourceConfigRegistrar;

/**
 * auto configuration boot
 */
@Configuration
@EnableConfigurationProperties(DBGroupProperties.class)
@EnableTransactionManagement
@Import(DataSourceConfigRegistrar.class)
public class MultiDataSourceAutoConfiguration {


}