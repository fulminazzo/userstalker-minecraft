package it.fulminazzo.userstalker.cache.utils

import it.fulminazzo.userstalker.cache.exception.CacheException
import spock.lang.Specification

class HttpUtilsTest extends Specification {
    private static final String ACTION = 'searching for userstalker repository'

    def 'test that getJsonFromURL returns valid Json'() {
        given:
        def url = 'https://api.github.com/repos/fulminazzo/userstalker'

        when:
        def json = HttpUtils.getJsonFromURL(url, ACTION).get()

        then:
        json.get('name').asString == 'userstalker'
        json.get('full_name').asString == 'fulminazzo/userstalker'
        json.get('private').asString == 'false'
    }

    def 'test that getJsonFromURL returns empty optional on 404'() {
        given:
        def url = 'https://api.github.com/repos/fulminazzo/notexisting'

        when:
        def json = HttpUtils.getJsonFromURL(url, ACTION)

        then:
        !json.isPresent()
    }

    def 'test that getJsonFromURL with invalid URL throws'() {
        when:
        HttpUtils.getJsonFromURL('invalid', ACTION)

        then:
        def e = thrown(CacheException)
        e.message == 'Invalid URL provided: invalid'
    }

    def 'test that getJsonFromURL with general error throws'() {
        when:
        HttpUtils.getJsonFromURL('http://localhost', ACTION)

        then:
        def e = thrown(CacheException)
        e.message == "ConnectException when $ACTION: Connection refused (Connection refused)"
    }

    def 'test that getJsonFromURL with invalid code throws'() {
        when:
        HttpUtils.getJsonFromURL('https://sessionserver.mojang.com/session/minecraft/profile/aa', ACTION)

        then:
        def e = thrown(CacheException)
        e.message == "Invalid response code when $ACTION: 400"
    }

}
