package it.fulminazzo.userstalker.gui

import it.fulminazzo.jbukkit.BukkitUtils
import it.fulminazzo.userstalker.cache.ProfileCache
import it.fulminazzo.userstalker.domain.UserLogin
import it.fulminazzo.userstalker.domain.UserLoginCount
import it.fulminazzo.userstalker.meta.ProfiledSkullMeta
import it.fulminazzo.userstalker.meta.SMMockItemFactory
import it.fulminazzo.userstalker.utils.TimeUtils
import it.fulminazzo.yagl.contents.ItemGUIContent
import it.fulminazzo.yagl.guis.DataGUI
import it.fulminazzo.yagl.guis.PageableGUI
import it.fulminazzo.yagl.items.BukkitItem
import it.fulminazzo.yagl.items.Item
import it.fulminazzo.yagl.utils.MessageUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.mockito.Mockito
import spock.lang.Specification

import java.time.LocalDateTime
import java.util.logging.Logger

class GUIsTest extends Specification {

    private static final ProfileCache cache = new MockProfileCache()

    void setupSpec() {
        BukkitUtils.setupServer()
        Mockito.when(Bukkit.getServer().getItemFactory()).thenReturn(new SMMockItemFactory())
    }

    def 'test that provider #provider has default dataConverter with warning message'() {
        given:
        def gui = provider.apply(9)

        and:
        def dataConverter = gui.dataConverter

        when:
        def result = dataConverter.apply(data) as ItemGUIContent

        then:
        result.material == 'barrier'
        result.displayName == MessageUtils.color('&4NOT IMPLEMENTED')

        where:
        provider                                  | data
        GUIs.NAMED_USER_LOGINS_COUNT_GUI_PROVIDER | UserLoginCount.builder().build()
        GUIs.NAMED_USER_LOGINS_GUI_PROVIDER       | UserLogin.builder().build()
        GUIs.USER_LOGINS_GUI_PROVIDER             | UserLogin.builder().build()
    }

    def 'test that setupMetadataConversion works'() {
        given:
        def content = GUIs.setupMetadataConversion(
                ItemGUIContent.newInstance('PLAYER_HEAD'),
                cache
        ).setVariable('username', 'valid')

        when:
        def itemStack = content.render().copy(BukkitItem).create()
        def itemMeta = itemStack.getItemMeta()

        then:
        itemMeta instanceof ProfiledSkullMeta
        def skullMeta = itemMeta as ProfiledSkullMeta
        skullMeta.profile.name == 'valid'
    }

    def 'test that setupMetadataConversion does not throw'() {
        given:
        def content = GUIs.setupMetadataConversion(
                ItemGUIContent.newInstance('PLAYER_HEAD'),
                cache
        ).setVariable('username', 'error')

        and:
        def plugin = Mock(JavaPlugin)
        def logger = Logger.getLogger(getClass().simpleName)
        plugin.logger >> logger

        and:
        SpyStatic(JavaPlugin)
        JavaPlugin.getProvidingPlugin(GUIs) >> plugin

        when:
        content.render().copy(BukkitItem).create()

        then:
        noExceptionThrown()
    }

    def 'test that setupMetadataConversion returns expected content'() {
        when:
        def content = GUIs.setupMetadataConversion(
                ItemGUIContent.newInstance(material),
                profileCache
        )
        content.setVariable('username', username)

        def render = content.render().copy(BukkitItem).create()

        then:
        render == expected

        where:
        material      | profileCache | username  || expected
        'STONE'       | null         | null      || new ItemStack(Material.STONE)
        'STONE'       | cache        | null      || new ItemStack(Material.STONE)
        'PLAYER_HEAD' | cache        | null      || new ItemStack(Material.PLAYER_HEAD)
        'PLAYER_HEAD' | cache        | 'invalid' || new ItemStack(Material.PLAYER_HEAD)
    }

    def 'test that default top users logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultTopUsersLogins()

