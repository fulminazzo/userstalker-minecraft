package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.cache.ProfileCacheException;
import it.fulminazzo.userstalker.cache.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class MockProfileCache implements ProfileCache {

    @Override
    public @NotNull Optional<Skin> getUserSkin(@NotNull String username) throws ProfileCacheException {
        if (username.equals("valid")) return Optional.of(Skin.builder()
                .uuid(UUID.randomUUID())
                .username("valid")
                .skin("skin")
                .signature("signature")
                .build());
        else if (username.equals("error")) throw new ProfileCacheException("error");
        else return Optional.empty();
    }

    @Override
    public @NotNull Optional<Skin> fetchUserSkin(@NotNull String username) throws ProfileCacheException {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<Skin> lookupUserSkin(@NotNull String username) throws ProfileCacheException {
        return Optional.empty();
    }

    @Override
    public void storeUserSkin(@NotNull Skin skin) throws ProfileCacheException {

    }

    @Override
    public @NotNull Optional<UUID> getUserUUID(@NotNull String username) throws ProfileCacheException {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<UUID> fetchUserUUID(@NotNull String username) throws ProfileCacheException {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<UUID> lookupUserUUID(@NotNull String username) throws ProfileCacheException {
        return Optional.empty();
    }

    @Override
    public void storeUserUUID(@NotNull String username, @NotNull UUID uuid) throws ProfileCacheException {

    }

    @Override
    public void close() throws ProfileCacheException {
        throw new ProfileCacheException("Not implemented yet");
    }

}
