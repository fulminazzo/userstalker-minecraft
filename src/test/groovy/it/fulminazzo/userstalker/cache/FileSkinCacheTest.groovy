package it.fulminazzo.userstalker.cache

import it.fulminazzo.yamlparser.configuration.FileConfiguration
import it.fulminazzo.yamlparser.utils.FileUtils
import spock.lang.Specification

class FileSkinCacheTest extends Specification {
    private File cacheFile
    private FileConfiguration config

    private FileSkinCache skinCache

    void setup() {
        cacheFile = new File('build/resources/test/cache.json')
        if (cacheFile.exists()) cacheFile.delete()
        FileUtils.createNewFile(cacheFile)

        def now = new Date().getTime()

        config = FileConfiguration.newConfiguration(cacheFile)
        config.set('expired.skin', 'skin')
        config.set('expired.expiry', now)

        config.set('not-expired.skin', 'skin')
        config.set('not-expired.expiry', now + 100 * 1000)

        config.set('not-specified.skin', 'skin')
        config.save()

        skinCache = new FileSkinCache(cacheFile, 100 * 1000)
    }

    def 'test that findUserSkin of #username is as expected'() {
        when:
        def skin = skinCache.findUserSkin(username)

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
        skinCache.storeSkin('Alex', 'AnotherSkin')
        config = FileConfiguration.newConfiguration(cacheFile)

        then:
        config.getConfigurationSection('Alex') != null
        config.getString('Alex.skin') == 'AnotherSkin'
        config.getLong('Alex.expiry') > new Date().getTime()
    }

}
