# Kakao Message Backend

A Spring Boot API server that handles Kakao OAuth authentication and message delivery.

This backend is responsible for:
- Handling Kakao OAuth callback flow
- Managing access / refresh tokens
- Fetching Kakao friend lists
- Sending Kakao messages in batches with retry and backoff
- Preventing duplicate message sending
- Scheduling automated message sending jobs

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Web
- Spring Data JPA
- MariaDB
- Gradle

## Core Features

- Kakao OAuth token exchange and refresh
- Friend list retrieval via Kakao API
- Message sending (batch size: 5)
- Retry & backoff on failure
- Duplicate send prevention
- Scheduled message sending (bi-weekly)
- Message send history and logs
