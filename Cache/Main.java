
public class Main {
    public static void main(String[] args) throws Exception {
        Cache cache = new Cache(2);

        cache.put("A", "Apple", 10000);
        cache.put("B", "Banana", 10000);

        System.out.println(cache.get("A")); // Apple

        cache.put("C", "Cherry", 10000); // B is evicted

        System.out.println(cache.get("B")); // null
        System.out.println(cache.get("C")); // Cherry

        cache.put("D", "Dates", 1000);
        Thread.sleep(1500);

        System.out.println(cache.get("D")); // null
    }
}
