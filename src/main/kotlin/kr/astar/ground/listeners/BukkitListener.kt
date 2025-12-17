package kr.astar.ground.listeners

import kr.astar.ground.Ground
import kr.astar.ground.data.GNData
import kr.astar.ground.events.PlayerRegionEnterEvent
import kr.astar.ground.events.PlayerRegionLeaveEvent
import kr.astar.ground.exception.GroundNotFound
import kr.astar.ground.manager.GroundManager
import kr.astar.ground.utils.Utils.getRegion
import kr.astar.ground.utils.toComponent
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class BukkitListener: Listener {
    @EventHandler
    fun PlayerInteractEvent.onInteraction() {
        if (!action.isRightClick) return
        if (!player.hasPermission("astar.ground.purchase")) return
        val groundManager= Ground.groundManager

        val purchaseItem= groundManager.getItem("purchase-item")
        if (!player.inventory.itemInMainHand.isSimilar(purchaseItem)) return

        val regionId=player.getRegion() ?: run {
            player.sendMessage(Component.translatable("error.not.regions"))
            return
        }

        val existing = runCatching {
            groundManager.getGround(regionId)
        }.getOrNull()

        if (existing != null && existing.world == player.world.uid) {
            player.sendMessage(Component.translatable("content.owner.exist"))
            return
        }

        val gndData= GNData(
            id= regionId,
            world= player.world.uid,
            owner= player.uniqueId
        )
        groundManager.addGround(gndData)
        player.sendMessage(Component.translatable(
            "content.purchase.success",
            "&a${regionId}".toComponent()
        ))
    }

    @EventHandler
    fun PlayerRegionEnterEvent.onEnter() {}

    @EventHandler
    fun PlayerRegionLeaveEvent.onLeave() {}
}