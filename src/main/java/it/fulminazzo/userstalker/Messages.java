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
    IP_INFO("general.ip-info", "&fIp: &b<ip>\n" +
            "&fCountry: &c<country>\n" +
            "&fCountry code: &c<country_code>\n" +
            "&fRegion: &e<region>\n" +
            "&fCity: &a<city>\n" +
            "&fISP: &9<isp>\n" +
            "&fMobile: <mobile>\n" +
            "&fProxy: <proxy>"),
    YES("general.yes", "&aYes"),
    NO("general.no", "&cNo"),

    INTERNAL_ERROR_OCCURRED("error.internal-error", "<prefix>&cAn internal error occurred. " +
            "Please notify an administrator about this"),
    CONSOLE_CANNOT_EXECUTE("error.console-cannot-execute", "<prefix>&cConsole cannot execute this command."),
    RELOAD_UNSUCCESSFUL("general.reload-unsuccessful", "<prefix>&cPlugin reload failed with error: &4<error>&c. " +
            "Check console for more details"),
    SUBCOMMAND_NOT_FOUND("error.subcommand-not-found", "<prefix>&cNo such subcommand: &4<subcommand>"),
    NOT_ENOUGH_ARGUMENTS("error.not-enough-arguments", "<prefix>&cYou did not specify enough arguments"),
    NOT_ENOUGH_PERMISSIONS("error.not-enough-permissions", "<prefix>&cYou do not have enough permissions to execute that subcommand"),
    USER_NOT_FOUND("error.user-not-found", "<prefix>&cCould not find user: &4<user>" ),
    INVALID_IP("error.invalid-ip", "<prefix>&cInvalid IP: &4<ip>"),

    HELP_RELOAD("help.reload", "Reloads the plugin configurations"),
    HELP_LOOKUP("help.lookup", "Fetches information for the given IP"),
    HELP_OPEN_GUI("help.open-gui", "Opens the main plugin GUI. If a username is specified, opens the user latest accesses"),
    HELP_HELP("help.help", "Shows descriptions for the requested subcommands"),

    HELP_DESCRIPTION("help.description", "&c/<command> <name> <usage>&8- &e<description>")

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
