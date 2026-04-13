# Hotel Booking Platform

Микросервисная платформа бронирования отелей на Java + Spring Boot 3.

## Сервисы

| Сервис | Порт | Описание |
|---|---|---|
| `auth-service` | 8080 | Регистрация, аутентификация, JWT |
| `booking-service` | 8081 | Отели, номера, бронирования |
| `payment-service` | 8082 | Платежи, возвраты |

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
├── common-api/           # Общие исключения и DTO
├── common-events/        # Kafka события
├── notification-service/ # В разработке
├── review-service/       # В разработке
└── docker-compose.yml
```

## API

`auth-service`: `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`, `/users/me`

`booking-service`: `/hotels`, `/room-types`, `/rooms`, `/tariffs`, `/bookings`, `/guests`

`payment-service`: `/payments`, `/refunds`

## База данных

Каждый сервис использует отдельную БД в одном PostgreSQL контейнере: `auth_db`, `booking_db`, `payment_db`.

## Kafka

`booking.created`, `booking.cancelled`, `booking.completed` → `payment-service`, `notification-service`

`payment.confirmed`, `payment.failed` → `booking-service`, `notification-service`

## TZ

Полное техническое задание и все рабочие эндпоинты можно посмотреть в hotel-booking-tz.md. Все эндпоинты реализованных сервисов работают за исключением одного (аудит в booking-service). Он в разработке