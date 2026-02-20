package pro.sky.manager.configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * Конфигурация Spring для отдельного подключения и настройки базы данных, связанной с ботом
 */

@Configuration
@EnableJpaRepositories(
        basePackages = "pro.sky.manager.botrepository",
        entityManagerFactoryRef = "botEntityManagerFactory",
        transactionManagerRef = "botTransactionManager"
)
@EnableConfigurationProperties(BotDataSourceProperties.class)
public class BotDataSourceConfiguration {

    @Bean(name = "botDataSourceProperties")
    @Qualifier("bot")
    @ConfigurationProperties("spring.datasource.bot")
    public DataSourceProperties botDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "botDataSource")
    public DataSource botDataSource(@Qualifier("botDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "botEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean botEntityManagerFactory(
            @Qualifier("botDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("pro.sky.manager.model.bot");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "botTransactionManager")
    public PlatformTransactionManager botTransactionManager(
            @Qualifier("botEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean
    public SpringLiquibase botLiquibase(@Qualifier("botDataSource") DataSource dataSource,
                                        @Value("${bot.liquibase.change-log}") String changeLog) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(true);
        return liquibase;
    }
}