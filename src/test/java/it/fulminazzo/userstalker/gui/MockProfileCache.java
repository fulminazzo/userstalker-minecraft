package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.cache.profile.ProfileCache;
import it.fulminazzo.userstalker.cache.exception.CacheException;
import it.fulminazzo.userstalker.cache.domain.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class MockProfileCache implements ProfileCache {

    @Override
    public @NotNull Optional<Skin> getUserSkin(@NotNull String username) throws CacheException {
        if (username.equals("valid")) return Optional.of(Skin.builder()
                .uuid(UUID.randomUUID())
                .username("valid")
                .skin("skin")
                .signature("signature")
                .build());
        else if (username.equals("error")) throw new CacheException("error");
        else return Optional.empty();
    }

    @Override
    public @NotNull Optional<Skin> fetchUserSkin(@NotNull String username) throws CacheException {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<Skin> lookupUserSkin(@NotNull String username) throws CacheException {
        return Optional.empty();
    }

    @Override
    public void storeUserSkin(@NotNull Skin skin) throws CacheException {

    }

    @Override
    public @NotNull Optional<UUID> getUserUUID(@NotNull String username) throws CacheException {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<UUID> fetchUserUUID(@NotNull String username) throws CacheException {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<UUID> lookupUserUUID(@NotNull String username) throws CacheException {
        return Optional.empty();
    }

    @Override
    public void storeUserUUID(@NotNull String username, @NotNull UUID uuid) throws CacheException {

    }

    @Override
    public void close() throws CacheException {
        throw new CacheException("Not implemented yet");
    }

}
