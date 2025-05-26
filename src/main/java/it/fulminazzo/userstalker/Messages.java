package it.fulminazzo.userstalker;

import it.fulminazzo.fulmicommands.FulmiMessagesPlugin;
import it.fulminazzo.fulmicommands.messages.DefaultFulmiMessages;
import it.fulminazzo.yagl.utils.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * The messages used throughout the plugin.
 */
@Getter
@RequiredArgsConstructor
public enum Messages implements DefaultFulmiMessages {
    PREFIX("prefix", "&6User&cStalker &8» &f")
    ;

    private final String path;
    private final String defaultMessage;


    @Override
    public @NotNull String getMessage() {
        return MessageUtils.color(DefaultFulmiMessages.super.getMessage());
    }

    @Override
    public @NotNull String prefix() {
        return PREFIX.getUnparsedMessage().replace("<path>", path);
    }

    @Override
    public @NotNull String fallbackMessage() {
        return "&cCould not load message at path: &4<path>";
    }

    @Override
    public @NotNull FulmiMessagesPlugin getPlugin() {
        return UserStalker.getInstance();
    }

}
