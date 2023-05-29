class CacheSimulator {
    // ... (other code)

    static void accessCache(Cache cache, String address) {
        // Convert the address to binary and split into tag, set, and block parts
        String binaryAddress = Integer.toBinaryString(Integer.parseInt(address, 16));
        String tag = binaryAddress.substring(0, binaryAddress.length() - (cache.s + cache.b));
        int setIndex = Integer.parseInt(binaryAddress.substring(tag.length(), tag.length() + cache.s), 2);

        // Check if the line is in the cache
        boolean hit = false;
        for (CacheLine line : cache.cache[setIndex]) {
            if (line.valid && line.tag.equals(tag)) {
                hit = true;
                break;
            }
        }

        // If it is, increment the hit count
        if (hit) {
            // ... (increment hit count)
        } else {
            // If not, increment the miss count and handle eviction if necessary
            // ... (increment miss count and handle eviction)
        }
    }
}
