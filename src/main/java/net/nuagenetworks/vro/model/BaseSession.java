package net.nuagenetworks.vro.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.nuagenetworks.bambou.RestException;
import net.nuagenetworks.bambou.RestFetcher;
import net.nuagenetworks.bambou.RestPushCenter;
import net.nuagenetworks.bambou.RestPushCenterListener;
import net.nuagenetworks.bambou.RestRootObject;
import net.nuagenetworks.bambou.RestSession;
import net.nuagenetworks.vro.model.fetchers.BaseFetcher;

public abstract class BaseSession<R extends RestRootObject> extends RestSession<R>implements RestPushCenterListener {
    private final static String EVENT_TYPE = "type";
    private final static String ENTITY_TYPE = "entityType";
    private final static String ENTITIES = "entities";
    private final static String EVENT_TYPE_CREATE = "CREATE";
    private final static String EVENT_TYPE_UPDATE = "UPDATE";
    private final static String ENTITY_PARENT_ID = "parentID";
    private final static String ENTITY_PARENT_TYPE = "parentType";
    private final static String EVENT_TYPE_DELETE = "DELETE";
    private final static String ENTITY_ID = "ID";

    protected static final Logger logger = LoggerFactory.getLogger(BaseSession.class);

    private RestPushCenter pushCenter;
    private boolean notificationsEnabled;

    public BaseSession(Class<R> restRootObjClass) {
        super(restRootObjClass);
    }

    public String getId() {
        // This uniquely identifies this session
        return getApiUrl();
    }

    protected boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    protected void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    
    @Override
    public void start() throws RestException {
        super.start();

        stopPushCenter();

        if (notificationsEnabled) {
            // Start listening for events from VSD
            pushCenter = getPushCenter();
            pushCenter.addListener(this);
            pushCenter.start();
        }
    }

    public void stop() {
        stopPushCenter();
        reset();
    }

    @Override
    public void onEvent(JsonNode event) {
        // Debug
        logger.info("Processing event: " + event.toString());

        String eventType = event.get(EVENT_TYPE).asText();
        String entityType = event.get(ENTITY_TYPE).asText();

        logger.info("eventType: " + eventType);
        logger.info("entityType: " + entityType);

        ArrayNode entities = (ArrayNode) event.get(ENTITIES);
        for (int i = 0; i < entities.size(); i++) {
            JsonNode entity = entities.get(i);
            JsonNode entityIdNode = entity.get(ENTITY_ID);
            String entityId = (entityIdNode != null && !entityIdNode.isNull()) ? entityIdNode.asText() : getId();
            JsonNode entityParentTypeNode = entity.get(ENTITY_PARENT_TYPE);
            String entityParentType = (entityParentTypeNode != null && !entityParentTypeNode.isNull()) ? entityParentTypeNode.asText() : "";
            JsonNode entityParentIdNode = entity.get(ENTITY_PARENT_ID);
            String entityParentId = (entityParentTypeNode != null && !entityParentIdNode.isNull()) ? entityParentIdNode.asText() : getId();

            logger.info("entityId: " + entityId);
            logger.info("entityParentType: " + entityParentType);
            logger.info("entityParentId: " + entityParentId);

            if (eventType.equals(EVENT_TYPE_CREATE)) {
                onEntityCreated(entityType, entityId, entityParentType, entityParentId);
            } else if (eventType.equals(EVENT_TYPE_UPDATE)) {
                onEntityUpdated(entityType, entityId, entityParentType, entityParentId);
            } else if (eventType.equals(EVENT_TYPE_DELETE)) {
                onEntityDeleted(entityType, entityId, entityParentType, entityParentId);
            }
        }
    }

    protected abstract void onEntityCreated(String entityType, String entityId, String entityParentType, String entityParentId);

    protected abstract void onEntityUpdated(String entityType, String entityId, String entityParentType, String entityParentId);

    protected abstract void onEntityDeleted(String entityType, String entityId, String entityParentType, String entityParentId);

    protected void notifyElementInvalidate(BaseSessionManager<?> sessionManager, String fetcherType, String affectedFetcherId) {
        // Refresh all the fetchers currently cached that handle this entity
        // type, except for the one specified (in params)
        for (RestFetcher<?> restFetcher : BaseModelHelper.getFetchers(fetcherType)) {
            BaseFetcher<?> fetcher = ((BaseFetcher<?>) restFetcher);

            // Make sure the fetcher found uses this session
            if (fetcher.getSession() == this) {
                String fetcherId = fetcher.getId();

                // Notify if this is not the fetcher specified
                if (!fetcherId.equals(affectedFetcherId)) {
                    sessionManager.notifyElementInvalidate(fetcherType, fetcherId);
                }
            }
        }

        // Refresh the fetcher specified
        sessionManager.notifyElementInvalidate(fetcherType, affectedFetcherId);
    }

    private void stopPushCenter() {
        if (pushCenter != null) {
            // Stop listening for events from VSD
            pushCenter.removeListener(this);
            pushCenter.stop();
            pushCenter = null;
        }
    }
}
