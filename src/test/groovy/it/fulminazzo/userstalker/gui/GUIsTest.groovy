package it.fulminazzo.userstalker.gui

import it.fulminazzo.yagl.guis.DataGUI
import it.fulminazzo.yagl.guis.PageableGUI
import it.fulminazzo.yagl.items.Item
import org.bukkit.Material
import spock.lang.Specification

class GUIsTest extends Specification {

    def 'test that default top users logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultTopUsersLogins()

        and:
        def expected = DataGUI.newGUI(45, defaultGUI.dataConverter)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(" "))
                .setPreviousPage(38, Item.newItem(Material.PAPER.name()).setDisplayName("&ePrevious page"))
                .setNextPage(42, Item.newItem(Material.PAPER.name()).setDisplayName("&eNext page"))
                .setContents(40, Item.newItem(Material.OBSIDIAN.name()).setDisplayName("&fCurrent page: &e<page>&8/&a<pages>"))
                .setTitle("&6Top users logins")

        expect:
        defaultGUI == expected
    }

    def 'test that default monthly users logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultMonthlyUsersLogins()

        and:
        def expected = DataGUI.newGUI(27, defaultGUI.dataConverter)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(" "))
                .setPreviousPage(20, Item.newItem(Material.PAPER.name()).setDisplayName("&ePrevious page"))
                .setNextPage(24, Item.newItem(Material.PAPER.name()).setDisplayName("&eNext page"))
                .setContents(22, Item.newItem(Material.OBSIDIAN.name()).setDisplayName("&fCurrent page: &e<page>&8/&a<pages>"))
                .setTitle("&6Monthly users logins")

        expect:
        defaultGUI == expected
    }

    def 'test that default newest users logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultNewestUsersLogins()

        and:
        def expected = DataGUI.newGUI(27, defaultGUI.dataConverter)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(" "))
                .setPreviousPage(20, Item.newItem(Material.PAPER.name()).setDisplayName("&ePrevious page"))
                .setNextPage(24, Item.newItem(Material.PAPER.name()).setDisplayName("&eNext page"))
                .setContents(22, Item.newItem(Material.OBSIDIAN.name()).setDisplayName("&fCurrent page: &e<page>&8/&a<pages>"))
                .setTitle("&6Newest users logins")

        expect:
        defaultGUI == expected
    }

    def 'test that default user logins GUI matches with expected gui'() {
        given:
        def defaultGUI = GUIs.defaultUserLogins()

        and:
        def expected = DataGUI.newGUI(54, defaultGUI.dataConverter)
                .setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(" "))
                .setPreviousPage(47, Item.newItem(Material.PAPER.name()).setDisplayName("&ePrevious page"))
                .setNextPage(51, Item.newItem(Material.PAPER.name()).setDisplayName("&eNext page"))
                .setContents(49, Item.newItem(Material.OBSIDIAN.name()).setDisplayName("&fCurrent page: &e<page>&8/&a<pages>"))
                .setTitle("&6<user>'s logins")

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

}
