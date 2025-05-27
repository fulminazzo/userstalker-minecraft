package it.fulminazzo.userstalker.gui

import it.fulminazzo.fulmicommands.configuration.ConfigurationException
import it.fulminazzo.userstalker.client.USAsyncApiClient
import it.fulminazzo.yagl.parsers.GUIYAGLParser
import it.fulminazzo.yamlparser.configuration.FileConfiguration
import it.fulminazzo.yamlparser.utils.FileUtils
import spock.lang.Specification

import java.util.logging.Logger

class USGUIManagerBuilderTest extends Specification {

    private static final File PLUGIN_DIRECTORY = new File('build/resources/test')

    private static USGUIManagerBuilder builder

    void setupSpec() {
        def logger = Logger.getLogger(getClass().simpleName)

        builder = new USGUIManagerBuilder()
                .logger(logger)
                .apiClient(Mock(USAsyncApiClient))
                .pluginDirectory(PLUGIN_DIRECTORY)
                .skinCache(null)

        GUIYAGLParser.addAllParsers()
    }

    def 'test build with no file uses and saves default #objectName'() {
        given:
        def file = new File(PLUGIN_DIRECTORY, 'guis.yml')
        if (file.exists()) file.delete()

        when:
        def manager = builder.build()

        then:
        manager."$objectName" == defaultObject
        def config = FileConfiguration.newConfiguration(file)
        config.get(path, defaultObject.class) == defaultObject

        where:
        defaultObject                    | path                         || objectName
        GUIs.defaultMainMenu()           | 'guis.main-menu'             || 'mainMenuGUI'
        GUIs.defaultTopUsersLogins()     | 'guis.top-users-logins'      || 'topUsersLoginsGUI'
        GUIs.defaultMonthlyUsersLogins() | 'guis.monthly-users-logins'  || 'monthlyUsersLoginsGUI'
        GUIs.defaultNewestUsersLogins()  | 'guis.newest-users-logins'   || 'newestUsersLoginsGUI'
        GUIs.defaultUserLogins()         | 'guis.user-logins'           || 'userLoginsGUI'
        GUIs.defaultUserLoginCountItem() | 'items.top-users-logins'     || 'topUsersLoginsGUIContent'
        GUIs.defaultUserLoginCountItem() | 'items.monthly-users-logins' || 'monthlyUsersLoginsGUIContent'
        GUIs.defaultNamedUserLoginItem() | 'items.newest-users-logins'  || 'newestUsersLoginsGUIContent'
        GUIs.defaultUserLoginItem()      | 'items.user-logins'          || 'userLoginsGUIContent'
        GUIs.defaultBackItem()           | 'items.back'                 || 'backGUIContent'
    }

