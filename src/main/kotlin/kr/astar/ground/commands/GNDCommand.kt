package kr.astar.ground.commands

import kr.astar.ground.Ground
import kr.astar.ground.commands.handler.GNDHandler
import kr.astar.ground.enums.CrewArgType
import kr.astar.ground.enums.SettingType
import net.kyori.adventure.text.Component
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
            if (b=="땅제거") {
                handler.handleSetting(sender, SettingType.REMOVE_GROUND, c)
            }
            if (b=="언어파일로드") {
                Ground.plugin.translateSet()
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
                if (sender.hasPermission("astar.ground.admin")) {
                    tab.add("설정")
                }
            }

            2 -> when (args[0]) {
                "동거" -> tab.addAll(listOf("추가", "제거", "목록"))
                "설정" -> tab.addAll(listOf("구매아이템", "땅접두사", "최대보유땅", "최대동거인원", "땅제거", "언어파일로드"))
            }

            3 -> when (args[0]) {
                "동거" -> {
                    if (args[1] == "추가" || args[1] == "제거") {
                        tab.add("<플레이어>")
                    }
                }
            }
        }

        return tab
    }
}