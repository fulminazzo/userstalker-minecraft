package it.fulminazzo.userstalker.cache

import spock.lang.Specification

class SkinCacheImplTest extends Specification {
    private static final String ACTION = 'searching for userstalker repository'

    private SkinCacheImpl skinCache

    void setup() {
        skinCache = new TestSkinCache()
    }

    def 'test that getJsonFromURL returns valid Json'() {
        given:
        def url = 'https://api.github.com/repos/fulminazzo/userstalker'

        when:
        def json = skinCache.getJsonFromURL(url, ACTION).get()

        then:
        json.get('name').asString == 'userstalker'
        json.get('full_name').asString == 'fulminazzo/userstalker'
        json.get('private').asString == 'false'
    }

    def 'test that getJsonFromURL returns empty optional on 404'() {
        given:
        def url = 'https://api.github.com/repos/fulminazzo/notexisting'

        when:
        def json = skinCache.getJsonFromURL(url, ACTION)

        then:
        !json.isPresent()
    }

    def 'test that getJsonFromURL with invalid URL throws'() {
        when:
        skinCache.getJsonFromURL('invalid', ACTION)

        then:
        def e = thrown(SkinCacheException)
        e.message == 'Invalid URL provided: invalid'
    }

    def 'test that getJsonFromURL with general error throws'() {
        when:
        skinCache.getJsonFromURL('http://localhost', ACTION)

        then:
        def e = thrown(SkinCacheException)
        e.message == "IOException when $ACTION: Connection refused (Connection refused)"
    }

    def 'test that getJsonFromURL with invalid code throws'() {
        when:
        skinCache.getJsonFromURL('https://sessionserver.mojang.com/session/minecraft/profile/aa', ACTION)

        then:
        def e = thrown(SkinCacheException)
        e.message == "Invalid response code when $ACTION: 400"
    }

}
