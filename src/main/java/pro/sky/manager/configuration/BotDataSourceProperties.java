package pro.sky.manager.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Для хранения настроек подключения к базе данных бота из конфигурационного файла
 */

@ConfigurationProperties(prefix = "spring.datasource.bot")
public class BotDataSourceProperties {
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
