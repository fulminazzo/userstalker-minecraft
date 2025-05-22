package it.fulminazzo.userstalker.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents the number of time a user has logged in the server.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class UserLoginCount {

    private String username;

    private long loginCount;

}
