package it.fulminazzo.userstalker.gui

import it.fulminazzo.userstalker.client.USAsyncApiClient
import it.fulminazzo.yagl.parsers.GUIYAGLParser
import it.fulminazzo.yamlparser.configuration.FileConfiguration
import it.fulminazzo.yamlparser.utils.FileUtils
import spock.lang.Specification

import java.util.logging.Logger

class USGUIManagerTest extends Specification {

    private final File pluginDirectory = new File('build/resources/test')

    private static USGUIManager manager

    void setupSpec() {
        def logger = Logger.getLogger(getClass().simpleName)

        manager = new USGUIManager(logger, Mock(USAsyncApiClient), null)

        GUIYAGLParser.addAllParsers()
    }

    def 'test setup with no file uses and saves default #objectName'() {
        given:
        def file = new File(pluginDirectory, 'guis.yml')
        if (file.exists()) file.delete()

        when:
        manager.setup(pluginDirectory)

        then:
        manager."$objectName" == defaultObject
        def config = FileConfiguration.newConfiguration(file)
        config.get(path, defaultObject.class) == defaultObject

        where:
        defaultObject                    | path                        || objectName
        GUIs.defaultTopUsersLogins()     | 'guis.top-users-logins'     || 'topUsersLoginsGUI'
        GUIs.defaultMonthlyUsersLogins() | 'guis.monthly-users-logins' || 'monthlyUsersLoginsGUI'
        GUIs.defaultNewestUsersLogins()  | 'guis.newest-users-logins'  || 'newestUsersLoginsGUI'
        GUIs.defaultUserLogins()         | 'guis.user-logins'          || 'userLoginsGUI'
        GUIs.defaultNamedUserLoginItem() | 'items.newest-users-logins' || 'newestUsersLoginsGUIContent'
        GUIs.defaultUserLoginItem()      | 'items.user-logins'         || 'userLoginsGUIContent'
    }

    def 'test setup with file uses stored #guiName GUI'() {
        given:
        def file = new File(pluginDirectory, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        and:
        def dataGUI = defaultGUI.setTitle('Hello, world!')

        and:
        def config = FileConfiguration.newConfiguration(file)
        config.set(path, dataGUI)
        config.save()

        when:
        manager.setup(pluginDirectory)

        then:
        manager."$guiName" == dataGUI

        where:
        defaultGUI                       | path                        || guiName
        GUIs.defaultTopUsersLogins()     | 'guis.top-users-logins'     || 'topUsersLoginsGUI'
        GUIs.defaultMonthlyUsersLogins() | 'guis.monthly-users-logins' || 'monthlyUsersLoginsGUI'
        GUIs.defaultNewestUsersLogins()  | 'guis.newest-users-logins'  || 'newestUsersLoginsGUI'
        GUIs.defaultUserLogins()         | 'guis.user-logins'          || 'userLoginsGUI'
    }

    def 'test setup with file uses stored #itemName GUI'() {
        given:
        def file = new File(pluginDirectory, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        and:
        def item = defaultItem.setVariable('Hello', 'world!')

        and:
        def config = FileConfiguration.newConfiguration(file)
        config.set(path, item)
        config.save()

        when:
        manager.setup(pluginDirectory)

        then:
        manager."$itemName" == item

        where:
        defaultItem                      | path                        || itemName
        GUIs.defaultNamedUserLoginItem() | 'items.newest-users-logins' || 'newestUsersLoginsGUIContent'
        GUIs.defaultUserLoginItem()      | 'items.user-logins'         || 'userLoginsGUIContent'
    }

    def 'test setup with file but object null uses default #objectName object'() {
        given:
        def file = new File(pluginDirectory, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        and:
        def config = FileConfiguration.newConfiguration(file)
        config.set(path, null)
        config.save()

        when:
        manager.setup(pluginDirectory)

        then:
        manager."$objectName" == defaultObject

        where:
        defaultObject                    | path                        || objectName
        GUIs.defaultTopUsersLogins()     | 'guis.top-users-logins'     || 'topUsersLoginsGUI'
        GUIs.defaultMonthlyUsersLogins() | 'guis.monthly-users-logins' || 'monthlyUsersLoginsGUI'
        GUIs.defaultNewestUsersLogins()  | 'guis.newest-users-logins'  || 'newestUsersLoginsGUI'
        GUIs.defaultUserLogins()         | 'guis.user-logins'          || 'userLoginsGUI'
        GUIs.defaultNamedUserLoginItem() | 'items.newest-users-logins' || 'newestUsersLoginsGUIContent'
        GUIs.defaultUserLoginItem()      | 'items.user-logins'         || 'userLoginsGUIContent'
    }

    def 'test setup with file but object not available uses default #objectName'() {
        given:
        def file = new File(pluginDirectory, 'guis.yml')
        if (file.exists()) file.delete()
        FileUtils.createNewFile(file)

        when:
        manager.setup(pluginDirectory)

        then:
        manager."$objectName" == defaultObject

        where:
        defaultObject                    | path                        || objectName
        GUIs.defaultTopUsersLogins()     | 'guis.top-users-logins'     || 'topUsersLoginsGUI'
        GUIs.defaultMonthlyUsersLogins() | 'guis.monthly-users-logins' || 'monthlyUsersLoginsGUI'
        GUIs.defaultNewestUsersLogins()  | 'guis.newest-users-logins'  || 'newestUsersLoginsGUI'
        GUIs.defaultUserLogins()         | 'guis.user-logins'          || 'userLoginsGUI'
        GUIs.defaultNamedUserLoginItem() | 'items.newest-users-logins' || 'newestUsersLoginsGUIContent'
        GUIs.defaultUserLoginItem()      | 'items.user-logins'         || 'userLoginsGUIContent'
    }

}
