# Установка и настройка

## Локальная разработка
### 1. Настройка IDE
- IntelliJ IDEA с плагинами Lombok, Spring Boot
- Настройка code style согласно project rules

### 2. Конфигурация базы данных
- PostgreSQL для разработки
- Настройки в application-dev.properties

## Продуктивная среда
### Docker развертывание
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
