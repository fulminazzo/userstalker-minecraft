package it.fulminazzo.userstalker.cache.profile

import it.fulminazzo.userstalker.MockFileConfiguration
import it.fulminazzo.userstalker.cache.exception.CacheException
import it.fulminazzo.yamlparser.configuration.FileConfiguration
import it.fulminazzo.yamlparser.utils.FileUtils
import org.h2.tools.Server
import spock.lang.Specification

import java.util.logging.Logger

class ProfileCacheBuilderTest extends Specification {

    private static final File PLUGIN_DIRECTORY = new File('build/resources/test/ProfileCacheBuilderTest')
    private final Logger logger = Logger.getLogger(getClass().simpleName)

    void setup() {
        if (PLUGIN_DIRECTORY.exists()) FileUtils.deleteFolder(PLUGIN_DIRECTORY)
        FileUtils.createFolder(PLUGIN_DIRECTORY)
    }

    def 'test that build builds SQLProfileCache on SQL type'() {
        given:
        def file = mockConfiguration('sql', 10, true)

        and:
        def server = Server.createTcpServer('-tcpAllowOthers', '-ifNotExists').start()

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        def cache = builder.build()

        then:
        cache instanceof SQLProfileCache
        server.stop()
    }

    def 'test that connection to invalid database throws'() {
        given:
        def file = mockConfiguration('sql', 10, 0,
                'localhost:3306', 'unknown',
                'userstalker', 'username', 'password',
                true
        )

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        builder.build()

        then:
        def e = thrown(CacheException)
        e.message == 'SQLException when connecting to database ' +
                '"jdbc:unknown://localhost:3306/userstalker" (username:password): ' +
                'No suitable driver found for jdbc:unknown://localhost:3306/userstalker'
    }

    def 'test that missing database configuration throws'() {
        given:
        def file = mockConfiguration('database', 10, 0,
                dbAddress, dbType, dbName, dbUsername, dbPassword, true
        )

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        builder.build()

        then:
        thrown(CacheException)

        where:
        dbAddress   | dbType | dbName | dbUsername | dbPassword
        null        | null   | null   | null       | null
        'localhost' | null   | null   | null       | null
        'localhost' | 'sql'  | null   | null       | null
        'localhost' | 'sql'  | 'db'   | null       | null
        'localhost' | 'sql'  | 'db'   | 'user'     | null
    }

    def 'test that build creates and then reads file with type #type'() {
        given:
        def file = mockConfiguration(type, 10, true)

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        def firstCache = builder.build()
        def secondCache = builder.build()

        then:
        new File(PLUGIN_DIRECTORY, "${ProfileCacheBuilder.FILE_NAME}.$extension").exists()
        firstCache instanceof FileProfileCache
        firstCache.config.class == expectedClass
        secondCache instanceof FileProfileCache
        secondCache.config.class == expectedClass

        where:
        type   | extension || expectedClass
        'json' | 'json'    || Class.forName('it.fulminazzo.yamlparser.configuration.JSONConfiguration')
        'xml'  | 'xml'     || Class.forName('it.fulminazzo.yamlparser.configuration.XMLConfiguration')
        'toml' | 'toml'    || Class.forName('it.fulminazzo.yamlparser.configuration.TOMLConfiguration')
        'yaml' | 'yml'     || Class.forName('it.fulminazzo.yamlparser.configuration.YAMLConfiguration')
    }

    def 'test that build with YAML uses file with .yaml extension'() {
        given:
        def cacheFile = new File(PLUGIN_DIRECTORY, "${ProfileCacheBuilder.FILE_NAME}.yaml")
        FileUtils.createNewFile(cacheFile)

        and:
        def file = mockConfiguration('YAML', 10, true)

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        def cache = builder.build()

        then:
        !new File(PLUGIN_DIRECTORY, "${ProfileCacheBuilder.FILE_NAME}.yml").exists()
        cache instanceof FileProfileCache
        cache.config.class == Class.forName('it.fulminazzo.yamlparser.configuration.YAMLConfiguration')
    }

    def 'test that build with no logger does not throw'() {
        given:
        def file = mockConfiguration(null, null, true)

        and:
        def builder = new ProfileCacheBuilder(null, PLUGIN_DIRECTORY, file)

        when:
        def cache = builder.build()

        then:
        cache instanceof FileProfileCache
    }

