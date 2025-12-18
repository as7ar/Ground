package kr.astar.ground.commands.handler

import kr.astar.ground.Ground
import kr.astar.ground.enums.CrewArgType
import kr.astar.ground.enums.SettingType
import kr.astar.ground.utils.translatable
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class GNDHandler {
    private val plugin = Ground.plugin
    private val groundManager= Ground.groundManager

    fun handleList(sender: Player) {
        sender.sendMessage("content.ground.list.header".translatable())
        if (sender.hasPermission("astar.ground.admin")) {
            groundManager.getGroundList().forEach { gnd->
                sender.sendMessage {
                    "content.ground.list.item.admin".translatable(
                        "&a${gnd.id}", "&e${gnd.owner}", "&b${gnd.world}"
                    )
                }
            }
        } else {
            groundManager.getOwned(sender.uniqueId).forEach { string->
                val gnd = groundManager.getGround(string)
                sender.sendMessage {
                    "content.ground.list.item.user".translatable(
                        "&a${gnd.id}", "&e${gnd.world}"
                    )
                }
            }
        }
        sender.sendMessage("content.ground.list.footer".translatable())
    }

    fun handleCrew(sender: Player, type: CrewArgType, targetName: String?=null) {
        if (type!= CrewArgType.LIST) {
            if (
                targetName==null
                || Bukkit.getPlayer(targetName)==null
            ) {
                sender.sendMessage("error.invalid.player".translatable(targetName.toString()))
                return
            }

            if (type== CrewArgType.ADD) {
                val success= groundManager.addCrew(
                    sender.uniqueId,
                    Bukkit.getPlayer(targetName)!!.uniqueId
                )
                if (success) {
                    sender.sendMessage("content.crew.add.success".translatable(
                        "&a${targetName}"
                    ))
                } else {
                    sender.sendMessage("content.crew.add.fail".translatable(
                        "&c${targetName}"
                    ))
                }
            } else if (type== CrewArgType.REMOVE) {
                val success= groundManager.removeCrew(
                    sender.uniqueId,
                    Bukkit.getPlayer(targetName)!!.uniqueId
                )
                if (success) {
                    sender.sendMessage("content.crew.remove.success".translatable(
                        "&a${targetName}"
                    ))
                } else {
                    sender.sendMessage("content.crew.remove.fail".translatable(
                        "&c${targetName}"
                    ))
                }
            }
        } else {
            val crews = groundManager.getCrewList(sender.uniqueId)
            sender.sendMessage("content.crew.list.header".translatable(
                "&e${crews.size}", "&a${groundManager.MAX_MEMBER}"
            ))
            if (crews.isEmpty()) {
                sender.sendMessage("content.crew.list.empty".translatable())
            } else {
                crews.forEach { uuid ->
                    sender.sendMessage("content.crew.list".translatable(
                        "&e${uuid}"
                    ))
                }
            }
            sender.sendMessage("content.crew.list.footer".translatable())
        }
    }

    fun handleSetting(sender: Player, type: SettingType, value:String?=null) {
        if (type== SettingType.PURCHASE_ITEM) {
            val item = sender.inventory.itemInMainHand
            if (item.isEmpty) {
                sender.sendMessage("error.item.mainhand.required".translatable())
                return
            }
            groundManager.setItem("purchase-item", item)
            sender.sendMessage("content.setting.purchaseitem.success".translatable())
        } else {
            if (value==null) {
                sender.sendMessage("content.config.get".translatable(
                    "&e${type.description}", "&a${when(type) {
                        SettingType.GND_PREFIX-> plugin.config.getString("region.prefix")
                        SettingType.MAX_OWNED_GROUND-> plugin.config.getInt("region.max-own")
                        SettingType.MAX_GROUND-> plugin.config.getInt("region.max-members")
                        else-> "N/A"
                    }}"
                ))
                return
            }

            if (type == SettingType.GND_PREFIX) {
                plugin.config.set("region.prefix", value)
                plugin.saveConfig()
                sender.sendMessage("content.command.set.suc".translatable(value))
                return
            }

            val intValue= value.toIntOrNull()
            if (intValue==null) {
                sender.sendMessage("error.invalid.value".translatable(value))
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
            sender.sendMessage("content.command.set.suc".translatable(value))
        }
    }
}