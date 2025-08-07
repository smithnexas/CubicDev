import java.util.*;

public class MultilevelCacheSystem {

    private final int l1Capacity;
    private final int l2Capacity;

    private final Map<String, String> l1 = new LinkedHashMap<>(16, 0.75f, true);
    private final Map<String, String> l2 = new LinkedHashMap<>(16, 0.75f, true);

    public MultilevelCacheSystem(int l1Capacity, int l2Capacity) {
        this.l1Capacity = l1Capacity;
        this.l2Capacity = l2Capacity;
    }

    /**
     * Inserts or updates a key-value pair in the multi-level cache system.
     * <p>
     * If the key already exists in the L1 cache, its value is updated.
     * If the key exists in the L2 cache, it is removed from L2 and added to L1,
     * evicting an entry from L1 if necessary.
     * If the key does not exist in either cache, it is added to L1,
     * evicting an entry from L1 if necessary.
     * </p>
     *
     * @param key   the key to insert or update in the cache
     * @param value the value to associate with the key
     * 
     * 
     */
    public void put(String key, String value) {
        if (l1.containsKey(key)) {
            l1.put(key, value);
        } else if (l2.containsKey(key)) {
            l2.remove(key);
            evictIfNeeded(l1, l1Capacity);
            l1.put(key, value);
        } else {
            evictIfNeeded(l1, l1Capacity);
            l1.put(key, value);
        }
    }

    public String get(String key) {
        if (l1.containsKey(key)) {
            return l1.get(key);
        } else if (l2.containsKey(key)) {
            String val = l2.remove(key);
            evictIfNeeded(l1, l1Capacity);
            l1.put(key, val);
            return val;
        }
        return null;
    }

    /**
     * Checks if the given cache has reached or exceeded its capacity and evicts the oldest entry if necessary.
     * The evicted entry is passed to the L2 cache via the {@code evictToL2} method.
     *
     * @param cache    the cache map to check and potentially evict from
     * @param capacity the maximum allowed number of entries in the cache
     */
    private void evictIfNeeded(Map<String, String> cache, int capacity) {
        if (capacity > 0 && cache.size() >= capacity) {
            Iterator<Map.Entry<String, String>> it = cache.entrySet().iterator();
            Map.Entry<String, String> oldest = it.next();
            it.remove();
            if (cache == l1) evictToL2(oldest.getKey(), oldest.getValue());
        }
    }

    private void evictToL2(String key, String value) {
        if (l2.containsKey(key)) {
            l2.remove(key); // Replace existing
        }
        evictIfNeeded(l2, l2Capacity);
        l2.put(key, value);
    }

    public void printCaches() {
        System.out.println("L1 Cache: " + l1);
        System.out.println("L2 Cache: " + l2);
    }
}
