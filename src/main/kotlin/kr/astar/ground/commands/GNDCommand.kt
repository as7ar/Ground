package kr.astar.ground.commands

import org.bukkit.command.CommandSender

class GNDCommand: ClassicCommand(
    "data", listOf("땅", "gnd"),
    "Ground Plugin Default Command", "astar.ground.command"
) {
    override fun execute(
        sender: CommandSender,
        args: Array<out String>
    ): Boolean {

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