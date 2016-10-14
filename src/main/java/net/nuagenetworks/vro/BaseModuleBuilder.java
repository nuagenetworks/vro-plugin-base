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

import com.vmware.o11n.plugin.sdk.module.ModuleBuilder;

import ch.dunes.vso.sdk.api.IPluginAdaptor;
import net.nuagenetworks.vro.model.BaseConstants;

public class BaseModuleBuilder extends ModuleBuilder {
    private String pluginName;
    private String pluginDescription;
    private String pluginRoot;
    private Class<? extends IPluginAdaptor> pluginAdaptorClass;
    private String basePackage;
    private String pluginImageFilename;

    protected BaseModuleBuilder(Class<? extends IPluginAdaptor> pluginAdaptorClass, String basePackage, String pluginName, String pluginDescription,
            String pluginRoot, String pluginImageFilename) {
        this.pluginAdaptorClass = pluginAdaptorClass;
        this.basePackage = basePackage;
        this.pluginName = pluginName;
        this.pluginDescription = pluginDescription;
        this.pluginRoot = pluginRoot;
        this.pluginImageFilename = pluginImageFilename;
    }

    @Override
    public void configure() {
        module(pluginName).withDescription(pluginDescription).withImage(pluginImageFilename).basePackages(basePackage).version("${project.version}");

        installation(InstallationMode.BUILD).action(ActionType.INSTALL_PACKAGE, "packages/${project.artifactId}-package-${project.version}.package");

        finderDatasource(pluginAdaptorClass, BaseConstants.DATASOURCE).anonymousLogin(LoginMode.INTERNAL);

        inventory(pluginRoot);
        finder(pluginRoot, BaseConstants.DATASOURCE).addRelation(BaseConstants.SESSION, BaseConstants.SESSIONS);
    }
}