package kr.astar.ground.commands.handler

import kr.astar.ground.Ground
import kr.astar.ground.enums.CrewArgType
import kr.astar.ground.enums.SettingType
import kr.astar.ground.exception.GroundMaximum
import kr.astar.ground.exception.GroundNotFound
import kr.astar.ground.manager.GroundManager
import kr.astar.ground.utils.sendMessage
import kr.astar.ground.utils.toComponent
import kr.astar.ground.utils.translatable
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class GNDHandler {

    private val plugin = Ground.plugin
    private val groundManager = Ground.groundManager

    fun handleList(sender: Player) {
        sender.sendMessage("content.ground.list.header".translatable(), true)

        if (sender.hasPermission("astar.ground.admin")) {
            groundManager.getGroundList().forEach { gnd ->
                sender.sendMessage("content.ground.list.item.admin".translatable(
                        "&a${gnd.id}".toComponent(),
                        "&e${Bukkit.getOfflinePlayer(gnd.owner).name}".toComponent(),
                        "&b${Bukkit.getWorld(gnd.world)?.name}".toComponent()
                ), true)
            }
        } else {
            groundManager.getOwned(sender.uniqueId).forEach { id ->
                val gnd = groundManager.getGround(id)
                sender.sendMessage("content.ground.list.item.user".translatable(
                    "&a${gnd.id}".toComponent(),
                    "&e${Bukkit.getWorld(gnd.world)?.name}".toComponent()
                ), true)
            }
        }

        sender.sendMessage("content.ground.list.footer".translatable(), true)
    }

    fun handleCrew(sender: Player, type: CrewArgType, targetName: String? = null) {
        if (type == CrewArgType.LIST) {
            val crews = groundManager.getCrewList(sender.uniqueId)

            sender.sendMessage(
                "content.crew.list.header".translatable(
                    crews.size.toString().toComponent(),
                    groundManager.MAX_MEMBER.toString().toComponent()
                ), true
            )

            if (crews.isEmpty()) {
                sender.sendMessage("content.crew.list.empty".translatable(), true)
            } else {
                crews.forEach { uuid -> sender.sendMessage(
                    "content.crew.list".translatable(Bukkit.getOfflinePlayer(uuid).name.toString().toComponent()),
                    true
                ) }
            }

            sender.sendMessage("content.crew.list.footer".translatable(), true)
            return
        }

        val target = targetName?.let { Bukkit.getOfflinePlayer(it) }
        if (target==null) {
            sender.sendMessage("error.player.not.exist".translatable(), true)
            return
        }
        /*if (target.player == null) {
            sender.sendMessage("error.invalid.player".translatable(
                targetName.toComponent()
            ), true)
            return
        }
*/
        val success = when (type) {
            CrewArgType.ADD -> {
                try {
                    val result= groundManager.addCrew(sender.uniqueId, target.uniqueId)
                    if (result) true
                    else {
                        sender.sendMessage("content.crew.maximum".translatable(), true)
                        false
                    }
                } catch (m: GroundMaximum) {
                    sender.sendMessage("content.crew.maximum.ground.1".translatable(), true)
                    sender.sendMessage("content.crew.maximum.ground.2".translatable(), true)
                    sender.sendMessage("content.crew.maximum.ground.3".translatable(
                        *groundManager.getOwned(target.uniqueId).map {
                            "땅 $it 제거".toComponent().clickEvent(ClickEvent.callback {  p->
                                groundManager.removeGround(it)
                                p.sendMessage("content.ground.remove.suc".translatable("&a${it}&f".toComponent()))
                                groundManager.addCrew(sender.uniqueId, target.uniqueId)
                            })
                        }.toTypedArray(),
                        "동거 포기하기".toComponent().clickEvent(ClickEvent.callback {
                            sender.sendMessage("content.crew.maximum.ground.giveup".translatable(), true)
                        })
                    ), true)
                    false
                }
            }
            CrewArgType.REMOVE ->
                groundManager.removeCrew(sender.uniqueId, target.uniqueId)
            else -> false
        }

        sender.sendMessage(
            "content.crew.${type.name.lowercase()}.${if (success) "success" else "fail"}"
                .translatable(targetName.toComponent()), true
        )
    }

    fun handleSetting(sender: Player, type: SettingType, value: String? = null) {
        if (type == SettingType.PURCHASE_ITEM) {
            val item = sender.inventory.itemInMainHand
            if (item.isEmpty) {
                sender.sendMessage("error.item.mainhand.required".translatable(), true)
                return
            }

            groundManager.setItem("purchase-item", item)
            sender.sendMessage("content.setting.purchaseitem.success".translatable(), true)
            return
        }


        if (value == null) {
            val current = when (type) {
                SettingType.GND_PREFIX ->
                    plugin.config.getString("region.prefix")
                SettingType.MAX_OWNED_GROUND ->
                    plugin.config.getInt("region.max-members")
                SettingType.MAX_GROUND ->
                    plugin.config.getInt("region.max-own")
                else -> "N/A"
            }

            sender.sendMessage(
                "content.config.get".translatable(
                    type.description.toComponent(),
                    current.toString().toComponent()
                ), true
            )
            return
        }

        if (type== SettingType.REMOVE_GROUND) {
            try {
                GroundManager.Generator.remove(sender, value)
                groundManager.removeGround(value)
                sender.sendMessage("content.ground.remove.suc".translatable("&e${value}".toComponent()), true)
            } catch (_: GroundNotFound) {
                sender.sendMessage("error.invalid.ground".translatable(value.toComponent()), true)
            }
            return
        }

        if (type == SettingType.GND_PREFIX) {
            plugin.config.set("region.prefix", value)
            plugin.saveConfig()

            sender.sendMessage(
                "content.command.set.suc".translatable(
                    value.toComponent()
                ), true
            )
            return
        }

        val intValue = value.toIntOrNull()
        if (intValue == null) {
            sender.sendMessage("error.invalid.value".translatable(value.toComponent()), true)
            return
        }

        plugin.config.set(
            when (type) {
                SettingType.MAX_OWNED_GROUND -> "region.max-own"
                SettingType.MAX_GROUND -> "region.max-members"
                else -> return
            },
            intValue
        )

        plugin.saveConfig()

        sender.sendMessage("content.command.set.suc".translatable(value.toComponent()), true)
    }
}
