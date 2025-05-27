package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.userstalker.builder.LoggedBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * A builder to create {@link USGUIManager} instances.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USGUIManagerBuilder extends LoggedBuilder<USGUIManager, USGUIManagerBuilder, ConfigurationException> {

    @Override
    public @NotNull USGUIManager build() throws ConfigurationException {
        return null;
    }

    @Override
    protected @NotNull ConfigurationException newException(@NotNull String message) {
        return new ConfigurationException(message);
    }

}
