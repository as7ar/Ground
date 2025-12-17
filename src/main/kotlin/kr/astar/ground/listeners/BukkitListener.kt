package kr.astar.ground.listeners

import kr.astar.ground.Ground
import kr.astar.ground.data.GNData
import kr.astar.ground.events.PlayerRegionEnterEvent
import kr.astar.ground.events.PlayerRegionLeaveEvent
import kr.astar.ground.exception.GroundNotFound
import kr.astar.ground.manager.GroundManager
import kr.astar.ground.utils.Utils.getRegion
import kr.astar.ground.utils.toComponent
import kr.astar.ground.utils.translatable
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

class BukkitListener: Listener {
    private val plugin = Ground.plugin

    @Deprecated("Function for TEST")
    @EventHandler
    fun PlayerMoveEvent.테스트() {
        player.sendActionBar("${player.getRegion()}".toComponent())
    }

    @EventHandler
    fun PlayerInteractEvent.onInteraction() {
        if (!action.isRightClick) return
        if (!player.hasPermission("astar.ground.purchase")) return
        val groundManager= Ground.groundManager

        val purchaseItem= groundManager.getItem("purchase-item")
        if (!player.inventory.itemInMainHand.isSimilar(purchaseItem)) return

        val regionId=player.getRegion() ?: run {
            player.sendMessage("error.not.regions".translatable())
            return
        }

        if (!regionId.startsWith(groundManager.gndPrefix, true)) return

        val existing = runCatching {
            groundManager.getGround(regionId)
        }.getOrNull()

        if (existing != null && existing.world == player.world.uid) {
            player.sendMessage("content.owner.exist".translatable())
            return
        }

        val gndData= GNData(
            id= regionId,
            world= player.world.uid,
            owner= player.uniqueId
        )
        groundManager.addGround(gndData)
        player.sendMessage("content.purchase.success".translatable(
            "&a${regionId}"
        ))
    }

    @EventHandler
    fun PlayerRegionEnterEvent.onEnter() {
        val groundManager = Ground.groundManager
        val ground = groundManager.getGround(region)
        val ownerName = runCatching {
            plugin.server.getOfflinePlayer(ground.owner).name ?: "Unknown"
        }.getOrNull() ?: "Unknown"


    }

    @EventHandler
    fun PlayerRegionLeaveEvent.onLeave() {}
}