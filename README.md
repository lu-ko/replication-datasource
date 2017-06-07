# Java (Spring & Non Spring) replication-datasource

**NOTE: This project is just forked from** [kwon37xi's replication-datasource](https://github.com/kwon37xi/replication-datasource)

When you need database replication, you have to route read/write connections to appropriate databases (e.g. master for writes, and slave for reads).

There are multiple ways of implementing replication datasources in Java environment:

* Java way for applications with [Spring framework](http://spring.io/)
* Java way for general applications without [Spring framework](http://spring.io/)
* Other - e.g. Database Proxy server like [MySQL Proxy](http://dev.mysql.com/doc/mysql-proxy/en/) or [MaxScale](https://github.com/mariadb-corporation/MaxScale) and [MySql Replication JDBC Driver](http://dev.mysql.com/doc/connector-j/en/connector-j-master-slave-replication-connection.html)).

Refer to [DatabaseConfiguration](https://github.com/lu-ko/replication-datasource/blob/master/src/main/java/sk/elko/demo/routing/configuration/DatabaseConfiguration.java) for main entry point to application code.

## Java way for applications with Spring Framework

You can make replication data source with only Spring framework's two basic classes:
 
* [LazyConnectionDataSourceProxy](https://github.com/spring-projects/spring-framework/blob/master/spring-jdbc/src/main/java/org/springframework/jdbc/datasource/LazyConnectionDataSourceProxy.java) - see usage when instantiating data source in [SpringReplicationRoutingDataSourceConfiguration](https://github.com/lu-ko/replication-datasource/blob/master/src/test/java/sk/elko/demo/routing/configuration/SpringReplicationRoutingDataSourceConfiguration.java)
* [AbstractRoutingDataSource](https://github.com/spring-projects/spring-framework/blob/master/spring-jdbc/src/main/java/org/springframework/jdbc/datasource/lookup/AbstractRoutingDataSource.java) - see implementation in [SpringReplicationRoutingDataSource](https://github.com/lu-ko/replication-datasource/blob/master/src/main/java/sk/elko/demo/routing/datasource/SpringReplicationRoutingDataSource.java)

This works very nicely with Spring's [TransactionSynchronizationManager](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/support/TransactionSynchronizationManager.html). If you use Spring framework for your application, this is enough for your database replication. You just need to set `@Transactional(readOnly = true|false)`.

```java
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "read" : "write";
    }
}

@Bean
public DataSource writeDataSource() {
    ...
}

@Bean
public DataSource readDataSource() {
    ...
}

@Bean
public DataSource routingDataSource(
    @Qualifier("writeDataSource") DataSource writeDataSource,
    @Qualifier("readDataSource") DataSource readDataSource) {
    ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

    Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
    dataSourceMap.put("write", writeDataSource);
    dataSourceMap.put("read", readDataSource);
    routingDataSource.setTargetDataSources(dataSourceMap);
    routingDataSource.setDefaultTargetDataSource(writeDataSource);

    return routingDataSource;
}

@Bean
public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
    return new LazyConnectionDataSourceProxy(routingDataSource);
}

// in Service class.

// working with read database
@Transactional(readOnly = true)
public Object readQuery() {
    ....
}

// working with write database
@Transactional(readOnly = false)
public void writeExection() {
    ....
}
```

Please refer to tests for verification.


## Java way for general applications without

If you are not using Spring framework or you don't want to depend on it, then you can use prepared data source proxy [PureReplicationRoutingDataSource](https://github.com/lu-ko/replication-datasource/blob/master/src/main/java/sk/elko/demo/routing/datasource/PureReplicationRoutingDataSource.java) made from [LazyConnectionDataSourceProxy](https://github.com/spring-projects/spring-framework/blob/master/spring-jdbc/src/main/java/org/springframework/jdbc/datasource/LazyConnectionDataSourceProxy.java).

Please see [PureReplicationRoutingDataSourceConfiguration](https://github.com/lu-ko/replication-datasource/blob/master/src/test/java/sk/elko/demo/routing/configuration/PureReplicationRoutingDataSourceConfiguration.java) for real usage of this proxy. It has features of Spring's LazyConnectionDataSourceProxy and it supports database replication (master/slave | read/write) routing.

This solution does not depend on Spring framework, and so you can use this code with any Java applications. But you have to remember to call `connection.setReadOnly(true|false)` for replication before executing statements. And You cannot reuse the connection for different readOnly status, you have to close and get again another connection for a new jdbc statement.

```java
@Bean
public DataSource writeDataSource() {
    ...
}

@Bean
public DataSource readDataSource() {
    ...
}

@Bean
public DataSource dataSource(DataSource writeDataSource, DataSource readDataSource) {
    return new PureReplicationRoutingDataSource(writeDataSource, readDataSource);
}
```

when you use with spring framework
```java
// in Service class.

// Spring's @Transaction AOP automatically call connection.setReadOnly(true|false).
// But Spring prior to 4.1.x JPA does not call setReadOnly method.
 // In this situation you'd better use first way (LazyConnectionDataSourceProxy + AbstractRoutingDataSource).
// working with read database
@Transactional(readOnly = true)
public Object readQuery() {
    ....
}

// working with write database
@Transactional(readOnly = false)
public void writeExection() {
    ....
}
```

when you use without Spring framwork
```java
Connection readConn = dataSource.getConnection();
readConn.setReadOnly(true);

// ... working with readConn...

readConn.close();

Connection writeConn = dataSource.getConnection();
writeConn.setReadOnly(false);

// ... working with writeConn...

writeConn.close();
```

Please refer to tests for verification.