package kr.astar.ground.utils

import kr.astar.ground.utils.Utils.prefix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit

class GNDLogger {

    private fun prefixComponent(): Component =
        MiniMessage.miniMessage().deserialize(
            prefix.replace(Regex("[|]"), "")
        )

    private fun format(color: String, message: Component): Component =
        prefixComponent()
            .append(
                MiniMessage.miniMessage().deserialize(
                    "<#C9B59C>></#C9B59C> "
                )
            )
            .append(message.color(NamedTextColor.NAMES.value(color)))

    fun info(message: String) = send("white", Component.text(message))
    fun warning(message: String) = send("yellow", Component.text(message))
    fun suc(message: String) = send("green", Component.text(message))
    fun bug(message: String) = send("red", Component.text(message))

    fun info(message: Component) = send("white", message)
    fun warning(message: Component) = send("yellow", message)
    fun suc(message: Component) = send("green", message)
    fun bug(message: Component) = send("red", message)

    private fun send(color: String, message: Component) {
        Bukkit.getConsoleSender().sendMessage(format(color, message))
    }
}
