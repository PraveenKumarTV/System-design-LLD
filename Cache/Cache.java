
import java.util.*;

public class Cache {
    private static class Entry {
        String value;
        long expiry;

        Entry(String value, long ttl) {
            this.value = value;
            this.expiry = System.currentTimeMillis() + ttl;
        }
    }

    private final int capacity;
    private final Map<String, Entry> data =
        new LinkedHashMap<>(16, 0.75f, true);

    public Cache(int capacity) {
        this.capacity = capacity;
    }

    public void put(String key, String value, long ttl) {
        removeExpired();

        if (!data.containsKey(key) && data.size() == capacity) {
            evictLRU();
        }

        data.put(key, new Entry(value, ttl));
    }

    public String get(String key) {
        Entry entry = data.get(key);

        if (entry == null) return null;

        if (System.currentTimeMillis() >= entry.expiry) {
            data.remove(key);
            return null;
        }

        return entry.value;
    }

    public void delete(String key) {
        data.remove(key);
    }

    private void removeExpired() {
        long now = System.currentTimeMillis();

        data.entrySet().removeIf(
            item -> now >= item.getValue().expiry
        );
    }

    private void evictLRU() {
        String firstKey = data.keySet().iterator().next();
        data.remove(firstKey);
    }
}
