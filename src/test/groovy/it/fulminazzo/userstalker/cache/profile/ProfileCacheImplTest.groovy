package it.fulminazzo.userstalker.cache.profile

import com.google.gson.JsonObject
import it.fulminazzo.fulmicollection.objects.Refl
import it.fulminazzo.userstalker.cache.utils.HttpUtils
import it.fulminazzo.userstalker.utils.GsonUtils
import spock.lang.Specification

class ProfileCacheImplTest extends Specification {

    private ProfileCacheImpl cache

    void setup() {
        cache = new TestProfileCache()
    }

    def 'test that getUserSkin does not store skin if empty response after lookup'() {
        given:
        def skinCache = Spy(TestProfileCache)
        skinCache.fetchUserSkin(_ as String) >> Optional.empty()

        when:
        skinCache.getUserSkin('user')

        and:
        def stored = skinCache.skinCache.get('user')

        then:
        stored == null
    }

    def 'test that fetchUserSkin of valid username returns expected value'() {
        when:
        def skin = cache.fetchUserSkin('Notch')

        then:
        skin.isPresent()
    }

    def 'test that fetchUserSkin of not existing player returns empty'() {
        when:
        def skin = cache.fetchUserSkin('NotExistingAtAll')

        then:
        !skin.isPresent()
    }

    def 'test that fetchUserSkin of #jsonObject is as expected'() {
        given:
        def skinCache = Spy(TestProfileCache)

        and:
        SpyStatic(HttpUtils)
        HttpUtils.getJsonFromURL(_ as String, _ as String, _) >> Optional.of(jsonObject)

        when:
        def skin = skinCache.fetchUserSkin('Notch')

        then:
        skin.isPresent() == expected

        where:
        jsonObject                                                                    || expected
        createData()                                                                  || false
        createData(new Object(), ['name': 'second'])                                  || false
        createData(new Object(), ['name': 'textures', 'value': 'skin'], new Object()) || true
    }

    def 'test that fetchUserSkin of not existing is put in blacklist'() {
        given:
        def username = 'NotExistingAtAll'

        when:
        cache.fetchUserSkin(username)
        def second = cache.fetchUserSkin(username)

        then:
        fetchBlacklist().containsKey(username)
        !second.isPresent()
    }

    def 'test that fetchUserSkin of not existing with found UUID is put in blacklist'() {
        given:
        def username = 'NotExistingAtAll'
        def blacklist = false

        and:
        def cache = Spy(TestProfileCache)
        cache.getUserUUID(_ as String) >> Optional.of(UUID.randomUUID())
        HttpUtils.getJsonFromURL(_ as String, _ as String) >> Optional.empty()
        cache.isInFetchBlacklist(_ as String) >> { args ->
            return blacklist
        }
        cache.updateFetchBlacklist(_ as String) >> { args ->
            blacklist = true
        }

        when:
        cache.fetchUserSkin(username)
        def second = cache.fetchUserSkin(username)

        then:
        !second.isPresent()
    }

    def 'test that getUserUUID does not store skin if empty response after lookup'() {
        given:
        def skinCache = Spy(TestProfileCache)
        skinCache.fetchUserUUID(_ as String) >> Optional.empty()

        when:
        skinCache.getUserUUID('user')

        and:
        def stored = skinCache.uuidCache.get('user')

        then:
        stored == null
    }

    def 'test that fetchUserUUID of valid username returns expected value'() {
        when:
        def uuid = cache.fetchUserUUID('Notch')

        then:
        uuid.isPresent()
        uuid.get() == UUID.fromString('069a79f4-44e9-4726-a5be-fca90e38aaf5')
    }

    def 'test that fetchUserUUID of not existing is put in blacklist'() {
        given:
        def username = 'NotExistingAtAll'

        when:
        cache.fetchUserUUID(username)
        def second = cache.fetchUserUUID(username)

        then:
        fetchBlacklist().containsKey(username)
        !second.isPresent()
    }

    def 'test that isInFetchBlacklist returns #expected for #time'() {
        given:
        def username = 'Fulminazzo'

        when:
        if (time != null) fetchBlacklist().put(username, time)

        then:
        cache.isInFetchBlacklist(username) == expected

        where:
        time                               || expected
        System.currentTimeMillis() - 20000 || false
        null                               || false
        System.currentTimeMillis() + 20000 || true
    }

    def 'test that close does nothing'() {
        when:
        cache.close()

        then:
        noExceptionThrown()
    }

    private Map<String, Long> fetchBlacklist() {
        return new Refl<>(cache).getFieldObject('fetchBlacklist')
    }

    private static JsonObject createData(Object... data) {
        def gson = GsonUtils.gson
        def raw = gson.toJson([
                'id'        : UUID.randomUUID().toString().replace('-', ''),
                'properties': data
        ])
        return gson.fromJson(raw, JsonObject)
    }

}
