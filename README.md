# JHipster Microservice Example

A simple project to demonstrate a microservice architecture created with JHipster.

To run this app, you'll need to install Java 8, [Node.js](https://nodejs.org/),  [Docker](https://docs.docker.com/engine/installation/) and [Docker Compose](https://docs.docker.com/compose/install/).

1. Make sure Docker is running.

2. Go to orderbook directory and build a docker image of `orderbook` microservice

    ```
    cd orderbook/
    ./gradlew -Pprod bootRepackage buildDocker
    cd ../
    ```

3. Go to gateway directory and build a docker image of `gateway` application

    ```
    cd gateway/
    ./gradlew -Pprod bootRepackage buildDocker
    ```

3. Go to docker directory and start Docker containers

    ```bash
    docker-compose up
    ```

    TIP: Add `-d` at the end of the command above if you want to run all containers detachedly.

4. Go to `docker` directory and stop Docker containers

    ```bash
    docker-compose stop
    ```
