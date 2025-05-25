package it.fulminazzo.userstalker.configuration;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * A builder for {@link FileConfiguration}.
 */
@NoArgsConstructor
public final class Configurator {

    private @Nullable File pluginDirectory;

    private @Nullable ConfigurationType type = ConfigurationType.YAML;

    /**
     * Gets plugin directory.
     *
     * @return the plugin directory
     * @throws ConfigurationException if the plugin directory has not been provided
     */
    @NotNull File getPluginDirectory() throws ConfigurationException {
        if (pluginDirectory == null) throw new ConfigurationException("No plugin directory specified");
        return pluginDirectory;
    }

    /**
     * Sets the plugin directory.
     *
     * @param pluginDirectory the plugin directory
     * @return this configurator
     */
    public @NotNull Configurator pluginDirectory(@Nullable File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
        return this;
    }

}
