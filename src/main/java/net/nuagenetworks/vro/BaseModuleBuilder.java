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