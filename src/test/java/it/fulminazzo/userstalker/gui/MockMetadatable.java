package it.fulminazzo.userstalker.gui;

import it.fulminazzo.yagl.Metadatable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MockMetadatable implements Metadatable {
    private final Map<String, String> variables;

    public MockMetadatable() {
        this.variables = new HashMap<>();
    }

    @Override
    public @NotNull Map<String, String> variables() {
        return variables;
    }

}
