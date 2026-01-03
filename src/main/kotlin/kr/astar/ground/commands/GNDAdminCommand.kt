package kr.astar.ground.commands

import kr.astar.ground.Ground
import kr.astar.ground.commands.handler.GNDHandler
import kr.astar.ground.enums.SettingType
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GNDAdminCommand: ClassicCommand(
    "groundadmin", listOf("땅관리자", "gndadmin"),
    "Ground Plugin Admin Command", "astar.ground.admin"
) {
    override fun execute(
        sender: CommandSender,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("astar.ground.admin")) {
            sender.sendMessage(Component.translatable("error.no.permission"))
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(Component.translatable("error.invalid.command"))
            return true
        }

        val handler = GNDHandler()
        val a = args[0]

        if (a=="설정") {
            if (args.size < 2) {
                sender.sendMessage(Component.translatable("error.invalid.command"))
                return true
            }
            val b = args[1]
            val c = args.getOrNull(2)
            when(b) {
                "구매아이템" -> {
                    handler.handleSetting(sender, SettingType.PURCHASE_ITEM)
                }
                "땅접두사" -> {
                    handler.handleSetting(sender, SettingType.GND_PREFIX, c)
                }
                "최대보유땅" -> {
                    handler.handleSetting(sender, SettingType.MAX_GROUND, c)
                }
                "최대동거인원" -> {
                    handler.handleSetting(sender, SettingType.MAX_OWNED_GROUND, c)
                }
                "땅제거" -> {
                    handler.handleSetting(sender, SettingType.REMOVE_GROUND, c)
                }
                "언어파일로드" -> {
                    Ground.plugin.translateSet()
                }
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
                tab.add("설정")
            }

            2 -> when (args[0]) {
                "설정" -> tab.addAll(listOf("구매아이템", "땅접두사", "최대보유땅", "최대동거인원", "땅제거", "언어파일로드"))
            }

            3 -> when (args[0]) {
                "설정" -> {
                    when (args[1]) {
                        "땅접두사", "최대보유땅", "최대동거인원" -> {
                            tab.add("<값>")
                        }
                        "땅제거" -> {
                            tab.add("<땅ID>")
                        }
                    }
                }
            }
        }

        return tab
    }
}
