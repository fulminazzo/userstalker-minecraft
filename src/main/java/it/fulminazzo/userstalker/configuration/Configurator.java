package it.fulminazzo.userstalker.configuration;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A builder for {@link FileConfiguration}.
 */
@NoArgsConstructor
public final class Configurator {

    private @Nullable File pluginDirectory;
    
    private @Nullable String name;

    private @Nullable ConfigurationType type = ConfigurationType.YAML;

    private @Nullable Consumer<FileConfiguration> onCreated;

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

        String fullName = type.getCompleteFileName(name);

        File configFile = new File(pluginDirectory, fullName);

        if (!configFile.exists())
            try {
                FileUtils.createNewFile(configFile);
                @NotNull Optional<InputStream> jarResource = getJarResource();
                if (jarResource.isPresent())
                    FileUtils.writeToFile(configFile, jarResource.get());
            } catch (IOException e) {
                throw new ConfigurationException(e.getMessage());
            }

        return FileConfiguration.newConfiguration(configFile);
    }

    /**
     * Checks the current JAR from the file fullname.
     *
     * @return an optional that might contain
     * @throws ConfigurationException if any error occurs
     */
    @NotNull Optional<InputStream> getJarResource() throws ConfigurationException {
        String fullName = getType().getCompleteFileName(getName());
        return Optional.ofNullable(Configurator.class.getResourceAsStream("/" + fullName));
    }

    /**
     * Specifies a function to execute when creating the configuration file for the first time.
     *
     * @param function the function
     * @return this configurator
     */
    public @NotNull Configurator onCreated(final @Nullable Consumer<FileConfiguration> function) {
        this.onCreated = function;
        return this;
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
