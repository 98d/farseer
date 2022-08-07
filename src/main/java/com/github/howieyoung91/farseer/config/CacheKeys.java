package com.github.howieyoung91.farseer.config;

public abstract class CacheKeys {
    private CacheKeys() {}

    public static String documentKey(String documentId) {
        return "farseer:document:" + documentId;
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
}
