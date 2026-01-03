package kr.astar.ground.commands

import kr.astar.ground.commands.handler.GNDHandler
import kr.astar.ground.enums.CrewArgType
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GNDCommand: ClassicCommand(
    "ground", listOf("땅", "gnd"),
    "Ground Plugin Default Command", "astar.ground.command"
) {
    override fun execute(
        sender: CommandSender,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("astar.ground.command")) {
            sender.sendMessage(Component.translatable("error.no.permission"))
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(Component.translatable("error.invalid.command"))
            return true
        }

        val handler= GNDHandler()
        val a = args[0]
        if (a=="목록") handler.handleList(sender)
        if (a=="동거") {
            if (args.size < 2) {
                sender.sendMessage(Component.translatable("error.invalid.command"))
                return true
            }
            val b= args[1]
            val c = args.getOrNull(2)
            when(b) {
                "추가"-> {
                    handler.handleCrew(sender, CrewArgType.ADD, c)
                }
                "제거"-> {
                    handler.handleCrew(sender, CrewArgType.REMOVE, c)
                }
                "목록"-> handler.handleCrew(sender, CrewArgType.LIST)
            }
        }
        return true
    }

    override fun tabComplete(
        sender: CommandSender,
        args: Array<out String>
    ): List<String> {
        val tab = mutableListOf<String>()

        when (args.size) {
            1 -> {
                tab.addAll(listOf("목록", "동거"))
            }

            2 -> when (args[0]) {
                "동거" -> tab.addAll(listOf("추가", "제거", "목록"))
            }

            3 -> when (args[0]) {
                "동거" -> {
                    if (args[1] == "추가" || args[1] == "제거") {
                        tab.addAll(Bukkit.getOnlinePlayers().map { it.name })
                    }
                }
            }
        }

        return tab
    }
}