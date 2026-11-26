# Machine Coding a Cache (Java)

A beginner-friendly cache project developed step by step using **Java
only**. No Maven or external libraries are required.

> Learning implementation: the database is simulated with a Java map.
> This is not a production-ready distributed cache.

## Contents

-   [What is a cache?](#what-is-a-cache)
-   [Run the project](#run-the-project)
-   [Class diagram](#class-diagram)
-   [Development stages](#development-stages)
-   [Key concepts](#key-concepts)
-   [Testing checklist](#testing-checklist)
-   [Code review checklist](#code-review-checklist)
-   [Limitations and next
    improvements](#limitations-and-next-improvements)

## What is a cache?

A cache is fast, temporary storage that keeps frequently requested data
in memory.

Example: the application asks for key `A`. If `A` is not in the cache,
the cache fetches it from the database, stores it, and returns it. The
next request can return the value directly from memory.

## Run the project

### Requirements

-   Java JDK
-   Terminal or command prompt

Check installation:

``` bash
java -version
javac -version
```

### Files

``` text
cache-project/
├── Cache.java
└── Main.java
```

Compile and run:

``` bash
javac *.java
java Main
```

The simulated database contains `A = Apple` and `B = Banana`.

## Class diagram

``` mermaid
classDiagram
    class Main {
        +main(String[] args) void
    }

    class Cache {
        -WritePolicy writePolicy
        -int capacity
        -long ttl
        -Map cache
        -Map database
        -Map loading
        -Map versions
        -ExecutorService[] workers
        -ExecutorService dbWorker
        -AtomicLong dbRequests
        +Cache(int capacity, WritePolicy policy)
        +Cache(int capacity, WritePolicy policy, long ttl)
        +getAsync(String key) CompletableFuture
        +setAsync(String key, String value) CompletableFuture
        +hotLoad(String... keys) void
        +databaseRequestCount() long
        +close() void
        -cachedValue(String key) String
        -putCache(String key, String value) void
        -workerIndex(String key) int
        -version(String key) AtomicLong
        -log(String event, String key) void
    }

    class Entry {
        +String value
        +long expiry
        +Entry(String value, long ttl)
    }

    class WritePolicy {
        <<enumeration>>
        WRITE_THROUGH
        WRITE_BACK
    }

    Main --> Cache : creates and calls
    Cache *-- Entry : stores
    Cache --> WritePolicy : selects update behavior
```

### Diagram explained

-   `Main` is the demo program. It creates the cache and calls its
    methods.
-   `Cache` manages cache data, the simulated database, asynchronous
    work, and logs.
-   `Entry` is an internal class containing the cached value and its
    expiration time.
-   `WritePolicy` is an enum that selects how updates are persisted.

## Development stages

The project was built in **four stages**. Each stage adds a new
capability to the previous one.

### Stage 1 --- Basic cache

**Goal:** Make a working key-value cache.

Added: - Create, read, update, and delete (CRUD). - Store values in a
`HashMap`. - Keep cache behavior inside a class.

Example:

``` java
Map<String, String> cache = new HashMap<>();
cache.put("A", "Apple");
System.out.println(cache.get("A"));
```

Output:

``` text
Apple
```

**Why:** Start with correct basic behavior before introducing more
complicated engineering concerns.

### Stage 2 --- Smart cache

**Goal:** Manage memory and remove entries when necessary.

Added: - TTL (time-to-live). - Expiration checks. - Maximum capacity. -
LRU (Least Recently Used) eviction.

Each `Entry` stores a value and an expiry timestamp. When reading an
entry, the cache checks whether its expiry time has passed.

The cache uses an access-order `LinkedHashMap`:

``` java
new LinkedHashMap<>(16, 0.75f, true);
```

The `true` means accesses update the order. When the cache is full, the
first entry in that order is the least recently used and is evicted.

Example with capacity 2:

``` text
Put A
Put B
Get A
Put C
```

`B` is removed because `A` was accessed more recently.

**Why:** Cache memory is limited. TTL removes old data, and LRU makes
room for new data.

### Stage 3 --- Concurrent cache

**Goal:** Handle overlapping requests without unnecessary duplicate
database work.

Added: - `CompletableFuture` for asynchronous results. - Executor
services for running tasks. - Same-key worker assignment. - Request
collapsing. - Version tracking to protect newer writes from old loads.

#### Asynchronous work

A `CompletableFuture` represents a result that may become available
later:

``` java
cache.getAsync("A")
     .thenAccept(System.out::println)
     .join();
```

#### Request collapsing

If several callers request the same missing key, they share one
in-progress database future:

``` text
Caller 1 ─┐
Caller 2 ─┼──> one database request for A
Caller 3 ─┘       result shared with all
```

The `loading` map keeps the future for a key while it is being loaded.

#### Same-key ordering

A key is assigned to a single-thread worker based on its hash.
Therefore, operations for the same key are queued on the same worker and
run sequentially. Different keys can use different workers and proceed
concurrently.

#### Version tracking

A database load may start before a write and finish after it. A version
counter helps prevent that older load from replacing the newer value.

**Why:** Concurrency improves throughput but introduces ordering and
stale-result problems. This stage handles those problems.

### Stage 4 --- Cache engineering features

**Goal:** Add practical cache behaviors.

Added: - Write-through and write-back policies. - Hot loading. - Event
logging.

#### Write-through

1.  Save the value to the database.
2.  Wait for the database operation.
3.  Update the cache.
4.  Return.

This favors persistence before updating the cache, but writes may take
longer.

#### Write-back

1.  Update the cache immediately.
2.  Send the database update asynchronously.
3.  Return without waiting for the database.

This can be faster, but if persistence fails or the program stops before
the update is saved, data can be lost.

**Implementation note:** This learning project queues the database
update in the background immediately. It does not implement a durable
queue, delayed batch flushing, or retry handling.

#### Hot loading

Hot loading preloads selected keys before normal requests arrive:

``` java
cache.hotLoad("A", "B");
```

If those keys exist in the simulated database, they are added to the
cache. The first request can then be served from memory.

#### Event logging

The cache prints events such as:

``` text
[CACHE] HIT : A
[CACHE] MISS : B
[CACHE] EVICT : A
[CACHE] SET : B
```

Logs help understand cache behavior and diagnose problems.

**Why:** These features make the cache easier to configure, observe, and
use.

## Key concepts

  Concept              Simple explanation
  -------------------- --------------------------------------------
  Cache                Fast temporary storage
  Key                  Identifier used to find a value
  TTL                  How long an entry is valid
  LRU                  Remove the least recently used entry
  Future               A result that may arrive later
  Executor             Runs tasks using threads
  Request collapsing   Share one ongoing load among callers
  Write-through        Persist first, then update cache
  Write-back           Update cache first, persist asynchronously
  Hot loading          Preload data before it is requested
  Event logging        Record cache operations

## Testing checklist

### Stage 1

-   [ ] Store and retrieve a key.
-   [ ] Update a value.
-   [ ] Delete a key.
-   [ ] Read a missing key.

### Stage 2

-   [ ] Entry works before TTL expires.
-   [ ] Entry expires after TTL.
-   [ ] Cache never exceeds capacity.
-   [ ] LRU entry is evicted.

### Stage 3

-   [ ] Concurrent requests for the same missing key cause one database
    load.
-   [ ] Different keys can proceed concurrently.
-   [ ] An old load cannot overwrite a newer write.
-   [ ] Same-key operations are ordered.

### Stage 4

-   [ ] Write-through saves before returning.
-   [ ] Write-back updates the cache immediately.
-   [ ] Hot loading caches existing database values.
-   [ ] Logs show cache events.

## Code review checklist

1.  Are shared cache operations thread-safe?
2.  Is capacity always respected?
3.  Are expired entries handled correctly?
4.  Can an old load overwrite a newer value?
5.  Can same-key requests create duplicate database calls?
6.  Are same-key operations ordered?
7.  What happens when a database operation fails?
8.  Are executor services shut down?
9.  Are logs useful and not excessively noisy?
10. Are limitations clearly documented?

## Limitations and next improvements

Current limitations: - The database is simulated with a map. - Only LRU
is implemented; LFU is not. - The cache runs in one process and is not
distributed. - Write-back does not have durable retries or failure
recovery. - Logging uses `System.out.println`. - This README describes a
learning implementation, not a production service.

Possible improvements: - Add plain Java assertion-based tests. -
Implement LFU as another eviction policy. - Add a real database
adapter. - Add durable write-back retries. - Track hit rate and miss
rate. - Add periodic expiry cleanup. - Separate cache storage from the
data source using an interface.

## What this project teaches

By building in stages, this project practices: - Java collections and
encapsulation. - CRUD operations. - TTL and LRU. - Asynchronous
programming with `CompletableFuture`. - Executor services and thread
assignment. - Request collapsing. - Preventing stale asynchronous
results. - Write-policy trade-offs. - Prefetching and logging. - Testing
and reviewing incrementally.

**Main lesson:** Machine coding is not only about producing a working
class. Start with the simplest correct behavior, identify the next
problem, and improve the design one step at a time without making the
code unnecessarily complicated.
