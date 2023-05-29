import java.io.*;

class CacheSimulator {
    static int hitCount = 0;
    static int missCount = 0;
    static int evictionCount = 0;

    static void accessCache(Cache cache, String address) {
        // Convert the address to binary and split into tag, set, and block parts
        String binaryAddress = Integer.toBinaryString(Integer.parseInt(address, 16));
        String tag = binaryAddress.substring(0, binaryAddress.length() - (cache.s + cache.b));
        int setIndex = Integer.parseInt(binaryAddress.substring(tag.length(), tag.length() + cache.s), 2);

        // Check if the line is in the cache
        boolean hit = false;
        int emptyIndex = -1;
        int lruIndex = 0;
        long lruTime = Long.MAX_VALUE;
        for (int i = 0; i < cache.cache[setIndex].length; i++) {
            CacheLine line = cache.cache[setIndex][i];
            if (line.valid && line.tag.equals(tag)) {
                hit = true;
                line.lastAccessTime = System.nanoTime();
                break;
            }
            if (!line.valid && emptyIndex == -1) {
                emptyIndex = i;
            }
            if (line.lastAccessTime < lruTime) {
                lruTime = line.lastAccessTime;
                lruIndex = i;
            }
        }

        // If it is, increment the hit count
        if (hit) {
            hitCount++;
        } else {
            // If not, increment the miss count and handle eviction if necessary
            missCount++;
            if (emptyIndex != -1) {
                // If there is an empty line, use it
                cache.cache[setIndex][emptyIndex].valid = true;
                cache.cache[setIndex][emptyIndex].tag = tag;
                cache.cache[setIndex][emptyIndex].lastAccessTime = System.nanoTime();
            } else {
                // Otherwise, evict the LRU line
                cache.cache[setIndex][lruIndex].tag = tag;
                cache.cache[setIndex][lruIndex].lastAccessTime = System.nanoTime();
                evictionCount++;
            }
        }
    }

    public static void main(String[] args) {
        int L1s = 0, L1E = 0, L1b = 0, L2s = 0, L2E = 0, L2b = 0;
        String traceFile = "";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-L1s":
                    L1s = Integer.parseInt(args[++i]);
                    break;
                case "-L1E":
                    L1E = Integer.parseInt(args[++i]);
                    break;
                case "-L1b":
                    L1b = Integer.parseInt(args[++i]);
                    break;
                case "-L2s":
                    L2s = Integer.parseInt(args[++i]);
                    break;
                case "-L2E":
                    L2E = Integer.parseInt(args[++i]);
                    break;
                case "-L2b":
                    L2b = Integer.parseInt(args[++i]);
                    break;
                case "-t":
                    traceFile = args[++i];
                    break;
                default:
                    System.out.println("Invalid argument: " + args[i]);
                    System.exit(1);
                   }

        Cache cache = new Cache(L1s, L1E, L1b);

        // Read the trace file and simulate cache accesses
        try (BufferedReader br = new BufferedReader(new FileReader(traceFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length < 3) {
                    System.out.println("Invalid trace file line: " + line);
                    System.exit(1);
                }
                String address = parts[2];
                accessCache(cache, address);
            }
        } catch (IOException e) {
            System.out.println("Error reading trace file: " + e.getMessage());
            System.exit(1);
        }

        // After all accesses
        System.out.println("Hits: " + hitCount);
        System.out.println("Misses: " + missCount);
        System.out.println("Evictions: " + evictionCount);

        // Print the final state of the cache
        for (int i = 0; i < cache.cache.length; i++) {
            for (int j = 0; j < cache.cache[i].length; j++) {
                System.out.println("Set " + i + ", Line " + j + ": " +
                                   (cache.cache[i][j].valid ? "Valid, Tag: " + cache.cache[i][j].tag : "Invalid"));
            }
        }
    }
}

class Cache {
    int s;  // set index bits
    int E;  // number of lines per set
    int b;  // block bits
    CacheLine[][] cache;  // The cache itself

    Cache(int s, int E, int b) {
        this.s = s;
        this.E = E;
        this.b = b;
        this.cache = new CacheLine[1 << s][E];
        for (int i = 0; i < (1 << s); i++) {
            for (int j = 0; j < E; j++) {
                this.cache[i][j] = new CacheLine();
            }
        }
    }
}

class CacheLine {
    boolean valid;
    String tag;
    long lastAccessTime;  // For LRU policy

    CacheLine() {
        this.valid = false;
        this.tag = "";
        this.lastAccessTime = 0;
    }
}
