package it.fulminazzo.userstalker.listener

import it.fulminazzo.userstalker.UserStalker
import it.fulminazzo.userstalker.client.USAsyncApiClient
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import spock.lang.Specification

class PlayerListenerTest extends Specification {

    def 'test that onPlayerJoin notifies api'() {
        given:
        def player = Mock(Player)
        player.name >> 'Fulminazzo'
        player.address >> new InetSocketAddress('localhost', 12345)

        and:
        def apiClient = Mock(USAsyncApiClient)

        and:
        def plugin = Mock(UserStalker)
        plugin.apiClient >> apiClient

        and:
        def listener = new PlayerListener(plugin)

        when:
        listener.onPlayerJoin(new PlayerJoinEvent(player, 'Player joined'))

        then:
        1 * apiClient.notifyUserLogin(player.name, player.address)
    }

}
