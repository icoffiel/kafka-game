# Kafka Games

Kafka Games is an application that allows Gaming Systems and Games to be added to a collection.

## Overview

![Architecture](.docs/images/architecture.svg)

The application uses The following microservices to add the expected entites to the system:
- Systems: To manage Gaming Systems
- Games: To manage games

Each of the microservices databases are then connected to Kafka via Kafka connect to produce messages whenever systems
are added, updated or deleted

## Prerequisites

- [Docker Compose](https://docs.docker.com/compose/)

## Running Locally

Start the external systems (DB, etc):
```shell
# run from the .docker folder
cd .docker

# run the systems in the background
docker-compose up -d
```

Start up the applications:

## To Do

- Games application
- Validation on endpoints
- Setup Kafka & Kafka Connect
- Stream processing apps (dashboard for games and consoles?)
- Tests
- Hook everything into the root gradle file for easier startups (single command to start everything)