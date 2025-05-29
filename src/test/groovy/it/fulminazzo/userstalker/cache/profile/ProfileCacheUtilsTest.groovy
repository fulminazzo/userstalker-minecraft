package it.fulminazzo.userstalker.cache.profile


import spock.lang.Specification

class ProfileCacheUtilsTest extends Specification {

    def 'test that fromString returns correct value'() {
        given:
        def raw = '069a79f444e94726a5befca90e38aaf5'

        when:
        def uuid = ProfileCacheUtils.fromString(raw)

        then:
        uuid == UUID.fromString('069a79f4-44e9-4726-a5be-fca90e38aaf5')
    }

    def 'test that fromString of invalid throws'() {
        when:
        ProfileCacheUtils.fromString('mock')

        then:
        thrown(IllegalArgumentException)
    }

}
