package io.firedancer.contrib.gerrit.gha;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public class Module extends AbstractModule {
    private final String pluginName;

    @Inject
    Module(@PluginName String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    protected void configure() {}
}
