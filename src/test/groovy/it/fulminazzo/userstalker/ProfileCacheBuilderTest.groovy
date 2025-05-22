package it.fulminazzo.userstalker

import it.fulminazzo.userstalker.cache.MockFileConfiguration
import it.fulminazzo.yamlparser.configuration.FileConfiguration
import spock.lang.Specification

class ProfileCacheBuilderTest extends Specification {

    def 'test that mock returns correct type'() {
        given:
        def type = 'test'

        and:
        def file = mockConfiguration(type, true)

        when:
        def actualType = file.getString('skin-cache.type')

        then:
        actualType == type
    }

    private static FileConfiguration mockConfiguration(String type, boolean section) {
        def map = [:]
        if (section) map['skin-cache.type'] = type
        return new MockFileConfiguration(map)
    }

}
