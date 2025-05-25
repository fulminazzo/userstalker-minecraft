package it.fulminazzo.userstalker.configuration;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link ConfigurationException}
 */
public class ConfigurationException extends Exception {

    /**
     * Instantiates a new Configuration exception.
     *
     * @param message the message
     */
    public ConfigurationException(final @NotNull String message) {
        super(message);
    }

}
