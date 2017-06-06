package kr.pe.kwonnam.replicationdatasource.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "kr.pe.kwonnam.replicationdatasource.jpa.repository")
@ComponentScan(basePackages = "kr.pe.kwonnam.replicationdatasource.jpa.service")
public class ReplicationDataSourceApplicationConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(env.getProperty("hibernate.generate_ddl", Boolean.class));
        vendorAdapter.setShowSql(env.getProperty("hibernate.show_sql", Boolean.class));
        vendorAdapter.setDatabasePlatform(env.getProperty("hibernate.dialect"));

        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setJpaVendorAdapter(vendorAdapter);
        emfb.setPackagesToScan("kr.pe.kwonnam.replicationdatasource.jpa.entity");
        emfb.setDataSource(dataSource);

        emfb.getJpaPropertyMap().put("hibernate.connection.charSet", env.getProperty("hibernate.connection.charSet"));
        emfb.getJpaPropertyMap().put("hibernate.connection.characterEncoding", env.getProperty("hibernate.connection.characterEncoding"));
        emfb.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        emfb.getJpaPropertyMap().put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));

        return emfb;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
