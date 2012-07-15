package com.github.krockode.slideshow

import spock.lang.Specification

import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

class SlideshowCommandExecutorTest extends Specification {

    def cmdExecutor
    def setup() {
        def plugin = Mock(Plugin)
        def fileConfig = Mock(FileConfiguration)
        def slidesConfig = Mock(ConfigurationSection)
        fileConfig.getConfigurationSection(_) >> slidesConfig
        slidesConfig.getConfigurationSection(_) >> slidesConfig
        slidesConfig.getKeys(false) >> ["test"]
        slidesConfig.getStringList("locations") >> ["world,-250,80,25,120,40"]
        plugin.config >> fileConfig
        plugin.server >> Mock(Server)
        cmdExecutor = new SlideshowCommandExecutor(plugin)
    }

    def "when I execute the slideshow list command (i.e. default command)" () {
        given:
            def player = Mock(Player)
            String[] args = []
        when:
            def result = cmdExecutor.onCommand(player, null, "", args)
        then:
            result == true
            1 * player.sendMessage("Slideshows (1): ${ChatColor.GREEN}test")
    }
}