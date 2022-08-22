package com.github.howieyoung91.farseer.core.config;

public abstract class CacheKeys {
    private CacheKeys() {}

    public static String documentKey(String documentId) {
        return "farseer:document:" + documentId;
    }

    public static String tokenIdCuckooFilter() {
        return "farseer:token:filter:cuckoo:id";

    }

    public static String tokenWordCuckooFilter() {
        return "farseer:token:filter:cuckoo:word";
    }

    public static String tokenIdKey(String tokenId) {
        return "farseer:token:id:" + tokenId;
    }

    public static String tokenWordKey(String word) {
        return "farseer:token:word:" + word;
    }

    public static String indicesOfDocumentKey(String documentId) {
        return "farseer:document:" + documentId + ":indices";
    }

    public static String documentIdCuckooFilter() {
        return "farseer:document:filter:cuckoo:id";
    }

}