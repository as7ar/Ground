package kr.astar.ground.listeners

import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import io.papermc.paper.event.player.PlayerPickItemEvent
import io.papermc.paper.event.player.PlayerPurchaseEvent
import kr.astar.ground.Ground
import kr.astar.ground.utils.Utils.getRegion
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*

class GroundEventListener : Listener {

    private val groundManager = Ground.groundManager

    private fun hasAccess(player: Player, location: Location?=null): Boolean {
        return try {
            val regionId = location?.getRegion() ?: player.getRegion()
            val ownerUUID = groundManager.getOwner(regionId)
            val crewList = groundManager.getCrewList(ownerUUID)
            player.uniqueId in crewList || ownerUUID ==  player.uniqueId || player.isOp
        } catch (_: Exception) {
            true
        }
    }

    private fun cancel(player: Player): Boolean {
        return !hasAccess(player)
    }

    @EventHandler
    fun PlayerInteractEvent.onInteraction() {
        isCancelled = !hasAccess(player, interactionPoint)
    }

    @EventHandler
    fun PlayerDropItemEvent.onDrop() {
        isCancelled = cancel(player)
    }

    @EventHandler
    fun PlayerPickItemEvent.onPickup() {
        isCancelled = cancel(player)
    }

    @EventHandler
    fun PlayerBedEnterEvent.onEnter() {
        isCancelled = !hasAccess(player, bed.location)
    }

    @EventHandler
    fun PlayerBucketFillEvent.onFill() {
        isCancelled = !hasAccess(player, this.blockClicked.location)
    }

    @EventHandler
    fun PlayerBucketEmptyEvent.onEmpty() {
        isCancelled = !hasAccess(player, this.blockClicked.location)
    }

    @EventHandler
    fun PlayerItemFrameChangeEvent.onChange() {
        isCancelled = !hasAccess(player, this.itemFrame.location)
    }

    @EventHandler
    fun PlayerChangeBeaconEffectEvent.onChange() {
        isCancelled = !hasAccess(player, this.beacon.location)
    }

    @EventHandler
    fun PlayerArmorStandManipulateEvent.onManipulate() {
        isCancelled = cancel(player)
    }

    @EventHandler
    fun PlayerPortalEvent.onPortal() {
        isCancelled = cancel(player)
    }

    @EventHandler
    fun PlayerPurchaseEvent.onPurchase() {
        isCancelled = cancel(player)
    }
}
