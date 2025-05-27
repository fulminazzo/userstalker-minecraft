package it.fulminazzo.userstalker.gui

import it.fulminazzo.userstalker.client.USAsyncApiClient
import it.fulminazzo.yagl.parsers.GUIYAGLParser
import spock.lang.Specification

class USGUIManagerTest extends Specification {

    private static final File PLUGIN_DIRECTORY = new File('build/resources/test/usguimanager')

    private static USGUIManager manager

    void setupSpec() {
        GUIYAGLParser.addAllParsers()

        manager = USGUIManager.builder()
                .apiClient(Mock(USAsyncApiClient))
                .pluginDirectory(PLUGIN_DIRECTORY)
                .build()
    }

    def 'test manager does load'() {
        expect:
        true
    }

}
