package it.fulminazzo.userstalker.cache

import com.google.gson.Gson
import com.google.gson.JsonObject
import spock.lang.Specification

class ProfileCacheImplTest extends Specification {
    private static final String ACTION = 'searching for userstalker repository'

    private ProfileCacheImpl cache

    void setup() {
        cache = new TestProfileCache()
    }

    def 'test that getUserSkin does not store skin if empty response after lookup'() {
        given:
        def skinCache = Spy(TestProfileCache)
        skinCache.lookupUserSkin(_ as String) >> Optional.empty()

        when:
        skinCache.getUserSkin('user')

        and:
        def stored = skinCache.skinCache.get('user')

        then:
        stored == null
    }

    def 'test that lookupUserSkin of valid username returns expected value'() {
        when:
        def skin = cache.lookupUserSkin('Notch')

        then:
        skin.isPresent()
    }

    def 'test that lookupUserSkin of not existing player returns empty'() {
        when:
        def skin = cache.lookupUserSkin('NotExistingAtAll')

        then:
        !skin.isPresent()
    }

    def 'test that lookupUserSkin of #jsonObject is as expected'() {
        given:
        def skinCache = Spy(TestProfileCache)
        skinCache.getJsonFromURL(_ as String, _ as String) >> Optional.of(jsonObject)

        when:
        def skin = skinCache.lookupUserSkin('Notch')

        then:
        skin.isPresent() == expected

        where:
        jsonObject                                                                    || expected
        createData()                                                                  || false
        createData(new Object(), ['name': 'second'])                                  || false
        createData(new Object(), ['name': 'textures', 'value': 'skin'], new Object()) || true
    }

    def 'test that getUserUUID does not store skin if empty response after lookup'() {
        given:
        def skinCache = Spy(TestProfileCache)
        skinCache.lookupUserUUID(_ as String) >> Optional.empty()

        when:
        skinCache.getUserUUID('user')

        and:
        def stored = skinCache.uuidCache.get('user')

        then:
        stored == null
    }

    def 'test that lookupUserUUID of valid username returns expected value'() {
        when:
        def uuid = cache.lookupUserUUID('Notch')

        then:
        uuid.isPresent()
        uuid.get() == UUID.fromString('069a79f4-44e9-4726-a5be-fca90e38aaf5')
    }

    def 'test that getJsonFromURL returns valid Json'() {
        given:
        def url = 'https://api.github.com/repos/fulminazzo/userstalker'

        when:
        def json = cache.getJsonFromURL(url, ACTION).get()

        then:
        json.get('name').asString == 'userstalker'
        json.get('full_name').asString == 'fulminazzo/userstalker'
        json.get('private').asString == 'false'
    }

    def 'test that getJsonFromURL returns empty optional on 404'() {
        given:
        def url = 'https://api.github.com/repos/fulminazzo/notexisting'

        when:
        def json = cache.getJsonFromURL(url, ACTION)

        then:
        !json.isPresent()
    }

    def 'test that getJsonFromURL with invalid URL throws'() {
        when:
        cache.getJsonFromURL('invalid', ACTION)

        then:
        def e = thrown(ProfileCacheException)
        e.message == 'Invalid URL provided: invalid'
    }

    def 'test that getJsonFromURL with general error throws'() {
        when:
        cache.getJsonFromURL('http://localhost', ACTION)

        then:
        def e = thrown(ProfileCacheException)
        e.message == "IOException when $ACTION: Connection refused (Connection refused)"
    }

    def 'test that getJsonFromURL with invalid code throws'() {
        when:
        cache.getJsonFromURL('https://sessionserver.mojang.com/session/minecraft/profile/aa', ACTION)

        then:
        def e = thrown(ProfileCacheException)
        e.message == "Invalid response code when $ACTION: 400"
    }

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

    def 'test that close does nothing'() {
        when:
        cache.close()

        then:
        noExceptionThrown()
    }

    private static JsonObject createData(Object... data) {
        Gson gson = new Gson()
        String raw = gson.toJson([
                'id'        : UUID.randomUUID().toString().replace('-', ''),
                'properties': data
        ])
        return gson.fromJson(raw, JsonObject)
    }

}
