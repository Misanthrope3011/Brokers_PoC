# This project is a PoC of helper tool to compare message brokers. 

## Tech Stack

- ### Java 17
- ### Spring Cloud Streams 4.2.0
- ### PostgreSQL

## Metric tools
- ### Prometheus - setting initial password to monitor:
- ### Pulsar manager - setting initial password to monitor:
- ### Metabase - UI tool for collecting metrics data

### build
./gradlew clean build

Encryption with jasypt:
Wrap your property with encrypt property like this: property: ENCRYPT(property)
./gradlew encryptProperties --password=your-hash-password
As a result you should see properties in format ENC(encrypted-property)

### run
./gradlew bootRun -Djasypt.encryptor.password=your-password

