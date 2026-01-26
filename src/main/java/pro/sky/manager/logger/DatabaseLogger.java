package pro.sky.manager.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLogger {

    private static final Logger log = LoggerFactory.getLogger(DatabaseLogger.class);

    @Value("${application.recommendations-db.url}")
    private String dbUrl;

    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void logDatabaseConnection() {
        System.out.println("Вызван метод logDatabaseConnection");
        String dbName = extractDatabaseName(dbUrl);
        System.out.println("Попытка подключения к базе данных: " + dbName);
        log.info("База данных '{}' по пути '{}' успешно подключена.", dbName, dbUrl);
    }

    private String extractDatabaseName(String url) {
        if (url != null && url.contains(":")) {
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex + 1 < url.length()) {
                return url.substring(lastSlashIndex + 1);
            }
        }
        return "неизвестная";
    }
}