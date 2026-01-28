//package earthrp.database;
//
//import earthrp.Earth;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class ChunkClaimCache {
//    public record ChunkCacheResult(boolean claimed, UUID townId) {
//    }
//
//    private static class CacheEntry {
//        boolean claimed; // true если занят, false — свободен
//        UUID townId;     // null если свободен
//        long expiryTime;
//
//        CacheEntry(UUID townId, long ttlMillis) {
//            this.claimed = townId != null;
//            this.townId = townId;
//            this.expiryTime = System.currentTimeMillis() + ttlMillis;
//        }
//
//        boolean isExpired() {
//            return System.currentTimeMillis() > expiryTime;
//        }
//    }
//
//    private final Map<Earth.ChunkPosition, CacheEntry> cache = new HashMap<>();
//    private final long ttlMillis;
//
//    public ChunkClaimCache(long ttlMillis) {
//        this.ttlMillis = ttlMillis;
//    }
//
//    public ChunkCacheResult get(Earth.ChunkPosition chunk) {
//        CacheEntry entry = cache.get(chunk);
//        if (entry == null || entry.isExpired()) {
//            cache.remove(chunk);
//            return null; // в кэше нет данных
//        }
//        return new ChunkCacheResult(entry.claimed, entry.townId);
//    }
//
//    public void put(Earth.ChunkPosition chunk, UUID townId) {
//        cache.put(chunk, new CacheEntry(townId, ttlMillis));
//    }
//
//    public void clearCache(){
//        cache.clear();
//    }
//
//    public void invalidateAllByTownId(UUID townId) {
//        long now = System.currentTimeMillis();
//        cache.forEach((chunk, entry) -> {
//            if (townId.equals(entry.townId)) {
//                // Заменяем townId на null и обновляем expiryTime
//                entry.townId = null;
//                entry.expiryTime = now + 1;
//            }
//        });
//    }
//}
