package it.fulminazzo.userstalker;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import lombok.RequiredArgsConstructor;

/**
 * A helper class to create a {@link ProfileCache} from
 * the given configuration.
 */
@RequiredArgsConstructor
public final class ProfileCacheBuilder {
    private final FileConfiguration configuration;

    private enum CacheType {
        YAML,
        JSON,
        TOML,
        XML,
        DATABASE
    }

}
