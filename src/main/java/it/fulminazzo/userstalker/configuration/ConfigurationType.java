package it.fulminazzo.userstalker.configuration;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * The type of configuration to create in {@link Configurator}.
 * Each type has a file extension associated to it.
 */
@RequiredArgsConstructor
public enum ConfigurationType {
    JSON("json"),
    TOML("toml"),
    YAML("yml"),
    XML("xml")
    ;

    private final @NotNull String extension;

    /**
     * Gets the file name with the extension of this configuration type.
     *
     * @param fileName the file name
     * @return the complete file name
     */
    public @NotNull String getCompleteFileName(final @NotNull String fileName) {
        return String.format("%s.%s", fileName, extension);
    }

}
