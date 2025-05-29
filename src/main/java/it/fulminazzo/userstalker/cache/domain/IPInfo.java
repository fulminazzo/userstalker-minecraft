package it.fulminazzo.userstalker.cache.domain;

import lombok.*;

/**
 * Represents all the information about a given IP.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Builder
public final class IPInfo {

    private final String ip;

    private final String country;
    private final String countryCode;

    private final String region;

    private final String city;

    private final String isp;

    private final boolean mobile;
    private final boolean proxy;

}
