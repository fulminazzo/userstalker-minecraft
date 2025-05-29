package it.fulminazzo.userstalker.cache.ip;

import it.fulminazzo.userstalker.cache.domain.IPInfo;
import it.fulminazzo.userstalker.cache.exception.CacheException;
import it.fulminazzo.userstalker.cache.utils.HttpUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * An implementation of {@link IPCache} that uses a {@link ConcurrentHashMap} as cache.
 */
class IPCacheImpl implements IPCache {
    private static final String IP_LOOKUP = "http://ip-api.com/json/%s";

    private final Map<String, IPInfo> cache;

    /**
     * Instantiates a new Ip cache.
     */
    public IPCacheImpl() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public void lookupIPInfoAnd(@NotNull String ip,
                                @NotNull Consumer<IPInfo> then,
                                @Nullable Runnable orElse) {

    }

    /**
     * Fetches the IP information online.
     *
     * @param ip the IP
     * @return an optional that might contain the IP information (if found)
     * @throws CacheException a wrapper for any exception
     */
    public @NotNull Optional<IPInfo> fetchIPInfo(@NotNull String ip) throws CacheException {
        Optional<IPInfo> ipInfo = HttpUtils.getJsonFromURL(String.format(IP_LOOKUP, ip), String.format("querying ip-api.com for IP \"%s\"", ip));
        ipInfo.ifPresent(info -> cache.put(ip, info));
        return ipInfo;
    }

    /**
     * Searches the IP information in the current cache.
     *
     * @param ip the IP
     * @return an optional that might contain the IP information (if previously cached)
     */
    public @NotNull Optional<IPInfo> lookupIPInfo(@NotNull String ip) {
        return Optional.ofNullable(cache.get(ip));
    }

}
