/*
  Copyright (c) 2015, Alcatel-Lucent Inc
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
      * Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
      * Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
      * Neither the name of the copyright holder nor the names of its contributors
        may be used to endorse or promote products derived from this software without
        specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
        logger.debug("Fetchers objects to add: " + fetcher.size());
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

        logger.debug("Added object to cache: " + objType + " " + objId + " " + obj);
    }

    private static Object getObjectFromCache(String objType, String objId) {
        Cache<String, Object> objTypeCache = cache.get(objType);
        if (objTypeCache != null) {
            Object obj = objTypeCache.getIfPresent(objId);
            logger.debug("Retrieved object from cache: " + objType + " " + objId + " " + obj);
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
