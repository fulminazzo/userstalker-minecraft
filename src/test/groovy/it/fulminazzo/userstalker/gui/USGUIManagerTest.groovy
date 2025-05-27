package it.fulminazzo.userstalker.gui

import it.fulminazzo.jbukkit.BukkitUtils
import it.fulminazzo.userstalker.client.USAsyncApiClient
import it.fulminazzo.userstalker.domain.UserLogin
import it.fulminazzo.userstalker.meta.SMMockItemFactory
import it.fulminazzo.yagl.contents.ItemGUIContent
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
