# API документация

## Рекомендации
### GET /api/v1/recommendations
Получить рекомендации по продуктам

**Параметры:**
- clientId (обязательный)
- riskProfile (опционально)

**Ответ:**
```json
{
  "recommendations": [
    {
      "productId": "INVEST_500",
      "name": "Invest 500",
      "score": 85
    }
  ]
}
