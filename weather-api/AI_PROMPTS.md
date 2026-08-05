# AI Assistance Log

As requested in the challenge instructions, this is a summary of how an AI coding assistant
(Claude, Anthropic) was used while building this project. All code was reviewed, understood,
and adjusted by me before submission.

## Summary of prompts / assistance used

1. Asked for a rundown of possible tooling choices for the challenge (IDE: IntelliJ vs VS
   Code, whether Talend API Tester was needed, and database options — Postgres, MongoDB,
   DynamoDB, H2 — with trade-offs for this specific query pattern of sensor/metric/date-range
   aggregation).
2. Asked what needed to be installed locally to get started (JDK, IntelliJ, Docker Desktop,
   Git).
3. Asked for the project to be scaffolded: a Spring Boot REST API with the two endpoints
   described in the brief (ingest a reading, query with aggregation), Postgres via Docker
   Compose plus an H2 profile for a no-Docker option, input validation, a global exception
   handler, and unit + integration tests.

## What I own / reviewed

- I chose the overall approach of doing aggregation in the application layer rather than via
  SQL `GROUP BY`, and can explain the trade-off (see README "Design decisions").
- I reviewed the validation rules (date range 1–31 days, statistic enum, required params) and
  the exception-handling structure and can walk through each of them.
- I reviewed the entity/index design and the reasoning for choosing Postgres over MongoDB/
  DynamoDB for this specific access pattern.
- I am prepared to discuss any part of the codebase, including alternative approaches I did
  not take, in the follow-up interview.
