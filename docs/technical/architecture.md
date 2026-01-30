# Архитектура

## Компоненты
- Controller → Service → Repository → Database
- REST API с Swagger документацией
- In-memory H2 для разработки

## Поток данных
Клиент → REST API → Сервисный слой → База данных
                      ↓
                Логика рекомендаций
