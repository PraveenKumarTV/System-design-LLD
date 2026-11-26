
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

enum WritePolicy {
    WRITE_THROUGH,
    WRITE_BACK
}

public class Cache {

    private static final long DEFAULT_TTL = 60_000;

    private final WritePolicy writePolicy;
    private final int capacity;
    private final long ttl;

    private static class Entry {
        String value;
        long expiry;

        Entry(String value, long ttl) {
            this.value = value;
            this.expiry = System.currentTimeMillis() + ttl;
        }
    }

    // LRU cache: least recently used entry comes first
    private final Map<String, Entry> cache =
        new LinkedHashMap<>(16, 0.75f, true);

    // Simulated database
    private final Map<String, String> database =
        new ConcurrentHashMap<>();

    // Same-key requests share one database future
    private final Map<String, CompletableFuture<String>> loading =
        new ConcurrentHashMap<>();

    // Prevents old database loads from overwriting newer writes
    private final Map<String, AtomicLong> versions =
        new ConcurrentHashMap<>();

    // Same key always uses the same worker
    private final ExecutorService[] workers =
        new ExecutorService[4];

    // Single database worker preserves write order
    private final ExecutorService dbWorker =
        Executors.newSingleThreadExecutor();

    private final AtomicLong dbRequests = new AtomicLong();

    public Cache(int capacity, WritePolicy writePolicy) {
        this(capacity, writePolicy, DEFAULT_TTL);
    }

    public Cache(int capacity, WritePolicy writePolicy,
                 long ttl) {

        if (capacity <= 0 || ttl <= 0) {
            throw new IllegalArgumentException(
                "Capacity and TTL must be positive"
            );
        }

        this.capacity = capacity;
        this.writePolicy = writePolicy;
        this.ttl = ttl;

        for (int i = 0; i < workers.length; i++) {
            workers[i] = Executors.newSingleThreadExecutor();
        }

        // Sample database data
        database.put("A", "Apple");
        database.put("B", "Banana");
    }

    private int workerIndex(String key) {
        return Math.abs(key.hashCode() % workers.length);
    }

    private AtomicLong version(String key) {
        return versions.computeIfAbsent(
            key, k -> new AtomicLong()
        );
    }

    private String cachedValue(String key) {
        synchronized (cache) {
            Entry entry = cache.get(key);

            if (entry == null) {
                return null;
            }

            if (System.currentTimeMillis() >= entry.expiry) {
                cache.remove(key);
                log("EXPIRED", key);
                return null;
            }

            return entry.value;
        }
    }

    private void putCache(String key, String value) {
        synchronized (cache) {

            // Remove expired entries
            cache.entrySet().removeIf(
                item -> System.currentTimeMillis()
                    >= item.getValue().expiry
            );

            // Evict LRU entry if cache is full
            if (!cache.containsKey(key)
                    && cache.size() >= capacity) {

                String oldest =
                    cache.keySet().iterator().next();

                cache.remove(oldest);
                log("EVICT", oldest);
            }

            cache.put(key, new Entry(value, ttl));
        }

        log("CACHE PUT", key);
    }

    public CompletableFuture<String> getAsync(String key) {

        CompletableFuture<CompletableFuture<String>> task =
            CompletableFuture.supplyAsync(() -> {

                String value = cachedValue(key);

                if (value != null) {
                    log("HIT", key);
                    return CompletableFuture.completedFuture(value);
                }

                log("MISS", key);

                long currentVersion = version(key).get();

                CompletableFuture<String> future =
                    loading.computeIfAbsent(key, k ->

                        CompletableFuture.supplyAsync(() -> {
                            dbRequests.incrementAndGet();
                            log("DB LOAD", key);

                            return database.get(k);

                        }, dbWorker).thenApply(result -> {

                            // Don't overwrite a newer write
                            if (result != null &&
                                version(key).get()
                                    == currentVersion) {

                                putCache(key, result);
                            }

                            return result;
                        })
                    );

                future.whenComplete(
                    (result, error) ->
                        loading.remove(key, future)
                );

                return future;

            }, workers[workerIndex(key)]);

        return task.thenCompose(future -> future);
    }

    public CompletableFuture<String> setAsync(
            String key, String value) {

        return CompletableFuture.supplyAsync(() -> {

            // Mark this as a newer write
            version(key).incrementAndGet();

            if (writePolicy == WritePolicy.WRITE_THROUGH) {

                // Database first; wait for completion
                CompletableFuture.runAsync(() -> {
                    database.put(key, value);
                }, dbWorker).join();

                putCache(key, value);

            } else {

                // Cache first; return quickly
                putCache(key, value);

                // Persist asynchronously
                CompletableFuture.runAsync(() -> {
                    database.put(key, value);
                    log("DB SAVE", key);
                }, dbWorker);
            }

            log("SET", key);
            return value;

        }, workers[workerIndex(key)]);
    }

    // Prefetch selected database keys into cache
    public void hotLoad(String... keys) {
        for (String key : keys) {
            String value = database.get(key);

            if (value != null) {
                putCache(key, value);
                log("HOT LOAD", key);
            }
        }
    }

    public long databaseRequestCount() {
        return dbRequests.get();
    }

    private void log(String event, String key) {
        System.out.println(
            "[CACHE] " + event + " : " + key
        );
    }

    public void close() {
        for (ExecutorService worker : workers) {
            worker.shutdown();
        }

        dbWorker.shutdown();
    }
}
