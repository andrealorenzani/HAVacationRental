# HAVacationRental

Scala 2.13 / Finagle REST service for listings (in-memory). See README.md for API and architecture.

- Build/test: `sbt test`; end to end: `./integrationTests.sh` (needs curl, jq). Use JDK 17+.
- Handlers throw `IllegalArgumentException` synchronously on unknown paths; `RestService` converts that to 500. Tests rely on this.
- `JsonUtil.fromJson` needs a `JavaTypeable` (not `ClassTag`) so generic types such as `Map[String, String]` work.
- Keep the Apache licence header on new source files.
