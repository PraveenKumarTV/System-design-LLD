
public class Main {

    public static void main(String[] args) {

        // Create a cache
        Cache<String, String> cache = new InMemoryCache<>();

        // CREATE
        System.out.println("Create name: "
                + cache.create("name", "Praveen"));

        System.out.println("Create city: "
                + cache.create("city", "Chennai"));

        // READ
        System.out.println("Name: "
                + cache.read("name"));

        // UPDATE
        System.out.println("Update name: "
                + cache.update("name", "Kumar"));

        System.out.println("Updated name: "
                + cache.read("name"));

        // Try updating a missing key
        System.out.println("Update country: "
                + cache.update("country", "India"));

        // CHECK
        System.out.println("Contains city: "
                + cache.containsKey("city"));

        // DELETE
        System.out.println("Delete city: "
                + cache.delete("city"));

        System.out.println("Contains city: "
                + cache.containsKey("city"));

        // CACHE SIZE
        System.out.println("Cache size: "
                + cache.size());

        // DISPLAY ALL
        System.out.println("Cache contents:");
        cache.printAll();
    }
}
