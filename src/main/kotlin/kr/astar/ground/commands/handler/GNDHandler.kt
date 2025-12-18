package kr.astar.ground.commands.handler

import kr.astar.ground.Ground
import kr.astar.ground.enums.CrewArgType
import kr.astar.ground.utils.translatable
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class GNDHandler {
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
}