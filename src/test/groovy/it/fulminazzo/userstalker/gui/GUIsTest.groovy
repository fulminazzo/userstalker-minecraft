package it.fulminazzo.userstalker.gui

import it.fulminazzo.yagl.guis.PageableGUI
import spock.lang.Specification

class GUIsTest extends Specification {

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
