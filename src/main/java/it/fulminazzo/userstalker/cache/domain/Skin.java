package it.fulminazzo.userstalker.cache.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Represents all the information required about a skin.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class Skin {

    private UUID uuid;

    private String username;

    private String skin;

    private String signature;

}