    def 'test that getExpiryTimeout of expire time #timeout returns #expected'() {
        given:
        def file = mockConfiguration(null, timeout, true)

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        def actualTimeout = builder.getExpiryTimeout()

        then:
        actualTimeout == expected

        where:
        timeout || expected
        10      || 10 * 1000
        null    || 86400 * 1000
    }

    def 'test that getBlacklistTimeout of blacklist time #timeout returns #expected'() {
        given:
        def file = mockConfiguration(null, null, timeout, true)

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        def actualTimeout = builder.getBlacklistTimeout()

        then:
        actualTimeout == expected

        where:
        timeout || expected
        10      || 10 * 1000
        null    || 86400 * 1000
    }

    def 'test that loadCacheType of type #type returns #expected'() {
        given:
        def file = mockConfiguration(type, true)

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        def actualType = builder.loadCacheType()

        then:
        actualType == expected

        where:
        type   || expected
        'JSON' || ProfileCacheBuilder.CacheType.JSON
        'json' || ProfileCacheBuilder.CacheType.JSON
        'YAML' || ProfileCacheBuilder.CacheType.YAML
        'TOML' || ProfileCacheBuilder.CacheType.TOML
        'XML'  || ProfileCacheBuilder.CacheType.XML
        null   || ProfileCacheBuilder.CacheType.JSON
    }

    def 'test that loadCacheType with no section returns JSON'() {
        given:
        def file = mockConfiguration(null, false)

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        def actualType = builder.loadCacheType()

        then:
        actualType == ProfileCacheBuilder.CacheType.JSON
    }

    def 'test that loadCacheType of invalid throws'() {
        given:
        def file = mockConfiguration('not-valid', true)

        and:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, file)

        when:
        builder.loadCacheType()

        then:
        thrown(CacheException)
    }

    def 'test that getPluginDirectory throws if null'() {
        given:
        def builder = new ProfileCacheBuilder(logger, null, null)

        when:
        builder.pluginDirectory

        then:
        thrown(CacheException)
    }

    def 'test that getConfiguration throws if null'() {
        given:
        def builder = new ProfileCacheBuilder(logger, PLUGIN_DIRECTORY, null)

        when:
        builder.configuration

        then:
        thrown(CacheException)
    }

    def 'test that checkFileExists throws wrapped ProfileCacheException'() {
        given:
        def file = Mock(File, constructorArgs: [PLUGIN_DIRECTORY, 'file.txt'])
        file.getName() >> 'file.txt'
        file.getParentFile() >> PLUGIN_DIRECTORY
        file.exists() >> false
        file.createNewFile() >> false

        when:
        ProfileCacheBuilder.checkFileExists(file)

        then:
        def e = thrown(CacheException)
        e.message == 'There was an error while creating file "file.txt"!'
    }

    def 'test that mock returns correct type'() {
        given:
        def type = 'test'

        and:
        def file = mockConfiguration(type, true)

        when:
        def actualType = file.getString('skin-cache.type')

        then:
        actualType == type
    }

    private static FileConfiguration mockConfiguration(String type, boolean section) {
        return mockConfiguration(type, null, section)
    }

    private static FileConfiguration mockConfiguration(String type, Long expireTimeout, boolean section) {
        return mockConfiguration(type, expireTimeout, 0, section)
    }

    private static FileConfiguration mockConfiguration(String type, Long expireTimeout,
                                                       Long blacklistTimeout, boolean section) {
        return mockConfiguration(
                type, expireTimeout, blacklistTimeout,
                "localhost/./$PLUGIN_DIRECTORY.path", 'h2:tcp', 'testdb',
                'sa', '',
                section)
    }

    private static FileConfiguration mockConfiguration(
            String type, Long expireTimeout, Long blacklistTimeout,
            String dbAddress, String dbType, String dbName,
            String dbUsername, String dbPassword,
            boolean section
    ) {
        def map = [:]
        if (section) map['skin-cache'] = [
                'type'          : type,
                'expire-time'   : expireTimeout,
                'blacklist-time': blacklistTimeout,
                'address'       : dbAddress,
                'database-type' : dbType,
                'database'      : dbName,
                'username'      : dbUsername,
                'password'      : dbPassword
        ]
        return new MockFileConfiguration(map)
    }

}
