package net.nuagenetworks.vro.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dunes.vso.sdk.helper.SDKHelper;
import net.nuagenetworks.bambou.RestException;
import net.nuagenetworks.vro.BasePluginFactory;

public abstract class BaseSessionManager<T extends BaseSession<?>> {
    private static final Logger logger = LoggerFactory.getLogger(BaseSessionManager.class);

    private List<BasePluginFactory> factories = Collections.synchronizedList(new ArrayList<BasePluginFactory>());
    private List<T> sessions = new ArrayList<T>();
    private String filePath;
    private String pluginRoot;

    protected BaseSessionManager(String pluginRoot, String filename) {
        this.pluginRoot = pluginRoot;

        try {
            filePath = SDKHelper.getConfigurationPathForPluginName(filename);
            load();
        } catch (RestException ex) {
            logger.error("Error", ex);
        } catch (IOException ex) {
            logger.error("Error", ex);
        } catch (Exception ex) {
            logger.error("Error", ex);
        }
    }

    protected abstract T createSession(String username, String password, String enterprise, String apiUrl, String certificate);

    public void addFactory(BasePluginFactory factory) {
        logger.info("Adding factory: " + factory);
        factories.add(factory);
    }

    public void removeFactory(BasePluginFactory factory) {
        logger.info("Removing factory: " + factory);
        factories.remove(factory);
    }

    public void addSession(T session) throws RestException {
        // Make sure there is not another session with the same name
        for (T existingSession : sessions) {
            if (existingSession.getId().equals(session.getId())) {
                throw new RestException("A session with the same name/id already exists");
            }
        }

        try {
            sessions.add(session);
            notifyElementInvalidate(pluginRoot, null);
            save();
        } catch (IOException ex) {
            throw new RestException(ex);
        }
    }

    public void removeSession(T session) throws RestException {
        try {
            sessions.remove(session);
            notifyElementDeleted(BaseConstants.SESSION, session.getId());
            save();
        } catch (IOException ex) {
            throw new RestException(ex);
        }
    }

    public List<T> getSessions() {
        return sessions;
    }

    public T getSessionById(String sessionId) {
        for (T session : sessions) {
            if (session.getId().equals(sessionId)) {
                return session;
            }
        }

        return null;
    }

    public void notifyElementInvalidate(String type, String id) {
        for (BasePluginFactory factory : new ArrayList<BasePluginFactory>(factories)) {
            logger.info("Invalidating - type: " + type + ", id: " + id + " to factory: " + factory);
            factory.getPluginNotificationHandler().notifyElementInvalidate(type, id);
        }
    }

    public void notifyElementDeleted(String type, String id) {
        for (BasePluginFactory factory : new ArrayList<BasePluginFactory>(factories)) {
            logger.info("Deleting - type: " + type + ", id: " + id + " to factory: " + factory);
            factory.getPluginNotificationHandler().notifyElementDeleted(type, id);
        }
    }

    public void notifyElementUpdated(String type, String id) {
        for (BasePluginFactory factory : new ArrayList<BasePluginFactory>(factories)) {
            logger.info("Updating - type: " + type + ", id: " + id + " to factory: " + factory);
            factory.getPluginNotificationHandler().notifyElementUpdated(type, id);
        }
    }

    private void load() throws IOException, RestException {
        File f = new File(filePath);

        FileInputStream fis = null;
        FileLock lock = null;

        try {
            if (!f.exists()) {
                createNewFile(f);
                return;
            }

            fis = new FileInputStream(f);
            lock = fis.getChannel().lock(0L, Long.MAX_VALUE, true);
            Properties props = new Properties();
            props.load(fis);

            // Add all the sessions found in the config file
            for (int i = 1;; i++) {
                String apiUrl = props.getProperty("Session." + i + ".apiUrl");
                if (apiUrl == null) {
                    break;
                }
                String username = props.getProperty("Session." + i + ".username");
                String password = props.getProperty("Session." + i + ".password");
                String enterprise = props.getProperty("Session." + i + ".enterprise");
                String notificationsEnabledStr = props.getProperty("Session." + i + ".notificationsEnabled");
                boolean notificationsEnabled = (notificationsEnabledStr != null) ? Boolean.valueOf(notificationsEnabledStr) : true;

                T session = createSession(username, password, enterprise, apiUrl, null);
                session.setNotificationsEnabled(notificationsEnabled);
                session.start();
                logger.info("Adding session: " + session.getId());
                sessions.add(session);
            }

        } finally {
            if ((lock != null) && (lock.isValid())) {
                lock.release();
            }
            IOUtils.closeQuietly(fis);
        }
    }

    private void save() throws IOException {
        logger.info("Saving server config");

        File f = new File(filePath);

        FileOutputStream fos = null;
        FileLock lock = null;
        try {
            if (!f.exists()) {
                createNewFile(f);
            }

            fos = new FileOutputStream(f);
            lock = fos.getChannel().lock();

            // Save all the configured sessions in the config file
            int sessionCount = 1;
            Properties props = new Properties();
            for (T session : sessions) {
                props.setProperty("Session." + sessionCount + ".apiUrl", session.getApiUrl());
                props.setProperty("Session." + sessionCount + ".username", session.getUsername());
                props.setProperty("Session." + sessionCount + ".password", session.getPassword());
                props.setProperty("Session." + sessionCount + ".enterprise", session.getEnterprise());
                props.setProperty("Session." + sessionCount + ".notificationsEnabled", Boolean.toString(session.getNotificationsEnabled()));
                sessionCount++;
            }
            props.store(fos, "List of configured sessions");
            fos.flush();
        } finally {
            if ((lock != null) && (lock.isValid())) {
                lock.release();
            }
            IOUtils.closeQuietly(fos);
        }

        logger.info("Completed saving server config");
    }

    private void createNewFile(File file) throws IOException {

        File directory = new File(FilenameUtils.getFullPath(file.getAbsolutePath()));
        if (!directory.exists()) {
            FileUtils.forceMkdir(directory);
        }
        if (file.createNewFile()) {
            logger.info("File created: " + file.getAbsolutePath());
        } else {
            logger.warn("File already exists: " + file.getAbsolutePath());
        }
    }
}