        and:
        def expected = DataGUI.newGUI(54, null)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name().toLowerCase()).setDisplayName(' '))
                .setPreviousPage(47, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName('&ePrevious page'))
                .setNextPage(51, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName('&eNext page'))
                .setContents(49, Item.newItem(Material.OBSIDIAN.name().toLowerCase()).setDisplayName('&fCurrent page: &e<page>&8/&a<pages>'))
                .setTitle('&cTop users logins')

        expect:
        defaultGUI == expected
    }

    def 'test that default monthly users logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultMonthlyUsersLogins()

        and:
        def expected = DataGUI.newGUI(45, null)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name().toLowerCase()).setDisplayName(' '))
                .setPreviousPage(38, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName('&ePrevious page'))
                .setNextPage(42, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName('&eNext page'))
                .setContents(40, Item.newItem(Material.OBSIDIAN.name().toLowerCase()).setDisplayName('&fCurrent page: &e<page>&8/&a<pages>'))
                .setTitle('&cMonthly users logins')

        expect:
        defaultGUI == expected
    }

    def 'test that default newest users logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultNewestUsersLogins()

        and:
        def expected = DataGUI.newGUI(45, null)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name().toLowerCase()).setDisplayName(' '))
                .setPreviousPage(38, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName('&ePrevious page'))
                .setNextPage(42, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName('&eNext page'))
                .setContents(40, Item.newItem(Material.OBSIDIAN.name().toLowerCase()).setDisplayName('&fCurrent page: &e<page>&8/&a<pages>'))
                .setTitle('&cNewest users logins')

        expect:
        defaultGUI == expected
    }

    def 'test that default user logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultUserLogins()

        and:
        def expected = DataGUI.newGUI(54, null)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name().toLowerCase()).setDisplayName(' '))
                .setPreviousPage(47, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName('&ePrevious page'))
                .setNextPage(51, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName('&eNext page'))
                .setContents(49, Item.newItem(Material.OBSIDIAN.name().toLowerCase()).setDisplayName('&fCurrent page: &e<page>&8/&a<pages>'))
                .setTitle('&c<username>\'s logins')

        expect:
        defaultGUI == expected
    }

    def 'test that setupPagesItemsAndCorners of gui with size #size sets items to slots #previous, #current and #next'() {
        given:
        def gui = PageableGUI.newGUI(size)

        when:
        GUIs.setupPagesItemsAndCorners(gui)

        then:
        def previousPage = gui.previousPage
        previousPage.isPresent()
        previousPage.key == previous

        def currentPage = gui.getContents(current)
        currentPage.size() == 1

        def nextPage = gui.nextPage
        nextPage.isPresent()
        nextPage.key == next

        where:
        size || previous | current | next
        9    || 2        | 4       | 6
        18   || 11       | 13      | 15
        27   || 20       | 22      | 24
        36   || 29       | 31      | 33
        45   || 38       | 40      | 42
        54   || 47       | 49      | 51
    }

    def 'test that setupVariables sets correct variables for object #object'() {
        given:
        def stringLoginDate = loginDate == null ? '' : TimeUtils.toString(loginDate)

        and:
        def metadatable = new MockMetadatable()

        when:
        GUIs.setupVariables(metadatable, object)

        then:
        metadatable.getVariable('username') == username
        metadatable.getVariable('ip') == ip
        metadatable.getVariable('login_date') == stringLoginDate

        where:
        username     | ip          | loginDate                             || object
        ''           | '127.0.0.1' | LocalDateTime.of(2025, 5, 25, 16, 13) || UserLogin.builder()
                .ip('127.0.0.1')
                .loginDate(LocalDateTime.of(2025, 5, 25, 16, 13))
                .build()
        'Fulminazzo' | ''          | LocalDateTime.of(2025, 5, 25, 16, 13) || UserLogin.builder()
                .username('Fulminazzo')
                .loginDate(LocalDateTime.of(2025, 5, 25, 16, 13))
                .build()
        'Fulminazzo' | '127.0.0.1' | null                                  || UserLogin.builder()
                .username('Fulminazzo')
                .ip('127.0.0.1')
                .build()
        'Fulminazzo' | ''          | null                                  || UserLogin.builder()
                .username('Fulminazzo').build()
        ''           | '127.0.0.1' | null                                  || UserLogin.builder()
                .ip('127.0.0.1').build()
        ''           | ''          | LocalDateTime.of(2025, 5, 25, 16, 13) || UserLogin.builder()
                .loginDate(LocalDateTime.of(2025, 5, 25, 16, 13))
                .build()
        'Fulminazzo' | '127.0.0.1' | LocalDateTime.of(2025, 5, 25, 16, 13) || UserLogin.builder()
                .username('Fulminazzo').ip('127.0.0.1')
                .loginDate(LocalDateTime.of(2025, 5, 25, 16, 13))
                .build()
    }

}
