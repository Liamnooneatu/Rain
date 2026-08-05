# AI Assistance Log

As requested in the challenge instructions, this is a summary of how an AI coding assistant
(Claude, Anthropic) was used while building this project. 

**All code was reviewed and understood by me before submission.**

## Summary of prompts / assistance used

1. Asked for a rundown of possible tooling choices for the challenge (IDE: IntelliJ vs VS
   Code, whether Talend API Tester was needed, and database options — Postgres, MongoDB,
   DynamoDB, H2 — with trade-offs for this specific query pattern of sensor/metric/date-range
   aggregation).
2. Asked if i needed a new JDK version and other possible progam updates locally to get started (JDK, IntelliJ, Docker Desktop,
   Git).
3. Asked for assistance on docker error (engine not running error.)

4. TRUNCATE TABLE readings; will this wipe out everything from my metrics table.

## What I own / reviewed

- I chose the overall approach of doing aggregation in the application layer rather than via
  SQL `GROUP BY`, and can explain the trade-off (see README "Design decisions").
- I reviewed the validation rules (date range 1–31 days, statistic enum, required params) and
  the exception-handling structure and can walk through each of them.
- I reviewed the entity/index design and the reasoning for choosing Postgres over MongoDB/
  DynamoDB for this specific access pattern.
- I am prepared to discuss any part of the codebase, including alternative approaches I did
  not take, in the follow-up interview.
