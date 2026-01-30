# Автоматические тесты

## Структура тестов
src/test/java
├── controller/RecommendationsControllerTest
├── service/RecommendationsServiceTest
└── repository/RecommendationsRepositoryTest


## Запуск тестов
```bash
mvn test          # все тесты
mvn test -Dtest=RecommendationsServiceTest  # конкретный класс
