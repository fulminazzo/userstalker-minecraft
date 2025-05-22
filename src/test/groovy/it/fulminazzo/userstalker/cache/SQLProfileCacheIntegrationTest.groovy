package it.fulminazzo.userstalker.cache

import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager

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

    def 'test that storeUUID saves correct value'() {
        given:
        def username = 'Notch'
        def uuid = UUID.randomUUID()

        when:
        cache.storeUUID(username, uuid)

        and:
        def resultSet = connection
                .prepareStatement('SELECT Id FROM uuid_cache WHERE Username = Notch')
                .executeQuery()

        then:
        resultSet.next()
        resultSet.getString(1) == ProfileCacheUtils.toString(uuid)
    }

    def 'test context loads'() {
        expect:
        true
    }

}
