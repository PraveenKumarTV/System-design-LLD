
public class Main {
    public static void main(String[] args) {

        Cache cache = new Cache(
            2, WritePolicy.WRITE_THROUGH
        );

        // Preload data
        cache.hotLoad("A", "B");

        // Cache hit
        cache.getAsync("A")
             .thenAccept(System.out::println)
             .join();

        // Update value
        cache.setAsync("A", "Apricot")
             .thenAccept(System.out::println)
             .join();

        // Read updated value
        cache.getAsync("A")
             .thenAccept(System.out::println)
             .join();

        System.out.println(
            "Database calls: "
            + cache.databaseRequestCount()
        );

        cache.close();
    }
}
