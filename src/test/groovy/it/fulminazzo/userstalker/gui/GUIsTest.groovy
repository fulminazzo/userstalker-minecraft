package it.fulminazzo.userstalker.gui

import it.fulminazzo.jbukkit.BukkitUtils
import it.fulminazzo.userstalker.cache.ProfileCache
import it.fulminazzo.userstalker.cache.ProfileCacheException
import it.fulminazzo.userstalker.domain.UserLogin
import it.fulminazzo.yagl.guis.DataGUI
import it.fulminazzo.yagl.guis.PageableGUI
import it.fulminazzo.yagl.items.BukkitItem
import it.fulminazzo.yagl.items.Item
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import spock.lang.Specification

import java.time.LocalDateTime

class GUIsTest extends Specification {

    private static final ProfileCache cache = new MockProfileCache()

    void setup() {
        BukkitUtils.setupServer()
    }

    def 'test that newContentConverter does not throw'() {
        given:
        def content = GUIs.newContentConverter('PLAYER_HEAD', cache)
                .setVariable('username', 'error')

        when:
        content.render().copy(BukkitItem).create()

        then:
        def e = thrown(IllegalArgumentException)
        // Plugin not available so check that the error is missing plugin
        e.message.contains('is not provided by class')
    }

    def 'test that newContentConverter returns expected content'() {
        when:
        def content = GUIs.newContentConverter(material, profileCache)
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
        def expected = DataGUI.newGUI(54, defaultGUI.dataConverter)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(' '))
                .setPreviousPage(47, Item.newItem(Material.PAPER.name()).setDisplayName('&ePrevious page'))
                .setNextPage(51, Item.newItem(Material.PAPER.name()).setDisplayName('&eNext page'))
                .setContents(49, Item.newItem(Material.OBSIDIAN.name()).setDisplayName('&fCurrent page: &e<page>&8/&a<pages>'))
                .setTitle('&cTop users logins')

        expect:
        defaultGUI == expected
    }

    def 'test that default monthly users logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultMonthlyUsersLogins()

        and:
        def expected = DataGUI.newGUI(45, defaultGUI.dataConverter)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(' '))
                .setPreviousPage(38, Item.newItem(Material.PAPER.name()).setDisplayName('&ePrevious page'))
                .setNextPage(42, Item.newItem(Material.PAPER.name()).setDisplayName('&eNext page'))
                .setContents(40, Item.newItem(Material.OBSIDIAN.name()).setDisplayName('&fCurrent page: &e<page>&8/&a<pages>'))
                .setTitle('&cMonthly users logins')

        expect:
        defaultGUI == expected
    }

    def 'test that default newest users logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultNewestUsersLogins()

        and:
        def expected = DataGUI.newGUI(45, defaultGUI.dataConverter)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(' '))
                .setPreviousPage(38, Item.newItem(Material.PAPER.name()).setDisplayName('&ePrevious page'))
                .setNextPage(42, Item.newItem(Material.PAPER.name()).setDisplayName('&eNext page'))
                .setContents(40, Item.newItem(Material.OBSIDIAN.name()).setDisplayName('&fCurrent page: &e<page>&8/&a<pages>'))
                .setTitle('&cNewest users logins')

        expect:
        defaultGUI == expected
    }

    def 'test that default user logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultUserLogins()

        and:
        def expected = DataGUI.newGUI(54, defaultGUI.dataConverter)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(' '))
                .setPreviousPage(47, Item.newItem(Material.PAPER.name()).setDisplayName('&ePrevious page'))
                .setNextPage(51, Item.newItem(Material.PAPER.name()).setDisplayName('&eNext page'))
                .setContents(49, Item.newItem(Material.OBSIDIAN.name()).setDisplayName('&fCurrent page: &e<page>&8/&a<pages>'))
                .setTitle('&c<user>\'s logins')

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
        def stringLoginDate = loginDate == null ? '' : loginDate.toString().replace('T', ' ')

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
