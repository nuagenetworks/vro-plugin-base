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