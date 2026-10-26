# HAVacationRental

A small in-memory REST service that manages vacation-rental listings. It was written as a coding exercise for HomeAway and has since been upgraded to current libraries.

- Scala 2.13 (Finagle is not published for Scala 3)
- [Finagle](https://twitter.github.io/finagle/) 24.2 as HTTP server
- Jackson 2.22 (+ `jackson-module-scala`) for JSON
- ScalaTest 3.2 for tests
- sbt 1.12, JDK 17 or newer (developed and tested on JDK 21)

## Usage

```bash
sbt run     # starts the server on port 8080
sbt test    # unit tests
./integrationTests.sh
```

`integrationTests.sh` starts the server with `sbt run`, exercises every endpoint with `curl`, checks the answers with `jq` and stops the server. Set `START_SERVER=no` to test an already running server (`BASE_URL` overrides `http://localhost:8080`).

## REST API

| Method | Path | Body | Reply |
|--------|------|------|-------|
| `POST` | `/new` | listing JSON **without** `id` | `{"newId": "<uuid>"}` |
| `PUT` | `/update` | listing JSON **with** an existing `id` | empty 200 |
| `GET` | `/listing/<id>` | | the listing JSON, 400 if not found |
| `GET` | `/listings` | | JSON array of all listings |
| `GET` | `/size` | | `{"size": N}` |
| `DELETE` | `/del/<id>` | | the deleted listing, 400 if not found |
| `DELETE` | `/delall` | | JSON array of the deleted listings (testing helper, dangerous!) |

A listing looks like [example.json](example.json) (`%NEWID%` is a placeholder for the id: `null` for a new listing):

```json
{ "listing": { "id": null, "contact": {...}, "address": {...}, "location": {...} } }
```

`id`, `contact`, `location` and the optional parts of `address` can be omitted. Unknown fields are ignored. Invalid requests (wrong path, a `POST` with an id, a `PUT` with an unknown id, ...) are answered with a 500 error, handled by `RestService`.

```bash
curl http://localhost:8080/size
curl --request POST --data @newlisting.json http://localhost:8080/new
curl --request DELETE http://localhost:8080/del/<id>
```

## Architecture

`VacationRentals` (the `App`) serves a `RestService` with Finagle. `RestService` dispatches by HTTP method to one of four stateless `RequestHandler`s (`Get`, `Post`, `Put`, `Del`), which match on the path, call the `DataStore` and serialise the answer with `JsonUtil`. `RestService` also turns uncaught failures into 500 replies.

```
src/main/scala/name/lorenzani/andrea/homeaway
├── VacationRentals.scala         entry point, port 8080
├── services/                     RestService, RequestHandler (+ factories) and the four handlers
├── datastore/                    DataStore trait, domain case classes, SimpleMapStore
└── json/JsonUtil.scala           Jackson wrapper (toJson / fromJson / toMap)
```

State lives only in the `DataStore`; the only implementation is `SimpleMapStore`, backed by a `ConcurrentHashMap`. Data is lost on restart.

## Design notes

- **Concurrency**: everything is immutable and stateless except the store, which is a concurrent map. `delAll` is the exception: it reads then clears, so a write in between is deleted but not returned. It is a testing helper and would not be shipped.
- **Reuse**: handlers depend on the abstract `DataStore`, and the service on abstract `RequestHandler`s. Swapping in a Redis or SQL store means changing the factory method in `DataStore`/`RequestHandler`. Storing other data only needs different case classes.
- **Performance and scalability**: Finagle does the heavy lifting; the store is the bottleneck. Instances are independent, so scaling out requires an external or partitioned store.

## Notes on the upgrade

Previously Scala 2.11, Finagle 6.45, Jackson 2.8, ScalaTest 3.0 and sbt 0.13. Changes besides versions: Scala 2.13 collection converters, Jackson's `JsonMapper` builder with `ClassTagExtensions` instead of the removed `ScalaObjectMapper`, ScalaTest 3.2 `AnyFlatSpec`, simpler `Future.value`-based handlers, a more robust integration script, and forked `run`/`test` JVMs.

## License

Apache License 2.0, see [LICENSE](LICENSE).
