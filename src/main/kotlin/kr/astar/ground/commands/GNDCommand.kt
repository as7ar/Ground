package kr.astar.ground.commands

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

        return true
    }

    override fun tabComplete(
        sender: CommandSender,
        args: Array<out String>
    ): List<String?> {
        val tab= mutableListOf<String>()
        if (args.isEmpty()) {
            tab.addAll(arrayOf(""))
        }
        return tab
    }
}