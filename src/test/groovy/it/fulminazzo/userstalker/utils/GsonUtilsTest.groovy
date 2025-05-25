package it.fulminazzo.userstalker.utils

import spock.lang.Specification

import java.time.LocalDateTime

class GsonUtilsTest extends Specification {

    def 'test that gson can convert LocalDateTime to Json'() {
        given:
        def localDateTime = LocalDateTime.of(2025, 5, 25, 21, 48)

        and:
        def expected = '"2025-05-25 21:48:00"'

        when:
        def actual = GsonUtils.gson.toJson(localDateTime)

        then:
        actual == expected
    }

    def 'test that gson can convert Json to LocalDateTime'() {
        given:
        def json = '"2025-05-25 21:48:00"'

        and:
        def expected = LocalDateTime.of(2025, 5, 25, 21, 48)

        when:
        def actual = GsonUtils.gson.fromJson(json, LocalDateTime)

        then:
        actual == expected
    }

}
