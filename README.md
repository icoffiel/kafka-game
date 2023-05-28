# Kafka Games

Kafka Games is an application that allows Gaming Systems and Games to be added to a collection.

## Overview

![Architecture](.docs/images/architecture.svg)

The application uses The following microservices to add the expected entites to the system:
- [Systems](./systems): REST Interface providing CRUD operations. DB is monitored by Kafka Connect.
- [Games](./games): REST Interface providing CRUD operations. DB is monitored by Kafka Connect.
- [Notifications](./notifications): REST Interface providing CRUD operations. Kafka Stream monitors for new games and provides a new message with any email addresses that have opted to be notified.

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
```shell
# run from systems folder
cd systems

./gradlew bootRun
```

```shell
# run from games folder
cd games

./gradlew bootRun
```

```shell
# run from notifications folder
cd notifications

./gradlew bootRun
```

Add the connectors:
```shell
# run from the root folder
./gradlew addConnectors
```

> Note: Kafka Connect can take a minute or two to start so if this fails try waiting a minute before running.
> This also only needs to be run once. If ran multiple times it will produce a 409 status code


Once everything has started then you will be able to add systems, games and notifications.

The following features are currently present:
- Adding a game with an id of a system that doesn't exist will cause an error
- Adding a notification with a system id, and then adding a game with that system id will cause messages to be output 
in the notifications logs that has the system id as the key and the email address as the value.


## Issues
- Topic naming
  - Using dot notation (`systems.sql.<table>` for instance) doesn't play nice when being ingested as a sink to a DB due to the naming
  - Would also be nice to instead of only having a prefix, to also have a postfix (`systems.<table>.sql` would then be possible for instance)
- Database lookup in stream application
  - This can cause latency and also couples us to the database (what if the database goes down?)
  - Enrich the new games topic with the email address and have another topic that ensures duplicates aren't sent?
  (Joining streams causes emissions from both streams to send a new message, while we only want new games to trigger new messages)

## To Do

- Updates
- Stream processing apps (dashboard for games and consoles, notification system)
- Tests
- Hook everything into the root gradle file for easier startups (single command to start everything)
- Ephemeral ports for apps and use a gateway for apps?