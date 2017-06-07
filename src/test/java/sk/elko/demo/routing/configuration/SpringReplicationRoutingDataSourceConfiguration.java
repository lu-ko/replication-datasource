package sk.elko.demo.routing.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import sk.elko.demo.routing.datasource.SpringReplicationRoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for applications running with Spring.
 *
 * You can make replication data source with only Spring framework's two basic classes:
 * <ul>
 * <li>{@link org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy}</li>
 * <li>{@link org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource}</li>
 * </ul>
 *
 * This solution works very nicely with Spring's TransactionSynchronizationManager. If you use Spring framework for your
 * application, this is enough for your database replication. You just need to set @Transactional(readOnly = true|false).
 */
@Configuration
@PropertySource("classpath:test-spring.properties")
public class SpringReplicationRoutingDataSourceConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource, @Qualifier("readDataSource") DataSource readDataSource) {
        SpringReplicationRoutingDataSource routingDataSource = new SpringReplicationRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
        dataSourceMap.put("write", writeDataSource);
        dataSourceMap.put("read", readDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    @Bean(destroyMethod = "shutdown")
    public DataSource writeDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
                .setName(env.getProperty("ds.spring.write.name"))
                .setType(EmbeddedDatabaseType.valueOf(env.getProperty("ds.spring.write.type")))
                .setScriptEncoding(env.getProperty("ds.spring.write.scriptEncoding"))
                .addScript(env.getProperty("ds.spring.write.script"));
        return builder.build();
    }

    @Bean(destroyMethod = "shutdown")
    public DataSource readDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
                .setName(env.getProperty("ds.spring.read.name"))
                .setType(EmbeddedDatabaseType.valueOf(env.getProperty("ds.spring.read.type")))
                .setScriptEncoding(env.getProperty("ds.spring.read.scriptEncoding"))
                .addScript(env.getProperty("ds.spring.read.script"));
        return builder.build();
    }
}
