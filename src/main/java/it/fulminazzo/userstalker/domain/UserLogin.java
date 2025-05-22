package it.fulminazzo.userstalker.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Represents the access on the server by the user.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class UserLogin {

    private String username;

    private String ip;

    private LocalDateTime loginDate;

}
