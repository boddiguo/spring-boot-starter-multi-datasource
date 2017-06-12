# multi-datasource
support for multi datasource and master slave datasource for spring boot

###Configuration
添加如下配置到classpath下的application.properties文件中

一：多数据配置

multidatasource.groups.ds.dbType=MYSQL
multidatasource.groups.ds.datasources[0].rwType=W
multidatasource.groups.ds.datasources[0].driverClassName=com.mysql.jdbc.Driver
multidatasource.groups.ds.datasources[0].url=jdbc:mysql://localhost:3306/ds_0
multidatasource.groups.ds.datasources[0].username=root
multidatasource.groups.ds.datasources[0].password=123456
multidatasource.groups.ds.datasources[0].initialSize=1
multidatasource.groups.ds.datasources[0].minIdle=1
multidatasource.groups.ds.datasources[0].maxActive=20
multidatasource.groups.ds.datasources[0].maxWait=60000
multidatasource.groups.ds.datasources[0].timeBetweenEvictionRunsMillis=1000
multidatasource.groups.ds.datasources[0].minEvictableIdleTimeMillis=1000
multidatasource.groups.ds.datasources[0].maxPoolPreparedStatementPerConnectionSize=30


multidatasource.groups.ds.dbType=MYSQL
multidatasource.groups.otherds.datasources[0].rwType=W
multidatasource.groups.otherds.datasources[0].driverClassName=com.mysql.jdbc.Driver
multidatasource.groups.otherds.datasources[0].url=jdbc:mysql://localhost:3306/ds_1
multidatasource.groups.otherds.datasources[0].username=root
multidatasource.groups.otherds.datasources[0].password=123456
multidatasource.groups.otherds.datasources[0].initialSize=1
multidatasource.groups.otherds.datasources[0].minIdle=1
multidatasource.groups.otherds.datasources[0].maxActive=20
multidatasource.groups.otherds.datasources[0].maxWait=60000
multidatasource.groups.otherds.datasources[0].timeBetweenEvictionRunsMillis=1000
multidatasource.groups.otherds.datasources[0].minEvictableIdleTimeMillis=1000
multidatasource.groups.otherds.datasources[0].maxPoolPreparedStatementPerConnectionSize=30


以上配置会生成两个数据源service：
名称为DataSource-ds的WrapperDataSource的Bean
名称为DataSource-otherds的WrapperDataSource的Bean
以及数据库事务service：
名称为TransactionManager-ds的DataSourceTransactionManager的Bean
名称为TransactionManager-otherds的DataSourceTransactionManager的Bean


二：master／slave数据源配置

multidatasource.groups.ds.dbType=MYSQL
multidatasource.groups.ds.datasources[0].rwType=W
multidatasource.groups.ds.datasources[0].driverClassName=com.mysql.jdbc.Driver
multidatasource.groups.ds.datasources[0].url=jdbc:mysql://localhost:3306/ds_0
multidatasource.groups.ds.datasources[0].username=root
multidatasource.groups.ds.datasources[0].password=123456
multidatasource.groups.ds.datasources[0].initialSize=1
multidatasource.groups.ds.datasources[0].minIdle=1
multidatasource.groups.ds.datasources[0].maxActive=20
multidatasource.groups.ds.datasources[0].maxWait=60000
multidatasource.groups.ds.datasources[0].timeBetweenEvictionRunsMillis=1000
multidatasource.groups.ds.datasources[0].minEvictableIdleTimeMillis=1000
multidatasource.groups.ds.datasources[0].maxPoolPreparedStatementPerConnectionSize=30
multidatasource.groups.ds.datasources[1].rwType=R
multidatasource.groups.ds.datasources[1].driverClassName=com.mysql.jdbc.Driver
multidatasource.groups.ds.datasources[1].url=jdbc:mysql://localhost:3306/ds_0
multidatasource.groups.ds.datasources[1].username=root
multidatasource.groups.ds.datasources[1].password=123456
multidatasource.groups.ds.datasources[1].initialSize=1
multidatasource.groups.ds.datasources[1].minIdle=1
multidatasource.groups.ds.datasources[1].maxActive=20
multidatasource.groups.ds.datasources[1].maxWait=60000
multidatasource.groups.ds.datasources[1].timeBetweenEvictionRunsMillis=1000
multidatasource.groups.ds.datasources[1].minEvictableIdleTimeMillis=1000
multidatasource.groups.ds.datasources[1].maxPoolPreparedStatementPerConnectionSize=30

以上数据源可以实现读写分离，sql语句为select的查询默认走备库，其他走主库
multidatasource.groups.ds.datasources[0].rwType=W  --为主库，只能配置一个主库
multidatasource.groups.ds.datasources[1].rwType=R  --备库，可以配置多个

参照以上配置，框架会自动注册以下数据源的service：
名称为DataSource-ds的MasterSlaveDataSource的Bean
名称为DataSource-ds-MASTER的DruidDataSource的Bean
名称为DataSource-ds-SLAVE-1的DruidDataSource的Bean

以及数据库事务service：
名称为TransactionManager-ds的DataSourceTransactionManager的Bean