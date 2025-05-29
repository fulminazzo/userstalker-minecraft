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
import java.util.logging.Logger;

/**
 * An implementation of {@link IPCache} that uses a {@link ConcurrentHashMap} as cache.
 */
class IPCacheImpl implements IPCache {
    private static final String IP_LOOKUP = "http://ip-api.com/json/%s";

    private final @NotNull Map<String, IPInfo> cache;
    private final @NotNull Logger logger;

    /**
     * Instantiates a new Ip cache.
     */
    public IPCacheImpl(@NotNull Logger logger) {
        this.logger = logger;
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public void lookupIPInfoAnd(@NotNull String ip,
                                @NotNull Consumer<IPInfo> then,
                                @Nullable Runnable orElse) {
        try {
            @NotNull Optional<IPInfo> info = lookupIPInfo(ip);
            if (!info.isPresent()) info = fetchIPInfo(ip);
            then.accept(info.orElse(null));
        } catch (CacheException e) {
            logger.warning(e.getMessage());
            if (orElse != null) orElse.run();
        }
    }

    /**
     * Fetches the IP information online.
     *
     * @param ip the IP
     * @return an optional that might contain the IP information (if found)
     * @throws CacheException a wrapper for any exception
     */
    public @NotNull Optional<IPInfo> fetchIPInfo(@NotNull String ip) throws CacheException {
        Optional<IPInfo> ipInfo = HttpUtils.getJsonFromURL(
                String.format(IP_LOOKUP, ip),
                String.format("querying ip-api.com for IP \"%s\"", ip)
        ).map(jsonObject -> {
            String status = jsonObject.get("status").getAsString();
            if (!status.equals("success")) return null;
            return IPInfo.builder()
                    .ip(jsonObject.get("query").getAsString())
                    .country(jsonObject.get("country").getAsString())
                    .countryCode(jsonObject.get("countryCode").getAsString())
                    .region(jsonObject.get("regionName").getAsString())
                    .city(jsonObject.get("city").getAsString())
                    .isp(jsonObject.get("isp").getAsString())
                    .build();
        });
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
