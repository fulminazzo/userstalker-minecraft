package it.fulminazzo.userstalker.configuration

import spock.lang.Specification

class ConfigurationTypeTest extends Specification {

    def 'test that #configurationType returns #expected with getCompleteFileName'() {
        given:
        def fileName = 'file'

        when:
        def completeName = configurationType.getCompleteFileName(fileName)

        then:
        completeName == expected

        where:
        configurationType      || expected
        ConfigurationType.JSON || 'file.json'
        ConfigurationType.TOML || 'file.toml'
        ConfigurationType.YAML || 'file.yml'
        ConfigurationType.XML  || 'file.xml'
    }

}
