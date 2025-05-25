package it.fulminazzo.userstalker.configuration

import spock.lang.Specification

class ConfiguratorTest extends Specification {

    def 'test that configurator throws if missing field'() {
        when:
        new Configurator().
                pluginDirectory(pluginDirectory)
                .name(name)
                .type(type)
                .build()

        then:
        thrown(ConfigurationException)

        where:
        pluginDirectory                  | name     | type
        null                             | 'config' | ConfigurationType.YAML
        new File('build/resources/test') | null     | ConfigurationType.YAML
        new File('build/resources/test') | 'config' | null
    }

}
