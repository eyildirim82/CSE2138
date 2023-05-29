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
    // You can add other necessary fields

    CacheLine() {
        this.valid = false;
        this.tag = "";
    }
}