    def 'test build with file uses stored #guiName GUI'() {
        given:
        def file = new File(PLUGIN_DIRECTORY, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        and:
        def dataGUI = defaultGUI.setTitle('Hello, world!')

        and:
        def config = FileConfiguration.newConfiguration(file)
        config.set(path, dataGUI)
        config.save()

        when:
        def manager = builder.build()

        then:
        manager."$guiName" == dataGUI

        where:
        defaultGUI                       | path                        || guiName
        GUIs.defaultMainMenu()           | 'guis.main-menu'             || 'mainMenuGUI'
        GUIs.defaultTopUsersLogins()     | 'guis.top-users-logins'     || 'topUsersLoginsGUI'
        GUIs.defaultMonthlyUsersLogins() | 'guis.monthly-users-logins' || 'monthlyUsersLoginsGUI'
        GUIs.defaultNewestUsersLogins()  | 'guis.newest-users-logins'  || 'newestUsersLoginsGUI'
        GUIs.defaultUserLogins()         | 'guis.user-logins'          || 'userLoginsGUI'
    }

    def 'test build with file uses stored #itemName GUI'() {
        given:
        def file = new File(PLUGIN_DIRECTORY, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        and:
        def item = defaultItem.setVariable('Hello', 'world!')

        and:
        def config = FileConfiguration.newConfiguration(file)
        config.set(path, item)
        config.save()

        when:
        def manager = builder.build()

        then:
        manager."$itemName" == item

        where:
        defaultItem                      | path                         || itemName
        GUIs.defaultUserLoginCountItem() | 'items.top-users-logins'     || 'topUsersLoginsGUIContent'
        GUIs.defaultUserLoginCountItem() | 'items.monthly-users-logins' || 'monthlyUsersLoginsGUIContent'
        GUIs.defaultNamedUserLoginItem() | 'items.newest-users-logins'  || 'newestUsersLoginsGUIContent'
        GUIs.defaultUserLoginItem()      | 'items.user-logins'          || 'userLoginsGUIContent'
        GUIs.defaultBackItem()           | 'items.back'                 || 'backGUIContent'
    }

    def 'test build with file but object null uses default #objectName object'() {
        given:
        def file = new File(PLUGIN_DIRECTORY, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        and:
        def config = FileConfiguration.newConfiguration(file)
        config.set(path, null)
        config.save()

        when:
        def manager = builder.build()

        then:
        manager."$objectName" == defaultObject

        where:
        defaultObject                    | path                         || objectName
        GUIs.defaultMainMenu()           | 'guis.main-menu'             || 'mainMenuGUI'
        GUIs.defaultTopUsersLogins()     | 'guis.top-users-logins'      || 'topUsersLoginsGUI'
        GUIs.defaultMonthlyUsersLogins() | 'guis.monthly-users-logins'  || 'monthlyUsersLoginsGUI'
        GUIs.defaultNewestUsersLogins()  | 'guis.newest-users-logins'   || 'newestUsersLoginsGUI'
        GUIs.defaultUserLogins()         | 'guis.user-logins'           || 'userLoginsGUI'
        GUIs.defaultUserLoginCountItem() | 'items.top-users-logins'     || 'topUsersLoginsGUIContent'
        GUIs.defaultUserLoginCountItem() | 'items.monthly-users-logins' || 'monthlyUsersLoginsGUIContent'
        GUIs.defaultNamedUserLoginItem() | 'items.newest-users-logins'  || 'newestUsersLoginsGUIContent'
        GUIs.defaultUserLoginItem()      | 'items.user-logins'          || 'userLoginsGUIContent'
        null                             | 'items.back'                 || 'backGUIContent'
    }

    def 'test build with file but object not available uses default #objectName'() {
        given:
        def file = new File(PLUGIN_DIRECTORY, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        when:
        def manager = builder.build()

        then:
        manager."$objectName" == defaultObject

        where:
        defaultObject                    | path                         || objectName
        GUIs.defaultMainMenu()           | 'guis.main-menu'             || 'mainMenuGUI'
        GUIs.defaultTopUsersLogins()     | 'guis.top-users-logins'      || 'topUsersLoginsGUI'
        GUIs.defaultMonthlyUsersLogins() | 'guis.monthly-users-logins'  || 'monthlyUsersLoginsGUI'
        GUIs.defaultNewestUsersLogins()  | 'guis.newest-users-logins'   || 'newestUsersLoginsGUI'
        GUIs.defaultUserLogins()         | 'guis.user-logins'           || 'userLoginsGUI'
        GUIs.defaultUserLoginCountItem() | 'items.top-users-logins'     || 'topUsersLoginsGUIContent'
        GUIs.defaultUserLoginCountItem() | 'items.monthly-users-logins' || 'monthlyUsersLoginsGUIContent'
        GUIs.defaultNamedUserLoginItem() | 'items.newest-users-logins'  || 'newestUsersLoginsGUIContent'
        GUIs.defaultUserLoginItem()      | 'items.user-logins'          || 'userLoginsGUIContent'
        null                             | 'items.back'                 || 'backGUIContent'
    }

    def 'test that getBackContentOffset of #slot returns #expected'() {
        given:
        def file = new File(PLUGIN_DIRECTORY, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        and:
        def config = FileConfiguration.newConfiguration(file)
        config.set('misc.back-offset', slot)
        config.save()

        when:
        def manager = builder.build()

        then:
        manager.backGUIContentSlotOffset == expected

        where:
        slot || expected
        null || -9
        0    || -9
        10   || -9
        -1   || -1
        -3   || -3
    }

    def 'test that #methodName throws if not specified'() {
        given:
        def builder = new USGUIManagerBuilder()

        when:
        builder."$methodName"()

        then:
        thrown(ConfigurationException)

        where:
        methodName << ['getPluginDirectory', 'getApiClient']
    }

}
