# EOHA Backend — implementation notes

The active implementation is Node.js 22+ / Express 5. It serves canned sample
records with no database. Routes call service functions; shared helpers provide
validation, pagination and response envelopes. See README.md and MIGRATION.md.

When a real data source is chosen, replace the sample arrays in the services with
queries. Preserve the documented API fields and add database integration tests,
connection management, migrations and indexes appropriate to that data source.
The API regression suite and CI are already present.
