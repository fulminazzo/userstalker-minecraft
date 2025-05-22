package it.fulminazzo.userstalker

import it.fulminazzo.yamlparser.configuration.ConfigurationSection
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

    private FileConfiguration mockConfiguration(String type, boolean section) {
        FileConfiguration configuration = Spy()
        if (section) {
            ConfigurationSection skinSection = Spy()
            skinSection.getString('type') >> type
            configuration.getConfigurationSection('skin-cache') >> skinSection
        }
        return configuration
    }

}
