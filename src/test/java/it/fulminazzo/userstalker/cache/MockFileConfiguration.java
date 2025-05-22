package it.fulminazzo.userstalker.cache;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

public class MockFileConfiguration extends FileConfiguration {
    private final @NotNull Map<?, ?> data;

    public MockFileConfiguration(final @NotNull Map<?, ?> data) {
        super("build/resources/main/config.yml");
        this.data = data;
    }

    @Override
    protected Map<?, ?> load(@NotNull InputStream stream) {
        return data;
    }

    @Override
    protected void dump(@NotNull Map<?, ?> data, @NotNull Writer writer) {
        throw new UnsupportedOperationException();
    }

}
