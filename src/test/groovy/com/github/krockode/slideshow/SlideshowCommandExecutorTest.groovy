package com.github.krockode.slideshow

import java.util.logging.Logger
import spock.lang.Specification

import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
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
        slidesConfig.getMapList("slides") >> [[world: "world", x: -250d, y: 80d, z: 25d, yaw: 120d, pitch: 40d]]
        plugin.config >> fileConfig
        plugin.server >> Mock(Server)
        plugin.logger >> Logger.getLogger('test')
        def world = Mock(World)
        world.name >> "Vulcan"
        plugin.server.world >> world
        cmdExecutor = new SlideshowCommandExecutor(plugin)
    }

    def "when I execute the slideshow default list command as any type of sender" () {
        given:
            def sender = Mock(CommandSender)
            String[] args = []
        when:
            def result = cmdExecutor.onCommand(sender, null, "", args)
        then:
            result == true
            1 * sender.sendMessage("Slideshows (1): ${ChatColor.GREEN}test")
    }

    def "when I run an unknown slideshow" () {
        given:
            def sender = Mock(Player)
            String[] args = ["nottest"]
        when:
            def result = cmdExecutor.onCommand(sender, null, "", args)
        then:
            result == true
            1 * sender.sendMessage("${ChatColor.RED}Cannot run slideshow ${args[0]}");
    }

    def "when I run the test slideshow" () {
        given:
            def sender = Mock(Player)
            def server = Mock(Server)
            sender.server >> server
            def scheduler = Mock(BukkitScheduler)
            server.scheduler >> scheduler
            String[] args = ["test"]
        when:
            def result = cmdExecutor.onCommand(sender, null, "", args)
        then:
            result == true
            1 * scheduler.scheduleSyncDelayedTask(_, _, _)
    }
}