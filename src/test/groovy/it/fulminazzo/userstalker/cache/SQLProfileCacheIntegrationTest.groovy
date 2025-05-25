package it.fulminazzo.userstalker.cache

import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Timestamp

class SQLProfileCacheIntegrationTest extends Specification {
    private static final String DB_URL = 'jdbc:h2:mem:testdb;DB_CLOSE_DELAY=0'
    private static final String DB_USER = 'sa'
    private static final String DB_PASSWORD = ''

    private Connection connection

    private SQLProfileCache cache

    void setup() {
        Class.forName('org.h2.Driver')
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)

        cache = new SQLProfileCache(connection, 100 * 1000, 0)
        cache.checkProfileTableExists()

        def statement = connection.prepareStatement('INSERT INTO profile_cache (username, uuid) VALUES (?, ?)')
        statement.setString(1, 'Steve')
        statement.setString(2, UUID.randomUUID().toString())
        statement.execute()

        statement = connection.prepareStatement('INSERT INTO profile_cache (username, uuid, skin, signature, expiry) VALUES (?, ?, ?, ?, ?)')
        statement.setString(1, 'Jeb')
        statement.setString(2, UUID.randomUUID().toString())
        statement.setString(3, 'skin')
        statement.setString(4, 'signature')
        statement.setTimestamp(5, new Timestamp(System.currentTimeMillis() + 1000000))
        statement.execute()
    }

    void cleanup() {
        connection.close()
    }

    def 'test that lookupUserSkin of #username returns correct value'() {
        given:
        def skin = Skin.builder()
                .uuid(UUID.randomUUID())
                .username(username)
                .skin('mock-skin')
                .signature('signature')
                .build()

        and:
        def statement = connection.prepareStatement('INSERT INTO profile_cache VALUES (?, ?, ?, ?, ?)')
        statement.setString(1, username)
        statement.setString(2, skin.uuid.toString())
        statement.setString(3, skin.skin)
        statement.setString(4, skin.signature)
        if (username == 'Notch')
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis() + 1000 * 100))
        else
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()))
        statement.execute()

        when:
        def actualSkin = cache.lookupUserSkin(username)

        then:
        actualSkin.isPresent() == expected

        where:
        username || expected
        'Notch'  || true
        'Alex'   || false
    }

    def 'test that storeUserSkin of #username saves correct value'() {
        given:
        def skin = Skin.builder()
                .uuid(UUID.randomUUID())
                .username(username)
                .skin('mock-skin')
                .signature('signature')
                .build()

        when:
        cache.storeUserSkin(skin)

        and:
        def resultSet = connection
                .prepareStatement("SELECT skin, signature, expiry FROM profile_cache WHERE username = '$username'")
                .executeQuery()

        then:
        resultSet.next()
        resultSet.getString(1) == skin.skin
        resultSet.getString(2) == skin.signature
        resultSet.getTimestamp(3).after(new Timestamp(System.currentTimeMillis()))

        where:
        username << ['Notch', 'Steve', 'Jeb']
    }

    def 'test that lookupUserUUID returns correct value'() {
        given:
        def username = 'Notch'
        def uuid = UUID.randomUUID()

        and:
        def statement = connection.prepareStatement('INSERT INTO profile_cache (username, uuid) VALUES (?, ?)')
        statement.setString(1, username)
        statement.setString(2, uuid.toString())
        statement.execute()

        when:
        def actualUUID = cache.lookupUserUUID(username)

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
                .prepareStatement("SELECT uuid FROM profile_cache WHERE username = '$username'")
                .executeQuery()

        then:
        resultSet.next()
        resultSet.getString(1) == uuid.toString()

        where:
        username << ['Notch', 'Steve']
    }

    def 'test that executeStatement wraps any exception in ProfileCacheException'() {
        when:
        cache.executeStatement(() -> connection.prepareStatement('INVALID'), s -> null)

        then:
        def e = thrown(ProfileCacheException)
        e.message == 'JdbcSQLSyntaxErrorException when querying database: ' +
                'Syntax error in SQL statement "[*]INVALID"; expected "INSERT"; ' +
                'SQL statement:\nINVALID [42001-224]'
    }

    def 'test that close closes connection'() {
        when:
        cache.close()

        then:
        connection.closed
    }

    def 'simulate close error'() {
        given:
        def connection = Mock(Connection)
        connection.close() >> {
            throw new SQLException('')
        }

        and:
        def cache = new SQLProfileCache(connection, 10)

        when:
        cache.close()

        then:
        def e = thrown(ProfileCacheException)
        e.message == 'SQLException when closing connection with database: '
    }

    def 'test context loads'() {
        expect:
        true
    }

}
