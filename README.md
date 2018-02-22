# Integration Test Coverage with JaCoCo
This is an example project showing how to generate coverage reports for integration tests run against a remote server.
The server will be run in docker, and the integration tests (written in Cucumber) will hit the remote urls.

## Pre-requisites
1. [Docker](https://store.docker.com/search?type=edition&offering=community)
1. Maven
1. JDK 8


## Module Layout
### server
Simple spring boot application that manages contacts and phone numbers backed by a postgres database
<br/>**NOTE** - requires postgres db running locally if server is running in IDE.  To run, spin up a docker container just for 
postgres with command 
   ```
   docker run -p5432:5432 -i -t postgres:9.6.7
   ```

### integration-tests
Integration tests written with Cucumber BDD

### multi-module-server
Same as the server module, but broken into separate modules for web and persistence layer


## Running int test coverage
1. Build the server module and run docker image
   ```
   cd server
   mvn clean package
   docker-compose build
   docker-compose up
   ```
1. Run the integration tests (in a new terminal)
   ```
   cd integration-tests
   mvn clean test -Pone
   ```
1. Stop server (in same terminal docker-compose up was run)
   ```
    Ctrl^C
    docker-compose down
   ```
1. Examine results in integration-tests/target/coverage-report/html

## Running test test coverage with multi-module-server
1. Build the server module and run docker image
   ```
   cd multi-module-server
   mvn clean install -f modules/pom.xml
   mvn clean package
   docker-compose build
   docker-compose up
   ```
1. Run the integration tests (in a new terminal)
   ```
   cd integration-tests
   mvn clean test -Pmulti
   ```
1. Stop server (in same terminal docker-compose up was run)
   ```
    Ctrl^C
    docker-compose down
   ```
1. Examine results in integration-tests/target/coverage-report/html
