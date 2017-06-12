package com.boddi.multidatasource.config;

import com.boddi.multidatasource.enums.RWType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DataSourceConfigRegistrar
    implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigRegistrar.class);

  private Environment environment;

  private BeanFactory beanFactory;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    // load configuration related properties
//    final DBGroupProperties dbGroupProperties = beanFactory.getBean(DBGroupProperties.class);
    final DBGroupProperties dbGroupProperties = loadDBGroupProperties(registry);

    // iterate the properties and register the bean definitions (and the
    // most important their dependencies) to spring
    for (Map.Entry<String, DataSourcesConfig> entry : dbGroupProperties.getGroups().entrySet()) {

      final String groupName = entry.getKey();

      final DataSourcesConfig dataSourcesConfig = entry.getValue();

      dataSourcesConfig.init();

      final DBBeanDefinition dbBeanDefinition = new DBBeanDefinition(groupName);

      if (dataSourcesConfig.enableMasterSlave()) {
        multiDataSourceDefinition(dbBeanDefinition, registry, dataSourcesConfig);
      } else {
        nonMutiDataSourceDefinition(dbBeanDefinition, registry, dataSourcesConfig);
      }
      // mybatis scan mapper annotations
    }
  }

  /**
   * use ConfigurationPropertiesBindingPostProcessor to load properties
   *
   * @param registry
   * @return
   * @throws Exception
   */
  private DBGroupProperties loadDBGroupProperties(BeanDefinitionRegistry registry) {
    final DBGroupProperties dbGroupProperties = new DBGroupProperties();
    try {
      final ConfigurationPropertiesBindingPostProcessor processor = new ConfigurationPropertiesBindingPostProcessor();
      processor.setEnvironment(environment);
      processor.setBeanFactory(beanFactory);
      processor.setConversionService(new DefaultConversionService());
      processor.setValidator(dbGroupProperties);

      // in case the PropertySourcesPlaceholderConfigurer is not
      // registered in spring
      String[] propertySourcesPlaceholderConfigurerBeanNames = ((ConfigurableListableBeanFactory) beanFactory)
          .getBeanNamesForType(PropertySourcesPlaceholderConfigurer.class);
      if (propertySourcesPlaceholderConfigurerBeanNames == null || propertySourcesPlaceholderConfigurerBeanNames.length == 0) {
        registry.registerBeanDefinition(PropertySourcesPlaceholderConfigurer.class.getSimpleName(),
            BeanDefinitionBuilder.genericBeanDefinition(PropertySourcesPlaceholderConfigurer.class)
                                 .getBeanDefinition());
      }

      final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = this.beanFactory
          .getBean(PropertySourcesPlaceholderConfigurer.class);
      propertySourcesPlaceholderConfigurer.postProcessBeanFactory((ConfigurableListableBeanFactory) beanFactory);

      processor.afterPropertiesSet();
      processor.postProcessBeforeInitialization(dbGroupProperties, DBGroupProperties.class.getName());
    } catch (Exception e) {
      LOGGER.error("failed to load configuration properties, error: ", e);
      throw new IllegalArgumentException(e);
    }
    return dbGroupProperties;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  private String multiDataSourceDefinition(final DBBeanDefinition dbBeanDefinition,
      final BeanDefinitionRegistry registry,
      final DataSourcesConfig dataSourcesConfig) {

    //register master and slave DataSource
    String masterDataSourceName = null;
    List<String> slaveDataSourceNames = new ArrayList<>();
    for (int i = 0; i < dataSourcesConfig.getDatasources().size(); i++) {
      DataSourceConfig dataSourceConfig = dataSourcesConfig.getDatasources().get(i);
      if (dataSourceConfig.getRwType() == RWType.W) {
        masterDataSourceName = dbBeanDefinition.defineMasterDataSource(registry, dataSourceConfig);
      } else {
        slaveDataSourceNames.add(dbBeanDefinition.defineSlaveDataSource(registry, dataSourceConfig, i + ""));
      }
    }

    // register masterSlaveDataSource
    final String dynamicDataSourceBeanName = dbBeanDefinition.defineMasterSlaveDataSource(
        registry, masterDataSourceName, slaveDataSourceNames);

    // register transacton manager
    dbBeanDefinition.defineTransactionManager(registry, dynamicDataSourceBeanName);

    return null;
  }

  private String nonMutiDataSourceDefinition(final DBBeanDefinition dbBeanDefinition,
      final BeanDefinitionRegistry registry, final DataSourcesConfig dataSourcesConfig) {
    final DataSourceConfig dataSourceConfig = dataSourcesConfig.uniqueDataSourceProperty();

    // register normal datasource
    final String dataSourceBeanName = dbBeanDefinition.defineDataSource(registry, dataSourceConfig);

    // register wrapper datasource
    String wrapperDataSourceBeanName = dbBeanDefinition.defineWrapperDataSource(registry, dataSourceBeanName);

    // register transacton manager
    dbBeanDefinition.defineTransactionManager(registry, wrapperDataSourceBeanName);

    return null;
  }
}
