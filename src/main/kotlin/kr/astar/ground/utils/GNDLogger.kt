package kr.astar.ground.utils

import kr.astar.ground.utils.Utils.prefix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit

class GNDLogger {
    private fun format(color: String, message: String): Component {
        return MiniMessage.miniMessage().deserialize(
            prefix.replace(Regex("[|]"), "")
        ).append("<#B8DB80>></#C9B59C> <$color>$message</$color>".toMiniMessage())
    }

    fun info(string: String) = send("white", string)
    fun warning(string: String) = send("yellow", string)
    fun suc(string: String) = send("green", string)
    fun bug(string: String) = send("red", string)

    private fun send(color: String, string: String) {
        Bukkit.getConsoleSender().sendMessage(format(color, string))
    }
}