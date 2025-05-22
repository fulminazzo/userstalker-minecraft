package it.fulminazzo.userstalker

import it.fulminazzo.userstalker.cache.MockFileConfiguration
import it.fulminazzo.yamlparser.configuration.FileConfiguration
import spock.lang.Specification

import java.util.logging.Logger

class ProfileCacheBuilderTest extends Specification {

    private final Logger logger = Logger.getLogger('TestUserStalker')

    def 'test that loadCacheType of type #type returns #expected'() {
        given:
        def file = mockConfiguration(type, true)

        and:
        def builder = new ProfileCacheBuilder(logger, file)

        when:
        def actualType = builder.loadCacheType()

        then:
        actualType == expected

        where:
        type   || expected
        'JSON' || ProfileCacheBuilder.CacheType.JSON
        'json' || ProfileCacheBuilder.CacheType.JSON
        'YAML' || ProfileCacheBuilder.CacheType.YAML
        'TOML' || ProfileCacheBuilder.CacheType.TOML
        'XML'  || ProfileCacheBuilder.CacheType.XML
        null   || ProfileCacheBuilder.CacheType.JSON
    }

    def 'test that loadCacheType with no section returns JSON'() {
        given:
        def file = mockConfiguration(null, false)

        and:
        def builder = new ProfileCacheBuilder(logger, file)

        when:
        def actualType = builder.loadCacheType()

        then:
        actualType == ProfileCacheBuilder.CacheType.JSON
    }

    def 'test that loadCacheType of invalid throws'() {
        given:
        def file = mockConfiguration('not-valid', true)

        and:
        def builder = new ProfileCacheBuilder(logger, file)

        when:
        builder.loadCacheType()

        then:
        thrown(IllegalArgumentException)
    }

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
        if (section) map['skin-cache'] = [
                'type': type
        ]
        return new MockFileConfiguration(map)
    }

}
