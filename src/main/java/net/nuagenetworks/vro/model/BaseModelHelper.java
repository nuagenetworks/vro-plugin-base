package net.nuagenetworks.vro.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.nuagenetworks.vro.model.fetchers.BaseFetcher;

public class BaseModelHelper {
    private static final Logger logger = LoggerFactory.getLogger(BaseModelHelper.class);

    private static Map<String, Cache<String, Object>> cache = new HashMap<String, Cache<String, Object>>();

    @SuppressWarnings("unchecked")
    public static <T extends BaseFetcher<? extends BaseObjectExtensions>> List<T> getFetchers(String fetcherType) {
        List<T> fetchers = new ArrayList<T>();
        for (Object obj : getObjectsFromCache(fetcherType)) {
            fetchers.add((T) obj);
        }
        return fetchers;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends BaseObjectExtensions> T getObject(String objType, String objId) {
        return (T) getObjectFromCache(objType, objId);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends BaseFetcher<? extends BaseObjectExtensions>> T getFetcher(String fetcherType, String fetcherId) {
        return (T) getObjectFromCache(fetcherType, fetcherId);
    }

    protected static <T extends BaseObjectExtensions> T addObject(String objType, T obj) {
        addObjectToCache(objType, obj.getId(), obj);
        return obj;
    }

    protected static <T extends BaseFetcher<? extends BaseObjectExtensions>> T addFetcher(String fetcherType, T fetcher) {
        addObjectToCache(fetcherType, fetcher.getId(), fetcher);
        return fetcher;
    }

    protected static <T extends BaseFetcher<? extends BaseObjectExtensions>> T addFetcherObjects(T fetcher, String objType) {
        logger.info("Fetchers objects to add: " + fetcher.size());
        for (BaseObjectExtensions obj : fetcher) {
            addObject(objType, obj);
        }
        return fetcher;
    }

    protected static void clearCache() {
        cache.clear();
    }

    private static void addObjectToCache(String objType, String objId, Object obj) {
        Cache<String, Object> objTypeCache = cache.get(objType);
        if (objTypeCache == null) {
            objTypeCache = CacheBuilder.newBuilder().weakValues().build();
            cache.put(objType, objTypeCache);
        }

        objTypeCache.put(objId, obj);

        logger.info("Added object to cache: " + objType + " " + objId + " " + obj);
    }

    private static Object getObjectFromCache(String objType, String objId) {
        Cache<String, Object> objTypeCache = cache.get(objType);
        if (objTypeCache != null) {
            Object obj = objTypeCache.getIfPresent(objId);
            logger.info("Retrieved object from cache: " + objType + " " + objId + " " + obj);
            return obj;
        }

        return null;
    }

    private static Collection<Object> getObjectsFromCache(String objType) {
        Cache<String, Object> objTypeCache = cache.get(objType);
        if (objTypeCache != null) {
            return objTypeCache.asMap().values();
        } else {
            return Collections.emptyList();
        }
    }
}
