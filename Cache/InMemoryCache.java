
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryCache<K, V> implements Cache<K, V> {

    private final Map<K, V> storage;

    public InMemoryCache() {
        storage = new HashMap<>();
    }

    // CREATE
    @Override
    public boolean create(K key, V value) {

        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");

        // If key already exists, creation is not allowed
        if (storage.containsKey(key)) {
            return false;
        }

        storage.put(key, value);
        return true;
    }

    // READ
    @Override
    public V read(K key) {

        return storage.get(key);
    }

    // UPDATE
    @Override
    public boolean update(K key, V value) {

        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");

        // We can update only an existing key
        if (!storage.containsKey(key)) {
            return false;
        }

        storage.put(key, value);
        return true;
    }

    // DELETE
    @Override
    public boolean delete(K key) {

        if (!storage.containsKey(key)) {
            return false;
        }

        storage.remove(key);
        return true;
    }

    // CHECK WHETHER KEY EXISTS
    @Override
    public boolean containsKey(K key) {

        return storage.containsKey(key);
    }

    // GET CACHE SIZE
    @Override
    public int size() {

        return storage.size();
    }

    // DISPLAY ALL ENTRIES
    @Override
    public void printAll() {

        System.out.println(storage);
    }
}
