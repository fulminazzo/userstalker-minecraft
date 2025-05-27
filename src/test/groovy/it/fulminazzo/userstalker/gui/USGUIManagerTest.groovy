package it.fulminazzo.userstalker.gui

import it.fulminazzo.fulmicollection.objects.Refl
import it.fulminazzo.jbukkit.BukkitUtils
import it.fulminazzo.userstalker.MockFileConfiguration
import it.fulminazzo.userstalker.UserStalker
import it.fulminazzo.userstalker.domain.UserLogin
import it.fulminazzo.userstalker.meta.SMMockItemFactory
import it.fulminazzo.yagl.GUIManager
import it.fulminazzo.yagl.contents.ItemGUIContent
import it.fulminazzo.yagl.guis.DataGUI
import it.fulminazzo.yagl.guis.GUI
import it.fulminazzo.yagl.items.BukkitItem
import it.fulminazzo.yagl.parsers.GUIYAGLParser
import it.fulminazzo.yagl.viewers.Viewer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.mockito.Mockito
import spock.lang.Specification

import java.util.logging.Logger

class USGUIManagerTest extends Specification {

    private static final File PLUGIN_DIRECTORY = new File('build/resources/test/usguimanager')

    private static MockAPIClient client
    private static USGUIManager manager

    private static boolean clicked = false

    void setupSpec() {
        BukkitUtils.setupServer()
        Mockito.when(Bukkit.getServer().isPrimaryThread()).thenReturn(true)
        Mockito.when(Bukkit.getServer().getItemFactory()).thenReturn(new SMMockItemFactory())

        def messages = new MockFileConfiguration([
                'internal-error': 'Internal error'
        ])

        def plugin = Mock(UserStalker)
        plugin.messages >> messages
        UserStalker.instance = plugin

        GUIYAGLParser.addAllParsers()

        new GUIManager()

        client = new MockAPIClient()

        manager = USGUIManager.builder()
                .logger(Logger.getLogger(getClass().simpleName))
                .apiClient(client)
                .pluginDirectory(PLUGIN_DIRECTORY)
                .build()
    }

    void cleanup() {
        BukkitUtils.PLAYERS.clear()
    }

    def 'test that openMainMenuGUI sets the correct action for #actionString'() {
        given:
        def player = getNewPlayer()

        and:
        def content = ItemGUIContent.newInstance('STONE')
        content.setVariable('action', actionString)
        def gui = GUI.newGUI(9).addContent(content)

        and:
        new Refl<>(manager).setFieldObject('mainMenuGUI', gui)

        when:
        manager.openMainMenuGUI(player)

        then:
        def guiViewer = GUIManager.getOpenGUIViewer(player)
        guiViewer.isPresent()
        def actualGui = guiViewer.value

        def actualContent = actualGui.getContents(0).get(0)
        def action = actualContent.clickItemAction()
        action.isPresent()

        when:
        action.get().execute(guiViewer.key, actualGui, actualContent)

        then:
        1 * player.openInventory(_ as Inventory)

        def newGuiViewer = GUIManager.getOpenGUIViewer(player)
        newGuiViewer.isPresent()
        def newGui = newGuiViewer.value

        newGui != gui
        newGui != null
        newGui.title == expectedTitle

        where:
        actionString       || expectedTitle
        USGUIAction.OPEN_GUI_TOP.serialize()|| '&cTop users logins'
        USGUIAction.OPEN_GUI_MONTHLY.serialize() || '&cMonthly users logins'
        USGUIAction.OPEN_GUI_NEWEST.serialize()  || '&cNewest users logins'
    }

    def 'test that openMainMenuGUI closes GUI on close action'() {
        given:
        def player = getNewPlayer()

        and:
        def content = ItemGUIContent.newInstance('STONE')
        content.setVariable('action', USGUIAction.CLOSE.serialize())
        def gui = GUI.newGUI(9).addContent(content)

        and:
        new Refl<>(manager).setFieldObject('mainMenuGUI', gui)

        when:
        manager.openMainMenuGUI(player)

        then:
        def guiViewer = GUIManager.getOpenGUIViewer(player)
        guiViewer.isPresent()
        def actualGui = guiViewer.value

        def actualContent = actualGui.getContents(0).get(0)
        def action = actualContent.clickItemAction()
        action.isPresent()

        when:
        action.get().execute(guiViewer.key, actualGui, actualContent)

        then:
        1 * player.closeInventory()
    }

