# Spring to Express migration

The Java baseline is Git commit 98027d4. Java/Maven sources are removed from the
active tree but remain recoverable in Git history. The local checkpoint branch
codex/spring-checkpoint-20261013 points to that baseline.

| Spring component | Express replacement |
| --- | --- |
| Controllers | src/routes/*.js |
| Services | src/services/*.js |
| DTO records | Plain objects with the same response fields |
| GlobalExceptionHandler | Final error middleware in src/app.js |
| WebConfig | src/cors.js |
| application.properties | src/config.js and environment variables |
| Static resources | public/ served by express.static |
| Maven | package.json and package-lock.json |

The health endpoint deliberately remains plain text OK. Resource paths, sample
records, filters, pagination envelope and error messages are retained. Empty
page/size parameters use the Spring defaults. Integer binding preserves the
signed 32-bit range and rejects overflow instead of returning null metadata.
JavaScript pagination also avoids the Java integer multiplication overflow.
The existing Express migration adds 127.0.0.1 alongside localhost origins.
Preflight headers are reflected to match Spring's wildcard-header behavior.

Configuration now validates PORT and explicitly loads an optional .env file.
The dashboard, README and implementation notes describe Node/Express.
CI installs from the lockfile and runs the contract tests on Node 22.

The backend still contains only sample data. Authentication, database access and
prediction services should be separate changes with their own requirements.
Historical SUMMARY.md, Summary/ and Skills.md documents may describe previous
architectures; README.md and the current source define the active implementation.
