package it.fulminazzo.userstalker.domain;

import lombok.*;

/**
 * Represents the number of time a user has logged in the server.
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class UserLoginCount {

    private String username;

    private long loginCount;

}
