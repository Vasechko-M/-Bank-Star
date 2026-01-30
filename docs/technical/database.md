# База данных

## Схема
### Таблица recommendations
```sql
CREATE TABLE recommendations (
  id BIGINT PRIMARY KEY,
  product_code VARCHAR(50),
  product_name VARCHAR(100),
  min_score INTEGER,
  max_score INTEGER,
  description TEXT
);
