
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Cache {

    private static class Entry {
        String value;
        long expiry;

        Entry(String value, long ttl) {
            this.value = value;
            expiry = System.currentTimeMillis() + ttl;
        }
    }

    private final int capacity;
    private final long ttl;

    // Cache with LRU order
    private final Map<String, Entry> cache =
        new LinkedHashMap<>(16, 0.75f, true);

    // Simulated database
    private final Map<String, String> database = new HashMap<>();

    // Same-key requests share this future
    private final Map<String, CompletableFuture<String>> loading =
        new ConcurrentHashMap<>();

    // Tracks writes to prevent an old load overwriting a new value
    private final Map<String, AtomicLong> versions =
        new ConcurrentHashMap<>();

    // Different keys can work concurrently
    private final ExecutorService[] workers = new ExecutorService[4];
    private final ExecutorService dbWorker =
        Executors.newFixedThreadPool(4);

    private final AtomicLong dbRequests = new AtomicLong();

    public Cache(int capacity, long ttl) {
        this.capacity = capacity;
        this.ttl = ttl;

        database.put("A", "Apple");
        database.put("B", "Banana");

        for (int i = 0; i < workers.length; i++) {
            workers[i] = Executors.newSingleThreadExecutor();
        }
    }

    private int workerIndex(String key) {
        return Math.abs(key.hashCode() % workers.length);
    }

    private AtomicLong version(String key) {
        return versions.computeIfAbsent(key, k -> new AtomicLong());
    }

    private String cachedValue(String key) {
        synchronized (cache) {
            Entry entry = cache.get(key);

            if (entry == null) return null;

            if (System.currentTimeMillis() >= entry.expiry) {
                cache.remove(key);
                return null;
            }

            return entry.value;
        }
    }

    private void putCache(String key, String value) {
        synchronized (cache) {
            cache.entrySet().removeIf(
                item -> System.currentTimeMillis()
                    >= item.getValue().expiry
            );

            if (!cache.containsKey(key) && cache.size() >= capacity) {
                String oldest = cache.keySet().iterator().next();
                cache.remove(oldest);
            }

            cache.put(key, new Entry(value, ttl));
        }
    }

    public CompletableFuture<String> getAsync(String key) {

        CompletableFuture<CompletableFuture<String>> task =
            CompletableFuture.supplyAsync(() -> {

                String value = cachedValue(key);

                if (value != null) {
                    return CompletableFuture.completedFuture(value);
                }

                long currentVersion = version(key).get();

                CompletableFuture<String> future =
                    loading.computeIfAbsent(key, k ->
                        CompletableFuture.supplyAsync(() -> {
                            dbRequests.incrementAndGet();

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }

                            return database.get(k);
                        }, dbWorker).thenApply(result -> {

                            if (result != null &&
                                version(key).get() == currentVersion) {
                                putCache(key, result);
                            }

                            return result;
                        })
                    );

                future.whenComplete(
                    (result, error) -> loading.remove(key, future)
                );

                return future;

            }, workers[workerIndex(key)]);

        return task.thenCompose(future -> future);
    }

    public CompletableFuture<String> setAsync(
            String key, String value) {

        return CompletableFuture.supplyAsync(() -> {
            version(key).incrementAndGet();
            putCache(key, value);
            return value;
        }, workers[workerIndex(key)]);
    }

    public long databaseRequestCount() {
        return dbRequests.get();
    }

    public void close() {
        for (ExecutorService worker : workers) {
            worker.shutdown();
        }

        dbWorker.shutdown();
    }
}
