package it.fulminazzo.userstalker.cache.ip;

import it.fulminazzo.userstalker.cache.domain.IPInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * A cache that stores information about an IP address lookup.
 */
public interface IPCache {

    /**
     * Looks up the IP address and executes the given function.
     *
     * @param ip     the ip address
     * @param then   the function to execute in case it was found
     * @param orElse the function to execute in case an error occurred
     */
    void lookupIPInfoAnd(final @NotNull String ip,
                         final @NotNull Consumer<IPInfo> then,
                         final @Nullable Runnable orElse);

    /**
     * Creates a new IP cache.
     *
     * @param logger the logger
     * @return the ip cache
     */
    static @NotNull IPCache newCache(final @NotNull Logger logger) {
        return new IPCacheImpl(logger);
    }

}
