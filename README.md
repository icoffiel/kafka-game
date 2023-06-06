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

Add the connectors:
```shell
# run from the root folder
./gradlew addConnectors
```

> Note: Kafka Connect can take a minute or two to start so if this fails try waiting a minute before running.
> This also only needs to be run once. If ran multiple times it will produce a 409 status code
 
> Note: You will need to add at least 1 system and 1 game before launching notifications to ensure that the 
> necessary topics are created. See the issues below for more information.


```shell
# run from notifications folder
cd notifications

./gradlew bootRun
```

Once everything has started then you will be able to add systems, games and notifications.

The following features are currently present:
- Adding a game with an id of a system that doesn't exist will cause an error
- Adding a notification with a system id, and then adding a game with that system id will cause messages to be output 
in the notifications logs that has the system id as the key and the email address as the value.


## Issues
- Topic naming
  - Using dot notation (`systems.sql.<table>` for instance) doesn't play nice when being ingested as a sink to a DB due to the naming
  - Would also be nice to instead of only having a prefix, to also have a postfix (`systems.<table>.sql` would then be possible for instance)
- Topic creation
  - The JDBC source connectors currently create the topic only when it detects that he DB has been updated for the first time
  - This should be moved to either prior to the pp starting or when the app starts up
- Startup order
  - Currently, the system and game apps create the DB on startup. The DB is required the JDBC source connectors. Ideally all apps should be able to startup in whatever order they choose.
  - Each app should be responsible for starting the things it needs in the following order:
    - SQL
    - Connectors/Application
  - The above order would ensure that the DB is there before any connectors, and that the DB is there for the app to use
  - There is some natural dependency though (games relies on the systems stream being there to begin with)

## To Do

- Updates
- Tests