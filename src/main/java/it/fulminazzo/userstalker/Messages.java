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
    PREFIX("prefix", "&6User&cStalker &8» &f"),

    RELOAD_SUCCESSFUL("general.reload-successful", "<prefix>&aPlugin reload completed successfully"),

    INTERNAL_ERROR_OCCURRED("error.internal-error", "<prefix>&cAn internal error occurred. " +
            "Please notify an administrator about this"),
    CONSOLE_CANNOT_EXECUTE("error.console-cannot-execute", "<prefix>&cConsole cannot execute this command."),
    RELOAD_UNSUCCESSFUL("general.reload-unsuccessful", "<prefix>&cPlugin reload failed with error: &4<error>&c. " +
            "Check console for more details"),
    SUBCOMMAND_NOT_FOUND("error.subcommand-not-found", "<prefix>&cNo such subcommand: &4<subcommand>"),

    HELP_RELOAD("help.reload", "Reloads the plugin configurations"),
    HELP_OPEN_GUI("help.open-gui", "Opens the main plugin GUI"),
    HELP_HELP("help.help", "Shows descriptions for the requested subcommands"),

    HELP_DESCRIPTION("help.description", "&c/<name> &8- &e<description>")

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
