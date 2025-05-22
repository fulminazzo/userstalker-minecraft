package it.fulminazzo.userstalker.cache

import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp

class SQLProfileCacheIntegrationTest extends Specification {
    private static final String DB_URL = 'jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1'
    private static final String DB_USER = 'sa'
    private static final String DB_PASSWORD = ''

    private Connection connection

    private SQLProfileCache cache

    void setup() {
        Class.forName('org.h2.Driver')
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)

        cache = new SQLProfileCache(connection, 100 * 1000)
    }

    def 'test that findUserUUID of #username returns correct value'() {
        given:
        def skin = 'mock-skin'

        and:
        cache.checkSkinTableExists()

        and:
        def statement = connection.prepareStatement('INSERT INTO skin_cache VALUES (?, ?, ?)')
        statement.setString(1, username)
        statement.setString(2, skin)
        if (username == 'Notch')
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis() + 1000 * 100))
        else
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()))
        statement.execute()

        when:
        def actualUUID = cache.findUserSkin(username)

        then:
        actualUUID.isPresent() == expected

        where:
        username || expected
        'Notch'  || true
        'Alex'   || false
    }

    def 'test that storeUserSkin of #username saves correct value'() {
        given:
        def skin = 'mock-skin'

        when:
        cache.storeUserSkin(username, skin)

        and:
        def resultSet = connection
                .prepareStatement("SELECT skin, expiry FROM skin_cache WHERE username = '$username'")
                .executeQuery()

        then:
        resultSet.next()
        resultSet.getString(1) == username
        resultSet.getTimestamp(2).after(new Timestamp(System.currentTimeMillis()))

        where:
        username << ['Notch', 'Steve']
    }

    def 'test that findUserUUID returns correct value'() {
        given:
        def username = 'Notch'
        def uuid = UUID.randomUUID()

        and:
        cache.checkUUIDTableExists()

        and:
        def statement = connection.prepareStatement('INSERT INTO uuid_cache VALUES (?, ?)')
        statement.setString(1, username)
        statement.setString(2, ProfileCacheUtils.toString(uuid))
        statement.execute()

        when:
        def actualUUID = cache.findUserUUID(username)

        then:
        actualUUID.isPresent()
        actualUUID.get() == uuid
    }

    def 'test that storeUserUUID of #username saves correct value'() {
        given:
        def uuid = UUID.randomUUID()

        when:
        cache.storeUserUUID(username, uuid)

        and:
        def resultSet = connection
                .prepareStatement("SELECT uuid FROM uuid_cache WHERE username = '$username'")
                .executeQuery()

        then:
        resultSet.next()
        resultSet.getString(1) == ProfileCacheUtils.toString(uuid)

        where:
        username << ['Notch', 'Steve']
    }

    def 'test context loads'() {
        expect:
        true
    }

}
