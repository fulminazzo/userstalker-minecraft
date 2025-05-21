package it.fulminazzo.userstalker.cache

import spock.lang.Specification

class SkinCacheImplTest extends Specification {
    private SkinCacheImpl skinCache

    void setup() {
        skinCache = new TestSkinCache()
    }

}
