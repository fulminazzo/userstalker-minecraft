package it.fulminazzo.userstalker.gui

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

}
