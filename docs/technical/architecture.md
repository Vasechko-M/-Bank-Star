# Архитектура системы

## Общая схема
[Клиент] → [REST API] → [Сервисный слой] → [Репозиторий] → [H2 DB]
↓
[Swagger UI]

## Компоненты
### 1. Controller Layer
- RecommendationsController - обработка HTTP запросов
- ResponseEntity для стандартных ответов

### 2. Service Layer
- RecommendationsService - бизнес-логика рекомендаций
- Расчет скоринга и фильтрация

### 3. Repository Layer
- RecommendationsRepository - доступ к данным
- Spring Data JPA
