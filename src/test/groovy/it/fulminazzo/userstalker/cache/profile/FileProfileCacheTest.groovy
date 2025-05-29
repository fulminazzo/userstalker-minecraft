package it.fulminazzo.userstalker.cache.profile

import it.fulminazzo.userstalker.cache.domain.Skin
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

        def now = System.currentTimeMillis()

        config = FileConfiguration.newConfiguration(cacheFile)
        config.set('expired.uuid', UUID.randomUUID())
        config.set('expired.username', 'Steve')
        config.set('expired.skin', 'skin')
        config.set('expired.signature', 'signature')
        config.set('expired.expiry', now)

        config.set('not-expired.uuid', UUID.randomUUID())
        config.set('not-expired.username', 'Fulminazzo')
        config.set('not-expired.skin', 'skin')
        config.set('not-expired.signature', 'signature')
        config.set('not-expired.expiry', now + 100 * 1000)

        config.set('not-specified.username', 'Joey')
        config.set('not-specified.skin', 'skin')
        config.set('not-specified.signature', 'signature')
        config.save()

        cache = new FileProfileCache(cacheFile, 100 * 1000, 0)
    }

    def 'test that getUserSkin of non cached queries Mojang API'() {
        given:
        def username = 'Notch'
        def skin = 'Skin'

        when:
        def actualSkin = cache.getUserSkin(username)

        then:
        actualSkin.isPresent()
        actualSkin.get().skin != skin
    }

    def 'test that getUserSkin of cached does not query Mojang API'() {
        given:
        def username = 'Notch'
        def skin = 'Skin'

        and:
        config.set('Notch.skin', skin)
        config.set('Notch.expiry', System.currentTimeMillis() + 100 * 1000)
        config.save()

        and:
        cache = new FileProfileCache(cacheFile, 100 * 1000, 0L)

        when:
        def actualSkin = cache.getUserSkin(username)

        then:
        actualSkin.isPresent()
        actualSkin.get().skin == skin
    }

    def 'test that lookupUserSkin of #username is as expected'() {
        when:
        def skin = cache.lookupUserSkin(username)

        then:
        skin.isPresent() == expected

        where:
        username        || expected
        'expired'       || false
        'not-expired'   || true
        'not-specified' || false
        'not-existing'  || false
    }

    def 'test that storeUserSkin saves correct value'() {
        given:
        def skin = Skin.builder()
                .uuid(UUID.randomUUID())
                .username('Alex')
                .skin('AnotherSkin')
                .signature('')
                .build()

        when:
        cache.storeUserSkin(skin)
        config = FileConfiguration.newConfiguration(cacheFile)

        then:
        config.getUUID('Alex.uuid') == skin.uuid
        config.getString('Alex.username') == skin.username
        config.getString('Alex.skin') == skin.skin
        config.getString('Alex.signature') == skin.signature
        config.getLong('Alex.expiry') > System.currentTimeMillis()
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
        config.set('Notch.uuid', uuid)
        config.set('Notch.expiry', System.currentTimeMillis() + 100 * 1000)
        config.save()

        and:
        cache = new FileProfileCache(cacheFile, 100 * 1000, 0L)

        when:
        def actualUUID = cache.getUserUUID(username)

        then:
        actualUUID.isPresent()
        actualUUID.get() == uuid
    }

    def 'test that lookupUserUUID of username is as expected'() {
        when:
        def uuid = cache.lookupUserUUID('not-expired')

        then:
        uuid.isPresent()
    }

    def 'test that storeUserUUID saves correct value'() {
        given:
        def uuid = UUID.randomUUID()

        when:
        cache.storeUserUUID('Alex', uuid)
        config = FileConfiguration.newConfiguration(cacheFile)

        then:
        config.getConfigurationSection('Alex') != null
        config.getUUID('Alex.uuid') == uuid
    }

}
