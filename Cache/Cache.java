
public interface Cache<K, V> {

    // Create a new key-value pair
    boolean create(K key, V value);

    // Read a value using its key
    V read(K key);

    // Update an existing key
    boolean update(K key, V value);

    // Delete a key
    boolean delete(K key);

    // Check whether a key exists
    boolean containsKey(K key);

    // Get the number of entries
    int size();

    // Display all entries
    void printAll();
}
