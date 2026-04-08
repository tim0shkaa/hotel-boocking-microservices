# Hotel Booking Platform

Микросервисная платформа бронирования отелей на Java + Spring Boot 3.

## Сервисы

| Сервис | Порт | Описание |
|---|---|---|
| `auth-service` | 8080 | Регистрация, аутентификация, JWT |
| `booking-service` | 8081 | Отели, номера, бронирования |

## Стек

Java 21, Spring Boot 3, Spring Security, Spring Data JPA, PostgreSQL, Liquibase, JWT RS256, MapStruct, Lombok, Docker

## Запуск

```bash
# Сборка
./gradlew :auth-service:bootJar :booking-service:bootJar

# Запуск
docker compose up --build
```

## Структура

```
hotel-booking-microservices/
├── auth-service/
├── booking-service/
├── common-api/          # Общие исключения и DTO
├── common-events/       # Kafka события (в разработке)
├── payment-service/     # В разработке
├── notification-service/ # В разработке
├── review-service/      # В разработке
└── docker-compose.yml
```

## API

`auth-service`: `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`, `/users/me`

`booking-service`: `/hotels`, `/room-types`, `/rooms`, `/tariffs`, `/bookings`, `/guests`

## База данных

Каждый сервис использует отдельную БД в одном PostgreSQL контейнере: `auth_db`, `booking_db`.