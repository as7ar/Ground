package kr.astar.ground.commands

import kr.astar.ground.commands.handler.GNDHandler
import kr.astar.ground.enums.CrewArgType
import kr.astar.ground.enums.SettingType
import kr.astar.ground.utils.translatable
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
            sender.sendMessage("error.no.permission".translatable())
            return true
        }
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
        if (a=="설정") {
            val b= args[1]
            val c= args.getOrNull(2)
            if (b=="구매아이템") {
                handler.handleSetting(sender, SettingType.PURCHASE_ITEM)
            }
            if (b=="땅접두사") {
                handler.handleSetting(sender, SettingType.GND_PREFIX, c)
            }
            if (b=="최대보유땅") {
                handler.handleSetting(sender, SettingType.MAX_GROUND, c)
            }
            if (b=="최대동거인원") {
                handler.handleSetting(sender, SettingType.MAX_OWNED_GROUND, c)
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