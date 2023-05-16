# Systems Application

This application allows for saving of Gaming Systems into a database.

Spring is set up to automatically update the database schema if changes are detected by defining the following:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

In a production system you should ideally use something like Liquibase or Flyway over allowing the application to
make automated database changes.