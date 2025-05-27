package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.contents.GUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * A class responsible for creating and displaying all the plugin 
 */
@Builder(builderMethodName = "internalBuilder", access = AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class USGUIManager {

    private final @NotNull Logger logger;
    private final @NotNull USAsyncApiClient client;

    private final @Nullable ProfileCache cache;

    private final @NotNull DataGUI<UserLoginCount> topUsersLoginsGUI;
    private final @NotNull GUIContent topUsersLoginsGUIContent;

    private final @NotNull DataGUI<UserLoginCount> monthlyUsersLoginsGUI;
    private final @NotNull GUIContent monthlyUsersLoginsGUIContent;

    private final @NotNull DataGUI<UserLogin> newestUsersLoginsGUI;
    private final @NotNull GUIContent newestUsersLoginsGUIContent;

    private final @NotNull DataGUI<UserLogin> userLoginsGUI;
    private final @NotNull GUIContent userLoginsGUIContent;

    private final @Nullable GUIContent backGUIContent;
    private final int backGUIContentSlotOffset;

    // getTopUserLoginsAndThen
    // getMonthlyUserLoginsAndThen
    // getNewestUserLoginsAndThen
    // getUserLoginsAndThen

}
