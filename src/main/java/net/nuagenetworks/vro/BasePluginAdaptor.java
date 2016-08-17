package net.nuagenetworks.vro;

import javax.security.auth.login.LoginException;

import ch.dunes.vso.sdk.api.IPluginAdaptor;
import ch.dunes.vso.sdk.api.IPluginEventPublisher;
import ch.dunes.vso.sdk.api.IPluginFactory;
import ch.dunes.vso.sdk.api.IPluginNotificationHandler;
import ch.dunes.vso.sdk.api.IPluginPublisher;
import ch.dunes.vso.sdk.api.PluginLicense;
import ch.dunes.vso.sdk.api.PluginLicenseException;
import ch.dunes.vso.sdk.api.PluginWatcher;
import net.nuagenetworks.vro.model.BaseSessionManager;

public abstract class BasePluginAdaptor implements IPluginAdaptor {
    private BaseSessionManager<?> sessionManager;

    protected BasePluginAdaptor(BaseSessionManager<?> sessionManager) {
        this.sessionManager = sessionManager;
    }

    protected abstract BasePluginFactory createPluginFactory(IPluginNotificationHandler pluginNotificationHandler);

    @Override
    public IPluginFactory createPluginFactory(String sessionId, String username, String password, IPluginNotificationHandler pluginNotificationHandler)
            throws SecurityException, LoginException, PluginLicenseException {
        BasePluginFactory factory = createPluginFactory(pluginNotificationHandler);
        sessionManager.addFactory(factory);

        return factory;
    }

    @Override
    public void setPluginName(String pluginName) {
    }

    @Override
    public void uninstallPluginFactory(IPluginFactory factory) {
        sessionManager.removeFactory((BasePluginFactory) factory);
    }

    @Override
    public void addWatcher(PluginWatcher arg0) {
    }

    @Override
    public void installLicenses(PluginLicense[] arg0) throws PluginLicenseException {
    }

    @Override
    public void registerEventPublisher(String arg0, String arg1, IPluginEventPublisher arg2) {
    }

    @Override
    public void removeWatcher(String arg0) {
    }

    @Override
    public void setPluginPublisher(IPluginPublisher arg0) {
    }

    @Override
    public void unregisterEventPublisher(String arg0, String arg1, IPluginEventPublisher arg2) {
    }
}