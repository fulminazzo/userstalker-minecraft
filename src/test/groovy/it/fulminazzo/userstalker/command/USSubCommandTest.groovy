package it.fulminazzo.userstalker.command


import it.fulminazzo.fulmicommands.configuration.ConfigurationException
import it.fulminazzo.userstalker.MockFileConfiguration
import it.fulminazzo.userstalker.UserStalker
import it.fulminazzo.userstalker.cache.domain.IPInfo
import it.fulminazzo.userstalker.cache.exception.CacheException
import it.fulminazzo.userstalker.cache.ip.IPCache
import it.fulminazzo.userstalker.client.APIClientException
import it.fulminazzo.userstalker.client.USAsyncApiClient
import it.fulminazzo.userstalker.gui.USGUIManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import spock.lang.Specification

import java.util.function.Consumer
import java.util.logging.Logger

class USSubCommandTest extends Specification {

    private UserStalker plugin

    private CommandSender sender

    void setup() {
        plugin = Mock(UserStalker)
        plugin.messages >> new MockFileConfiguration([:])
        plugin.logger >> Logger.getLogger(getClass().simpleName)
        plugin.name >> 'UserStalker'

        sender = Mock(CommandSender)

        UserStalker.instance = plugin
    }

    def 'test that LookupSubCommand works for ip #ip'() {
        given:
        def subcommand = new LookupSubCommand(plugin)

        and:
        def cache = Mock(IPCache.newCache(plugin.logger).class)
        cache.lookupIPInfoAnd(_ as String, _ as Consumer, _ as Runnable) >> { args ->
            if (args[0] == 'error') args[2].run()
            else args[1].accept(ipInfo)
        }
        plugin.ipCache >> cache

        and:
        def args = []
        if (ip != null) args.add(ip)

        when:
        subcommand.execute(sender, args.toArray(new String[0]))

        then:
        1 * sender.sendMessage(_)

        where:
        ip                   || ipInfo
        null                 || null
        'invalid'            || null
        'valid'              || mockIpInfo(false, false)
        'valid_mobile'       || mockIpInfo(true, false)
        'valid_proxy'        || mockIpInfo(false, true)
        'valid_mobile_proxy' || mockIpInfo(true, true)
        'error'              || null
    }

    def 'test that LookupSubCommand tabComplete returns #expected for arguments #args'() {
        given:
        def subcommand = new LookupSubCommand(plugin)

        when:
        def result = subcommand.tabComplete(sender, args.toArray(new String[0]))

        then:
        result == expected

        where:
        args || expected
        []   || []
        [''] || ['<ip>']
    }

    def 'test that HelpSubCommand sends #number messages for arguments #args'() {
        given:
        def subcommand = new HelpSubCommand(plugin, new USCommand(plugin))

        and:
        sender.hasPermission(_ as String) >> true

        when:
        subcommand.execute(sender, args.toArray(new String[0]))

        then:
        number * sender.sendMessage(_ as String)

        where:
        number || args
        1      || ['notexisting']
        3      || ['o']
        1      || ['?']
        4      || []
    }

    def 'test that HelpSubCommand tabComplete returns #expected for #args'() {
        given:
        def subcommand = new HelpSubCommand(plugin, new USCommand(plugin))

        and:
        sender.hasPermission(_ as String) >> { a ->
            String perm = a[0]
            return !perm.contains('help')
        }

        when:
        def list = subcommand.tabComplete(sender, args.toArray(new String[0]))

        then:
        list == expected

        where:
        args    || expected
        []      || []
        ['rel'] || ['reload']
    }

    def 'test that OpenGUISubCommand executes correctly with arguments #arguments'() {
        given:
        def subcommand = new OpenGUISubCommand(plugin)

        and:
        def player = Mock(Player)

        and:
        def guiManager = Mock(USGUIManager)
        plugin.getGUIManager() >> guiManager

        and:
        def apiClient = Mock(USAsyncApiClient)
        apiClient.usernames >> ['fulminazzo']
        plugin.apiClient >> apiClient

        when:
        subcommand.execute(player, arguments.toArray(new String[0]))

        then:
        if (arguments.size() == 0)
            1 * guiManager.openMainMenuGUI(player)
        else
            1 * guiManager.openUserLoginsGUI(player, *arguments)

        where:
        arguments << [
                [],
                ['Fulminazzo']
        ]
    }

    def 'test that OpenGUISubCommand sends error message for invalid username'() {
        given:
        def subcommand = new OpenGUISubCommand(plugin)

        and:
        def player = Mock(Player)

        and:
        def guiManager = Mock(USGUIManager)
        plugin.getGUIManager() >> guiManager

        and:
        def apiClient = Mock(USAsyncApiClient)
        apiClient.usernames >> ['fulminazzo']
        plugin.apiClient >> apiClient

        when:
        subcommand.execute(player, new String[]{'notexisting'})

        then:
        noExceptionThrown()
        1 * player.sendMessage(_ as String)
        0 * guiManager.openUserLoginsGUI(player, _ as String)
    }

    def 'test that OpenGUISubCommand refutes for non-player senders'() {
        given:
        def subcommand = new OpenGUISubCommand(plugin)

        and:
        def guiManager = Mock(USGUIManager)
        plugin.getGUIManager() >> guiManager

        when:
        subcommand.execute(sender, new String[0])

        then:
        noExceptionThrown()
        1 * sender.sendMessage(_ as String)
    }

    def 'test that OpenGUISubCommand tabComplete returns #expected for #sender'() {
        given:
        def subcommand = new OpenGUISubCommand(plugin)

        and:
        def apiClient = Mock(USAsyncApiClient)
        apiClient.usernames >> ['fulminazzo']
        plugin.apiClient >> apiClient

        when:
        def list = subcommand.tabComplete(sender, new String[]{''})

        then:
        list == expected

        where:
        sender              || expected
        Mock(CommandSender) || []
        Mock(Player)        || ['fulminazzo']
    }

    def 'test that ReloadSubCommand executes correctly'() {
        given:
        def subcommand = new ReloadSubCommand(plugin)

        when:
        subcommand.execute(sender, new String[0])

        then:
        noExceptionThrown()
        1 * plugin.reload()
        1 * sender.sendMessage(_ as String)
    }

    def 'test that ReloadSubCommand does not throw for exception #exception'() {
        given:
        plugin.reload() >> {
            throw exception
        }

        and:
        def subcommand = new ReloadSubCommand(plugin)

        when:
        subcommand.execute(sender, new String[0])

        then:
        noExceptionThrown()
        1 * sender.sendMessage(_ as String)
        1 * plugin.forceDisable()

        where:
        exception << [
                new CacheException('Profile cache'),
                new APIClientException('Api client'),
                new ConfigurationException('Configuration')
        ]
    }

    private static IPInfo mockIpInfo(boolean mobile, boolean proxy) {
        return IPInfo.builder()
                .ip('ip')
                .country('country')
                .countryCode('country_code')
                .region('region')
                .city('city')
                .isp('isp')
                .mobile(mobile)
                .proxy(proxy)
                .build()
    }

}
