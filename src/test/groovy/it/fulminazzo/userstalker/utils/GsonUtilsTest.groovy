package it.fulminazzo.userstalker.utils

import spock.lang.Specification

import java.time.LocalDateTime

class GsonUtilsTest extends Specification {

    def 'test that gson can convert #localDateTime to #expected'() {
        when:
        def actual = GsonUtils.gson.toJson(localDateTime, LocalDateTime)

        then:
        actual == expected

        where:
        localDateTime                         || expected
        LocalDateTime.of(2025, 5, 25, 21, 48) || '"2025-05-25 21:48:00"'
        null                                  || 'null'
    }

    def 'test that gson can convert #json to #expected'() {
        when:
        def actual = GsonUtils.gson.fromJson(json, LocalDateTime)

        then:
        actual == expected

        where:
        json                    || expected
        '"2025-05-25 21:48:00"' || LocalDateTime.of(2025, 5, 25, 21, 48)
        'null'                  || null
    }

}