    def 'test that openTopUsersLoginsGUI of valid opens GUI'() {
        given:
        def player = getNewPlayer()

        when:
        manager.openTopUsersLoginsGUI(player)

        then:
        def viewer = GUIManager.getViewer(player)
        viewer != null
        viewer.openGUI != null
        viewer.openGUI.title == GUIs.defaultTopUsersLogins().title
        1 * player.openInventory(_ as Inventory)
    }

    def 'test that openMonthlyUsersLoginsGUI of valid opens GUI'() {
        given:
        def player = getNewPlayer()

        when:
        manager.openMonthlyUsersLoginsGUI(player)

        then:
        def viewer = GUIManager.getViewer(player)
        viewer != null
        viewer.openGUI != null
        viewer.openGUI.title == GUIs.defaultMonthlyUsersLogins().title
        1 * player.openInventory(_ as Inventory)
    }

    def 'test that openNewestUsersLoginsGUI of valid opens GUI'() {
        given:
        def player = getNewPlayer()

        when:
        manager.openNewestUsersLoginsGUI(player)

        then:
        def viewer = GUIManager.getViewer(player)
        viewer != null
        viewer.openGUI != null
        viewer.openGUI.title == GUIs.defaultNewestUsersLogins().title
        1 * player.openInventory(_ as Inventory)
    }

    def 'test that #methodName opens user logins GUI upon click on content'() {
        given:
        def player = getNewPlayer()

        when:
        manager."$methodName"(player)

        then:
        def guiViewer = GUIManager.getOpenGUIViewer(player)
        guiViewer.isPresent()
        def gui = guiViewer.value

        def content = gui.getContents(10).get(0)
        def action = content.clickItemAction()
        action.isPresent()

        when:
        action.get().execute(guiViewer.key, gui, content)

        then:
        1 * player.openInventory(_ as Inventory)

        def newGuiViewer = GUIManager.getOpenGUIViewer(player)
        newGuiViewer.isPresent()
        def newGui = newGuiViewer.value

        newGui != gui
        newGui.title == '&cAlex\'s logins'

        where:
        methodName << [
                'openTopUsersLoginsGUI',
                'openMonthlyUsersLoginsGUI',
                'openNewestUsersLoginsGUI',
        ]
    }

    def 'test that openUserLoginsGUI of valid opens GUI'() {
        given:
        def player = getNewPlayer()

        when:
        manager.openUserLoginsGUI(player, 'valid')

        then:
        def viewer = GUIManager.getViewer(player)
        viewer != null
        viewer.openGUI != null
        viewer.openGUI.title == GUIs.defaultUserLogins().title.replace('<username>', 'valid')
        1 * player.openInventory(_ as Inventory)
    }

    def 'test that #managerMethod of invalid does not throw'() {
        given:
        def player = getNewPlayer()

        and:
        client."$clientMethod"()

        when:
        if (managerMethod == 'openUserLoginsGUI')
            manager.openUserLoginsGUI(player, 'invalid')
        else
            manager."$managerMethod"(player)

        then:
        noExceptionThrown()
        1 * player.sendMessage(_ as String)

        where:
        managerMethod               | clientMethod
        'openTopUsersLoginsGUI'     | 'disableTop'
        'openMonthlyUsersLoginsGUI' | 'disableMonthly'
        'openNewestUsersLoginsGUI'  | 'disableNewest'
        'openUserLoginsGUI'         | 'toString'
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
        new Refl<>(manager).setFieldObject('backGUIContentSlotOffset', -36)

        when:
        manager.prepareGUI(
                Mock(GUI),
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

    private Player getNewPlayer() {
        def player = Mock(Player)
        player.uniqueId >> UUID.randomUUID()
        player.name >> 'Fulminazzo'
        player.displayName >> 'Fulminazzo'
        player.server >> Bukkit.server
        player.online >> true
        BukkitUtils.PLAYERS.add(player)
        return player
    }

}
