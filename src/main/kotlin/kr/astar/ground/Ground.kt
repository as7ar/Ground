package kr.astar.ground

import kr.astar.ground.events.PlayerChangeRegionEvent
import kr.astar.ground.listeners.BukkitListener
import kr.astar.ground.listeners.EventListener
import kr.astar.ground.utils.GNDLogger
import kr.astar.ground.utils.Utils.bannerGenerator
import kr.astar.ground.utils.toMiniMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStreamReader

class Ground : JavaPlugin() {
    companion object {
        lateinit var plugin: Ground
            private set
    }

    val logger= GNDLogger()

    override fun onLoad() {
        plugin= this
    }

    override fun onEnable() {
        saveDefaultConfig()

        logger.info("Ground Enabled!")

        server.pluginManager.registerEvents(EventListener(), this)
        server.pluginManager.registerEvents(BukkitListener(), this)

        printLogo()
    }

    private fun printLogo() {
        val art = listOf(
            " ██████╗ ███╗   ██╗██████╗ ",
            "██╔════╝ ████╗  ██║██╔══██╗",
            "██║  ███╗██╔██╗ ██║██║  ██║",
            "██║   ██║██║╚██╗██║██║  ██║",
            "╚██████╔╝██║ ╚████║██████╔╝",
            " ╚═════╝ ╚═╝  ╚═══╝╚═════╝ "
        )

        val banner = bannerGenerator(
            art,
            version = pluginMeta.version,
            author = pluginMeta.authors.joinToString(", ")
        )

        banner.forEach { server.consoleSender.sendMessage(
            "<gradient:#B8DB80:#C9B59C>${it}</gradient>".toMiniMessage()
        ) }
    }

    private fun loadFile(name:String): YamlConfiguration {
        val file = File(dataFolder, name)

        if (!file.exists()) saveResource(name, false)

        val userConfig = YamlConfiguration.loadConfiguration(file)

        val defaultConfigStream = getResource(name) ?: return userConfig
        val defaultConfig = YamlConfiguration.loadConfiguration(
            InputStreamReader(defaultConfigStream, Charsets.UTF_8)
        )

        userConfig.options().copyDefaults(true)
        userConfig.setDefaults(defaultConfig)
        userConfig.save(file)

        return userConfig
    }
}
