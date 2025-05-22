package it.fulminazzo.userstalker.cache

import it.fulminazzo.yamlparser.configuration.FileConfiguration
import it.fulminazzo.yamlparser.utils.FileUtils
import spock.lang.Specification

class FileProfileCacheTest extends Specification {
    private File cacheFile
    private FileConfiguration config

    private FileProfileCache cache

    void setup() {
        cacheFile = new File('build/resources/test/cache.json')
        if (cacheFile.exists()) cacheFile.delete()
        FileUtils.createNewFile(cacheFile)

        def now = new Date().getTime()

        config = FileConfiguration.newConfiguration(cacheFile)
        config.set('expired.skin', 'skin')
        config.set('expired.expiry', now)

        config.set('not-expired.skin', 'skin')
        config.set('not-expired.uuid', UUID.randomUUID().toString().replace('-', ''))
        config.set('not-expired.expiry', now + 100 * 1000)

        config.set('not-specified.skin', 'skin')
        config.save()

        cache = new FileProfileCache(cacheFile, 100 * 1000)
    }

    def 'test that getUserSkin of non cached queries Mojang API'() {
        given:
        def username = 'Notch'
        def skin = 'Skin'

        when:
        def actualSkin = cache.getUserSkin(username)

        then:
        actualSkin.isPresent()
        actualSkin.get() != skin
    }

    def 'test that getUserSkin of cached does not query Mojang API'() {
        given:
        def username = 'Notch'
        def skin = 'Skin'

        and:
        config.set('Notch.skin', skin)
        config.set('Notch.expiry', new Date().getTime() + 100 * 1000)
        config.save()

        and:
        cache = new FileProfileCache(cacheFile, 100 * 1000)

        when:
        def actualSkin = cache.getUserSkin(username)

        then:
        actualSkin.isPresent()
        actualSkin.get() == skin
    }

    def 'test that findUserSkin of #username is as expected'() {
        when:
        def skin = cache.findUserSkin(username)

        then:
        skin.isPresent() == expected

        where:
        username        || expected
        'expired'       || false
        'not-expired'   || true
        'not-specified' || false
        'not-existing'  || false
    }

    def 'test that storeSkin saves correct value'() {
        when:
        cache.storeSkin('Alex', 'AnotherSkin')
        config = FileConfiguration.newConfiguration(cacheFile)

        then:
        config.getConfigurationSection('Alex') != null
        config.getString('Alex.skin') == 'AnotherSkin'
        config.getLong('Alex.expiry') > new Date().getTime()
    }

    def 'test that getUserUUID of non cached queries Mojang API'() {
        given:
        def username = 'Notch'
        def uuid = UUID.randomUUID()

        when:
        def actualUUID = cache.getUserUUID(username)

        then:
        actualUUID.isPresent()
        actualUUID.get() != uuid
    }

    def 'test that getUserUUID of cached does not query Mojang API'() {
        given:
        def username = 'Notch'
        def uuid = UUID.randomUUID()

        and:
        config.set('Notch.uuid', uuid.toString().replace('-', ''))
        config.set('Notch.expiry', new Date().getTime() + 100 * 1000)
        config.save()

        and:
        cache = new FileProfileCache(cacheFile, 100 * 1000)

        when:
        def actualUUID = cache.getUserUUID(username)

        then:
        actualUUID.isPresent()
        actualUUID.get() == uuid
    }

    def 'test that findUserUUID of username is as expected'() {
        when:
        def uuid = cache.findUserUUID('not-expired')

        then:
        uuid.isPresent()
    }

    def 'test that storeUUID saves correct value'() {
        given:
        def uuid = UUID.randomUUID()

        when:
        cache.storeUUID('Alex', uuid)
        config = FileConfiguration.newConfiguration(cacheFile)

        then:
        config.getConfigurationSection('Alex') != null
        config.getString('Alex.uuid') == uuid.toString().replace('-', '')
    }

}
