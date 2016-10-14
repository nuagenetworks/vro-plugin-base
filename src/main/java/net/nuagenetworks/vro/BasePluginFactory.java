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