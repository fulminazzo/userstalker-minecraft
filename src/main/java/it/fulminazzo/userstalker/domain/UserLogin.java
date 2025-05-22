package it.fulminazzo.userstalker.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents the access on the server by the user.
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class UserLogin {

    private String username;

    private String ip;

    private LocalDateTime loginDate;

}
