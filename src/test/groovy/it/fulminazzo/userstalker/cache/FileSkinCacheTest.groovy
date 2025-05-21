package it.fulminazzo.userstalker.cache

import it.fulminazzo.yamlparser.utils.FileUtils
import spock.lang.Specification

class FileSkinCacheTest extends Specification {
    private File cacheFile

    private FileSkinCache skinCache

    void setup() {
        cacheFile = new File('build/resources/test/cache.json')
        if (cacheFile.exists()) cacheFile.delete()
        FileUtils.createNewFile(cacheFile)
        cacheFile.write('Fulminazzo: MockSkin')

        skinCache = new FileSkinCache(cacheFile)
    }

    def 'test that findUserSkin returns correct value from cache'() {
        when:
        def skin = skinCache.findUserSkin('Fulminazzo')

        then:
        skin.isPresent()
        skin.get() == 'MockSkin'
    }

    def 'test that storeSkin saves correct value'() {
        when:
        skinCache.storeSkin('Alex', 'AnotherSkin')

        then:
        cacheFile.readLines() == ['Fulminazzo: MockSkin', 'Alex: AnotherSkin']
    }

}
