package com.boddi.multidatasource.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "multidatasource")
public class DBGroupProperties implements Validator {

  @Getter
  @Setter
  private Map<String, DataSourcesConfig> groups;

  @Override
  public boolean supports(Class<?> clazz) {
    return DBGroupProperties.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    if (groups == null || groups.isEmpty())
      throw new IllegalArgumentException("datasource group cannot be null");

    for (Map.Entry<String, DataSourcesConfig> dataSourcesConfigEntry : groups.entrySet()) {
      DataSourcesConfig config = dataSourcesConfigEntry.getValue();
      if (null == config)
        throw new IllegalArgumentException("DataSourcesConfig cannot be null");
      config.validate();
    }
  }
}
