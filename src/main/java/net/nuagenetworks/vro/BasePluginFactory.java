package net.nuagenetworks.vro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dunes.vso.sdk.api.HasChildrenResult;
import ch.dunes.vso.sdk.api.IPluginFactory;
import ch.dunes.vso.sdk.api.IPluginNotificationHandler;
import ch.dunes.vso.sdk.api.PluginExecutionException;
import ch.dunes.vso.sdk.api.QueryResult;
import net.nuagenetworks.bambou.RestException;

public abstract class BasePluginFactory implements IPluginFactory {
    private static final Logger logger = LoggerFactory.getLogger(BasePluginFactory.class);

    private IPluginNotificationHandler pluginNotificationHandler;

    protected BasePluginFactory(IPluginNotificationHandler pluginNotificationHandler) {
        this.pluginNotificationHandler = pluginNotificationHandler;
    }

    protected abstract List<?> findRelationImpl(String type, String id, String relationName) throws RestException;

    protected abstract QueryResult findAllImpl(String type, String query) throws RestException;

    protected abstract Object findImpl(String type, String id) throws RestException;

    public IPluginNotificationHandler getPluginNotificationHandler() {
        return pluginNotificationHandler;
    }

    @Override
    public Object find(String type, String id) {
        // Debug
        logger.debug("find() --> type: " + type + ", " + id);

        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(id))
            return null;

        try {
            return findImpl(type, id);
        } catch (RestException ex) {
            logger.error("Error", ex);
            return null;
        }
    }

    @Override
    public List<?> findRelation(String type, String id, String relationName) {
        // Debug
        logger.debug("findRelation() --> type: " + type + ", id: " + id + ", relationName: " + relationName);

        try {
            return findRelationImpl(type, id, relationName);
        } catch (RestException ex) {
            logger.error("Error", ex);
            return null;
        }
    }

    @Override
    public QueryResult findAll(String type, String query) {
        // Debug
        logger.debug("findAll() --> type: " + type + ", query: " + query);

        try {
            return findAllImpl(type, query);
        } catch (RestException ex) {
            logger.error("Error", ex);
            return null;
        }
    }

    @Override
    public final HasChildrenResult hasChildrenInRelation(String type, String id, String relationName) {
        return HasChildrenResult.Unknown;
    }

    @Override
    public void executePluginCommand(String command) throws PluginExecutionException {
    }

    @Override
    public void invalidate(String type, String id) {
    }

    @Override
    public void invalidateAll() {
    }

    protected <T> List<T> toList(T obj) {
        return (obj != null) ? Arrays.asList(obj) : new ArrayList<T>();
    }
}