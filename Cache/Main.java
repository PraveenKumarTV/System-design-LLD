
import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) throws Exception {

        Cache cache = new Cache(3, 10000);

        // Three requests for the same key
        CompletableFuture<String> request1 =
            cache.getAsync("A");

        CompletableFuture<String> request2 =
            cache.getAsync("A");

        CompletableFuture<String> request3 =
            cache.getAsync("A");

        System.out.println(request1.get());
        System.out.println(request2.get());
        System.out.println(request3.get());

        System.out.println("Database calls: "
            + cache.databaseRequestCount());

        // Update the value
        cache.setAsync("A", "Updated Apple").get();

        // Read our own write
        System.out.println(cache.getAsync("A").get());

        cache.close();
    }
}
