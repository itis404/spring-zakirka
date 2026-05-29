# Laundry Locator — семестровая работа ОРИС

Веб-приложение для поиска прачечных, бронирования стиральных машин и отзывов.

## Стек

- Spring Boot 3, Spring MVC, Spring Security, Spring Data JPA
- PostgreSQL, Redis
- Thymeleaf, Bootstrap 5, JavaScript (AJAX)
- Docker Compose
- OpenAPI (Swagger UI: `/swagger-ui.html`)
- OkHttp → Telegram Bot API

## Запуск (Docker)

```bash
cp .env
mvn clean package -DskipTests
docker compose up --build
```

**Telegram:** в `.env` токен бота; владелец прачечной указывает свой Telegram ID в панели `/owner/dashboard`. Нужно нажать Start у бота.

Открыть: http://localhost:8080

## Тестовые учётные записи 

| Email           | Пароль   | Роль  |
|-----------------|----------|-------|
| admin@gmail.com | admin123 | ADMIN |
| owner@gmail.com | owner123 | OWNER |
| zakir@gmail.com | 123456   | USER  |



Новые пользователи регистрируются только как `USER`. Роль `OWNER` назначает администратор в `/admin/users`.

## Postman

Импорт `Laundry_API_Postman_Collection.json`.

## Соответствие ТЗ

- 6 JPA-сущностей, O2M и M2M (избранные прачечные)
- CRUD прачечен (панель владельца)
- `@Query`, CriteriaBuilder, subselect
- Redis `@Cacheable`
- REST + OpenAPI + Postman
- Spring Security, валидация, PRG, кастомные ошибки
- AJAX-отзывы, Telegram через OkHttp
