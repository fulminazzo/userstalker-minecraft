package it.fulminazzo.userstalker.gui


import it.fulminazzo.userstalker.client.USAsyncApiClient
import it.fulminazzo.yagl.guis.DataGUI
import it.fulminazzo.yagl.parsers.GUIYAGLParser
import it.fulminazzo.yamlparser.configuration.FileConfiguration
import spock.lang.Specification

import java.util.logging.Logger

class USGUIManagerTest extends Specification {

    private final File pluginDirectory = new File('build/resources/test')

    private USGUIManager manager

    void setup() {
        def logger = Logger.getLogger(getClass().simpleName)

        manager = new USGUIManager(logger, Mock(USAsyncApiClient))

        GUIYAGLParser.addAllParsers()
    }

    def 'test setup with no file uses and saves default User Logins GUI'() {
        given:
        def file = new File(pluginDirectory, 'guis.yml')
        if (file.exists()) file.delete()

        and:
        def defaultGUI = GUIs.defaultUserLogins()

        when:
        manager.setup(pluginDirectory)

        then:
        manager.userLoginsGUI == defaultGUI
        def config = FileConfiguration.newConfiguration(file)
        config.get('user-logins', DataGUI) == defaultGUI
    }

}
