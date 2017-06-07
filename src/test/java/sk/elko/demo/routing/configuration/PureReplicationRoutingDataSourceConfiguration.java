package sk.elko.demo.routing.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import sk.elko.demo.routing.datasource.PureReplicationRoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Configuration for applications running without Spring.
 * <p>
 * This has features of {@link org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy} and support database
 * replication(master/slave | read/write) routing. This also does NOT depend on Spring framework. So you can use this
 * code with any Java applications. But you have to remember to call connection.setReadOnly(true|false) for replication
 * before executing statements. And You cannot reuse the connection for different readOnly status, you have to close and
 * get again another connection for a new jdbc statement.
 */
@Configuration
@PropertySource("classpath:test-pure.properties")
public class PureReplicationRoutingDataSourceConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource(@Qualifier("writeDataSource") DataSource writeDataSource, @Qualifier("readDataSource") DataSource readDataSource) {
        return new PureReplicationRoutingDataSource(writeDataSource, readDataSource);
    }

    @Bean(destroyMethod = "shutdown")
    public DataSource writeDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
                .setName(env.getProperty("ds.pure.write.name"))
                .setType(EmbeddedDatabaseType.valueOf(env.getProperty("ds.pure.write.type")))
                .setScriptEncoding(env.getProperty("ds.pure.write.scriptEncoding"))
                .addScript(env.getProperty("ds.pure.write.script"));
        return builder.build();
    }

    @Bean(destroyMethod = "shutdown")
    public DataSource readDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
                .setName(env.getProperty("ds.pure.read.name"))
                .setType(EmbeddedDatabaseType.valueOf(env.getProperty("ds.pure.read.type")))
                .setScriptEncoding(env.getProperty("ds.pure.read.scriptEncoding"))
                .addScript(env.getProperty("ds.pure.read.script"));
        return builder.build();
    }

}