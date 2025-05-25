package it.fulminazzo.userstalker.configuration;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
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
    
    private @Nullable String name;

    private @Nullable ConfigurationType type = ConfigurationType.YAML;

    /**
     * Builds a new file configuration from the set parameters.
     *
     * @return the file configuration
     * @throws ConfigurationException if any error occurs
     */
    public @NotNull FileConfiguration build() throws ConfigurationException {
        File pluginDirectory = getPluginDirectory();
        String name = getName();
        ConfigurationType type = getType();

        return null;
    }

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

    /**
     * Gets configuration name.
     *
     * @return the configuration name
     * @throws ConfigurationException if the configuration name has not been provided
     */
    @NotNull String getName() throws ConfigurationException {
        if (name == null) throw new ConfigurationException("No configuration name specified");
        return name;
    }

    /**
     * Sets the file name.
     *
     * @param name the name
     * @return this configurator
     */
    public @NotNull Configurator name(@Nullable String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets configuration type.
     *
     * @return the configuration type
     * @throws ConfigurationException if the configuration type has not been provided
     */
    @NotNull ConfigurationType getType() throws ConfigurationException {
        if (type == null) throw new ConfigurationException("No configuration type specified");
        return type;
    }

    /**
     * Sets the configuration type.
     *
     * @param type the type
     * @return this configurator
     */
    public @NotNull Configurator type(@Nullable ConfigurationType type) {
        this.type = type;
        return this;
    }

}
