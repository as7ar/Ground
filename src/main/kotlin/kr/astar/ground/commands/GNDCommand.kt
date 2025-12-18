package kr.astar.ground.commands

import kr.astar.ground.Ground
import kr.astar.ground.commands.handler.GNDHandler
import kr.astar.ground.enums.CrewArgType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GNDCommand: ClassicCommand(
    "ground", listOf("땅", "gnd"),
    "Ground Plugin Default Command", "astar.ground.command"
) {
    private val plugin = Ground.plugin

    override fun execute(
        sender: CommandSender,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return true
        val handler= GNDHandler()
        val a = args[0]
        if (a=="목록") handler.handleList(sender)
        if (a=="동거") {
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
    ): List<String?> {
        val tab= mutableListOf<String>()
        if (args.isEmpty()) {
            tab.addAll(arrayOf("목록", "동거"))
            if (sender.hasPermission("astar.ground.admin")) tab.addAll(arrayOf(
                "설정"
            ))
        }
        if (args.size==1) {
            val a= args[0]
            tab.addAll(when(a) {
                "동거"-> arrayOf("추가", "제거", "목록")
                "설정"-> arrayOf("구매아이템", "땅접두사", "최대보유땅", "최대동거인원")
                else-> arrayOf("")
            })
        }
        return tab
    }
}