# Task Manager

Веб-приложение для управления персональными задачами. Каждый пользователь видит и изменяет только свои задачи.

## Стек

- Java 21
- Spring Boot 3.4 (Web, Data JPA, Security, Validation)
- Thymeleaf
- Hibernate / PostgreSQL (Docker) / H2 (dev, test)
- BCrypt, session-based authentication
- JUnit 5, Mockito, AssertJ, Spring Security Test
- Docker, Docker Compose

## Возможности

- Регистрация и вход
- CRUD задач (title, description, status, priority, deadline)
- Смена статуса из списка
- Изоляция данных по пользователю
- Централизованная обработка ошибок
- Логирование операций (без паролей)

## Структура проекта

```
src/main/java/com/example/core3/
├── config/          # Security, свойства приложения
├── controller/      # MVC-контроллеры
├── dto/             # Формы и DTO
├── entity/          # JPA-сущности User, Task
├── exception/       # Исключения и @ControllerAdvice
├── mapper/          # Маппинг entity ↔ form
├── repository/      # Spring Data JPA
├── security/        # UserDetailsService
└── service/         # Бизнес-логика (@Transactional)
src/main/resources/
├── templates/       # Thymeleaf
├── static/css/      # Стили
└── application*.yml # Профили dev, test, docker
```

## Запуск локально

Требования: JDK 21, Maven 3.9+

```bash
./mvnw spring-boot:run
```

Профиль `dev` по умолчанию (встроенная H2). Приложение: http://localhost:8080

H2 Console (только dev): http://localhost:8080/h2-console

## Запуск через Docker

1. Скопируйте переменные окружения:

```bash
cp .env.example .env
```

2. Задайте `DB_USERNAME` и `DB_PASSWORD` в `.env`.

3. Запуск:

```bash
docker compose up --build
```

Приложение: http://localhost:8080 (профиль `docker`, PostgreSQL).

## Тесты

```bash
./mvnw test
```

- Unit: `TaskServiceTest`, `UserServiceTest`
- JPA: `TaskRepositoryTest` (H2)
- MVC: `TaskControllerTest`
- Security: `SecurityIntegrationTest`

## Переменные окружения (Docker)

| Переменная   | Описание              | Пример        |
|-------------|------------------------|---------------|
| DB_NAME     | Имя БД                 | taskmanager   |
| DB_USERNAME | Пользователь PostgreSQL| taskuser      |
| DB_PASSWORD | Пароль БД              | (секрет)      |
| DB_PORT     | Порт PostgreSQL        | 5432          |
| APP_PORT    | Порт приложения        | 8080          |

## Профили Spring

| Профиль | База данных | ddl-auto |
|---------|-------------|----------|
| dev     | H2 in-memory| update   |
| test    | H2 in-memory| create-drop |
| docker  | PostgreSQL  | update   |

## Тестовый пользователь

Создайте через форму регистрации: http://localhost:8080/register

Либо зарегистрируйтесь и войдите с выбранными учётными данными.
