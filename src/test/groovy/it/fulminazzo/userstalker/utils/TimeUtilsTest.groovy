package it.fulminazzo.userstalker.utils

import spock.lang.Specification

import java.time.LocalDateTime

class TimeUtilsTest extends Specification {

    def 'test that toString format is correct'() {
        given:
        def dateTime = LocalDateTime.of(2025, 5, 1, 6, 5, 2, 111)

        when:
        def result = TimeUtils.toString(dateTime)

        then:
        result == '06:05:02 01/05/2025'
    }

}
