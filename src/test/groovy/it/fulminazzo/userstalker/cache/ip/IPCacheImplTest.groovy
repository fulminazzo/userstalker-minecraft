package it.fulminazzo.userstalker.cache.ip

import it.fulminazzo.userstalker.cache.domain.IPInfo
import spock.lang.Specification

import java.util.logging.Logger

class IPCacheImplTest extends Specification {

    private IPCacheImpl cache

    void setup() {
        cache = new IPCacheImpl(Logger.getLogger(getClass().simpleName))
    }

    def 'test that lookupIPInfoAnd runs the consumer for ip #ip'() {
        given:
        def info = IPInfo.builder()
                .ip('127.0.0.1')
                .country('Canada')
                .countryCode('CA')
                .region('Quebec')
                .city('Montreal')
                .isp('Le Groupe Videotron Ltee')
                .build()

        and:
        cache.cache.put('127.0.0.1', info)

        and:
        def run = false

        when:
        cache.lookupIPInfoAnd(ip, i -> run = true, () -> { })

        then:
        run == true

        where:
        ip << [
                '127.0.0.1',
                '24.48.0.1',
                'invalid'
        ]
    }

    def 'test that fetchIPInfo returns #expected for ip #ip'() {
        when:
        def info = cache.fetchIPInfo(ip).orElse(null)

        then:
        info == expected

        where:
        ip          || expected
        'invalid'   || null
        '24.48.0.1' || IPInfo.builder()
                .ip('24.48.0.1')
                .country('Canada')
                .countryCode('CA')
                .region('Quebec')
                .city('Montreal')
                .isp('Le Groupe Videotron Ltee')
                .build()
    }

    def 'test that lookupIPInfo returns #expected for ip #ip'() {
        given:
        def info = IPInfo.builder()
                .ip('127.0.0.1')
                .country('Canada')
                .countryCode('CA')
                .region('Quebec')
                .city('Montreal')
                .isp('Le Groupe Videotron Ltee')
                .build()

        and:
        cache.cache.put('127.0.0.1', info)

        when:
        def actualInfo = cache.lookupIPInfo(ip).orElse(null)

        then:
        actualInfo == expected

        where:
        ip          || expected
        '127.0.0.1' || IPInfo.builder()
                .ip('127.0.0.1')
                .country('Canada')
                .countryCode('CA')
                .region('Quebec')
                .city('Montreal')
                .isp('Le Groupe Videotron Ltee')
                .build()
        '127.0.0.2' || null
    }

}
