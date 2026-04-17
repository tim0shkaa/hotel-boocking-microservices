# Hotel Booking Platform

Микросервисная платформа бронирования отелей на Java + Spring Boot 3.

## Сервисы

| Сервис | Порт | Описание |
|---|---|---|
| `auth-service` | 8080 | Регистрация, аутентификация, JWT |
| `booking-service` | 8081 | Отели, номера, бронирования |
| `payment-service` | 8082 | Платежи, возвраты |
| `notification-service` | 8083 | Уведомления по Kafka событиям |
| `review-service` | 8084 | Отзывы и рейтинги |

## Стек

Java 21, Spring Boot 3, Spring Security, Spring Data JPA, PostgreSQL, Liquibase, Kafka, JWT RS256, MapStruct, Lombok, Docker

## Запуск

```bash
./gradlew build
docker compose up --build
```

## Структура

```
hotel-booking-microservices/
├── auth-service/
├── booking-service/
├── payment-service/
├── notification-service/
├── review-service/
├── common-api/           # Общие исключения и DTO
├── common-events/        # Kafka события
└── docker-compose.yml
```

## API

`auth-service`: `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`, `/users/me`

`booking-service`: `/hotels`, `/room-types`, `/rooms`, `/tariffs`, `/bookings`, `/guests`

`payment-service`: `/payments`, `/refunds`

`review-service`: `/reviews`, `/hotels/{id}/reviews`, `/room-types/{id}/reviews`, `/hotels/{id}/rating`

## База данных

Каждый сервис использует отдельную БД в одном PostgreSQL контейнере: `auth_db`, `booking_db`, `payment_db`, `notification_db`, `review_db`.

## Kafka

`booking.created`, `booking.cancelled`, `booking.completed` → `payment-service`, `notification-service`, `review-service`

`payment.confirmed`, `payment.failed` → `booking-service`, `notification-service`

`review.created` → `booking-service`

Все топики с DLQ и exponential backoff. Идемпотентность через таблицу `processed_events` в каждом сервисе.

## ТЗ

Полное техническое задание в `hotel-booking-tz.md`. Все эндпоинты реализованных сервисов работают за исключением аудита в `booking-service` — в разработке.