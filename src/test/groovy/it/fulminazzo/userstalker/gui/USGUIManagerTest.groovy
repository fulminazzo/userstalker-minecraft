package it.fulminazzo.userstalker.gui

import it.fulminazzo.fulmicollection.objects.Refl
import it.fulminazzo.jbukkit.BukkitUtils
import it.fulminazzo.userstalker.client.USAsyncApiClient
import it.fulminazzo.userstalker.domain.UserLogin
import it.fulminazzo.userstalker.meta.SMMockItemFactory
import it.fulminazzo.yagl.contents.ItemGUIContent
import it.fulminazzo.yagl.guis.DataGUI
import it.fulminazzo.yagl.guis.GUI
import it.fulminazzo.yagl.items.BukkitItem
import it.fulminazzo.yagl.parsers.GUIYAGLParser
import it.fulminazzo.yagl.viewers.Viewer
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

    def 'test that prepareGUI sets correct back action'() {
        given:
        def openings = 0

        and:
        def previousGUI = Mock(GUI)
        previousGUI.open(_) >> { openings++ }
        def viewer = Mock(Viewer)

        and:
        def backGUIContent = ItemGUIContent.newInstance('BARRIER')
        def gui = DataGUI.newGUI(27, null)

        and:
        new Refl<>(manager).setFieldObject('backGUIContent', backGUIContent)

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
        contents.size() > 0
        def content = contents.get(0)
        content.copy().onClickItem(null) == backGUIContent

        def itemAction = content.clickItemAction()
        itemAction.isPresent()
        itemAction.get().execute(viewer, previousGUI, content)
        openings == 1

        def closeAction = gui.closeGUIAction()
        closeAction.isPresent()
        closeAction.get().execute(viewer, previousGUI)
        openings == 2
    }

    def 'test that prepareGUI ignores back content when content is #backGUIContent and previous GUI is #previousGUI'() {
        given:
        def gui = DataGUI.newGUI(27, null)

        and:
        new Refl<>(manager).setFieldObject('backGUIContent', backGUIContent)

        when:
        gui = manager.prepareGUI(
                previousGUI,
                gui,
                ['data'],
                ItemGUIContent.newInstance('STONE'),
                (v, g, c) -> { }
        )

        then:
        gui.getContents(18).size() == 0

        where:
        previousGUI | backGUIContent
        null        | null
        Mock(GUI)   | null
        null        | ItemGUIContent.newInstance('BARRIER')
    }

    def 'test that prepareGUI with invalid back content slot offset does not throw'() {
        given:
        def gui = DataGUI.newGUI(27, null)

        and:
        new Refl<>(manager).setFieldObject('backGUIContentSlotOffset', 36)

        when:
        manager.prepareGUI(
                null,
                gui,
                ['data'],
                ItemGUIContent.newInstance('STONE'),
                (v, g, c) -> { }
        )

        then:
        noExceptionThrown()
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
