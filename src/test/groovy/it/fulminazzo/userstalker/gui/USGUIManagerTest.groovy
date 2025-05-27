package it.fulminazzo.userstalker.gui

import it.fulminazzo.jbukkit.BukkitUtils
import it.fulminazzo.userstalker.client.USAsyncApiClient
import it.fulminazzo.userstalker.domain.UserLogin
import it.fulminazzo.userstalker.meta.SMMockItemFactory
import it.fulminazzo.yagl.contents.ItemGUIContent
import it.fulminazzo.yagl.guis.DataGUI
import it.fulminazzo.yagl.guis.GUI
import it.fulminazzo.yagl.items.BukkitItem
import it.fulminazzo.yagl.parsers.GUIYAGLParser
import org.bukkit.Bukkit
import org.mockito.Mockito
import spock.lang.Specification

class USGUIManagerTest extends Specification {

    private static final File PLUGIN_DIRECTORY = new File('build/resources/test/usguimanager')

    private static USGUIManager manager

    private static boolean clicked = false

    void setupSpec() {
        BukkitUtils.setupServer()
        Mockito.when(Bukkit.getServer().getItemFactory()).thenReturn(new SMMockItemFactory())

        GUIYAGLParser.addAllParsers()

        manager = USGUIManager.builder()
                .apiClient(Mock(USAsyncApiClient))
                .pluginDirectory(PLUGIN_DIRECTORY)
                .build()
    }

    def 'test that prepareGUI sets correct backGUIContent'() {
        given:
        def gui = DataGUI.newGUI(27, null)

        when:
        gui = manager.prepareGUI(
                previousGUI,
                gui,
                ['data'],
                ItemGUIContent.newInstance('STONE'),
                (v, g, c) -> { }
        )

        and:
        def contents = gui.getContents(18)

        then:
        if (backGUIContent != null && previousGUI != null) {
            contents.size() > 0
            contents.get(0) == backGUIContent
        } else contents.size() == 0

        where:
        previousGUI | backGUIContent
        null        | null
        Mock(GUI)   | null
        null        | ItemGUIContent.newInstance('BARRIER')
        Mock(GUI)   | ItemGUIContent.newInstance('BARRIER')
    }

    def 'test that prepareContent correctly updates content when action is #action'() {
        given:
        def content = ItemGUIContent.newInstance('PLAYER_HEAD')
                .setDisplayName('<username>')

        and:
        def data = UserLogin.builder()
                .username('Fulminazzo')
                .build()

        when:
        content = manager.prepareContent(content, data, action)

        and:
        def stack = content.render().copy(BukkitItem).create()
        content.clickItemAction().ifPresent {
            it.execute(null, null, null)
        }

        then:
        stack.itemMeta.displayName == data.username
        action == null || clicked

        where:
        action << [
                (v, c, g) -> clicked = true,
                null
        ]
    }

    def 'test manager does load'() {
        expect:
        true
    }

}
