package kr.astar.ground

import kr.astar.ground.commands.GNDCommand
import kr.astar.ground.listeners.BukkitListener
import kr.astar.ground.listeners.EventListener
import kr.astar.ground.listeners.GroundEventListener
import kr.astar.ground.manager.GroundManager
import kr.astar.ground.utils.Debugger.debug
import kr.astar.ground.utils.GNDLogger
import kr.astar.ground.utils.Utils.bannerGenerator
import kr.astar.ground.utils.toMiniMessage
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationStore
import net.kyori.adventure.util.UTF8ResourceBundleControl
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStreamReader
import java.util.*

class Ground : JavaPlugin() {
    companion object {
        lateinit var plugin: Ground
            private set
        lateinit var groundManager: GroundManager
            private set
    }

    val logger= GNDLogger()

    override fun onLoad() {
        plugin= this
        groundManager = GroundManager()
    }

    override fun onEnable() {
        saveDefaultConfig()
        printLogo()

        debug("Ground Enabled!")

        translateSet()

        logger.info(Component.translatable("content.test", Component.text("Ground")))

        debug("Registering Events...")
        server.pluginManager.registerEvents(EventListener(), this)
        server.pluginManager.registerEvents(BukkitListener(), this)
        server.pluginManager.registerEvents(GroundEventListener(), this)

        debug("Loading Commands...")
        GNDCommand().register()
    }

    fun translateSet() {
        val store = TranslationStore.messageFormat(Key.key("astar:ground"))

        val locale = Locale.KOREA
        val bundle = ResourceBundle.getBundle(
            "locale.Bundle",
            locale,
            UTF8ResourceBundleControl.get()
        )

        store.registerAll(locale, bundle, false)
        GlobalTranslator.translator().addSource(store)
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
