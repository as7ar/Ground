package kr.astar.ground.commands.handler

import kr.astar.ground.Ground
import kr.astar.ground.enums.CrewArgType
import kr.astar.ground.enums.SettingType
import kr.astar.ground.utils.toComponent
import kr.astar.ground.utils.translatable
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class GNDHandler {

    private val plugin = Ground.plugin
    private val groundManager = Ground.groundManager

    fun handleList(sender: Player) {
        sender.sendMessage("content.ground.list.header".translatable())

        if (sender.hasPermission("astar.ground.admin")) {
            groundManager.getGroundList().forEach { gnd ->
                sender.sendMessage("content.ground.list.item.admin".translatable(
                        "&a${gnd.id}".toComponent(),
                        "&e${Bukkit.getOfflinePlayer(gnd.owner).name}".toComponent(),
                        "&b${Bukkit.getWorld(gnd.world)?.name}".toComponent()
                ))
            }
        } else {
            groundManager.getOwned(sender.uniqueId).forEach { id ->
                val gnd = groundManager.getGround(id)
                sender.sendMessage("content.ground.list.item.user".translatable(
                    "&a${gnd.id}".toComponent(),
                    "&e${Bukkit.getWorld(gnd.world)?.name}".toComponent()
                ))
            }
        }

        sender.sendMessage("content.ground.list.footer".translatable())
    }

    fun handleCrew(sender: Player, type: CrewArgType, targetName: String? = null) {
        if (type == CrewArgType.LIST) {
            val crews = groundManager.getCrewList(sender.uniqueId)

            sender.sendMessage(
                "content.crew.list.header".translatable(
                    crews.size.toString().toComponent(),
                    groundManager.MAX_MEMBER.toString().toComponent()
                )
            )

            if (crews.isEmpty()) {
                sender.sendMessage("content.crew.list.empty".translatable())
            } else {
                crews.forEach { uuid -> sender.sendMessage(
                    "content.crew.list".translatable(uuid.toString().toComponent())
                ) }
            }

            sender.sendMessage("content.crew.list.footer".translatable())
            return
        }

        val target = targetName?.let { Bukkit.getPlayer(it) }
        if (target == null) {
            sender.sendMessage("error.invalid.player".translatable(
                (targetName ?: "null").toComponent()
            ))
            return
        }

        val success = when (type) {
            CrewArgType.ADD ->
                groundManager.addCrew(sender.uniqueId, target.uniqueId)
            CrewArgType.REMOVE ->
                groundManager.removeCrew(sender.uniqueId, target.uniqueId)
            else -> false
        }

        sender.sendMessage(
            "content.crew.${type.name.lowercase()}.${if (success) "success" else "fail"}"
                .translatable(target.name.toComponent())
        )
    }

    fun handleSetting(sender: Player, type: SettingType, value: String? = null) {
        if (type == SettingType.PURCHASE_ITEM) {
            val item = sender.inventory.itemInMainHand
            if (item.isEmpty) {
                sender.sendMessage("error.item.mainhand.required".translatable())
                return
            }

            groundManager.setItem("purchase-item", item)
            sender.sendMessage("content.setting.purchaseitem.success".translatable())
            return
        }

        if (value == null) {
            val current = when (type) {
                SettingType.GND_PREFIX ->
                    plugin.config.getString("region.prefix")
                SettingType.MAX_OWNED_GROUND ->
                    plugin.config.getInt("region.max-own")
                SettingType.MAX_GROUND ->
                    plugin.config.getInt("region.max-members")
                else -> "N/A"
            }

            sender.sendMessage(
                "content.config.get".translatable(
                    type.description.toComponent(),
                    current.toString().toComponent()
                )
            )
            return
        }

        if (type == SettingType.GND_PREFIX) {
            plugin.config.set("region.prefix", value)
            plugin.saveConfig()

            sender.sendMessage(
                "content.command.set.suc".translatable(
                    value.toComponent()
                )
            )
            return
        }

        val intValue = value.toIntOrNull()
        if (intValue == null) {
            sender.sendMessage("error.invalid.value".translatable(value.toComponent()))
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

        sender.sendMessage("content.command.set.suc".translatable(value.toComponent()))
    }
}
