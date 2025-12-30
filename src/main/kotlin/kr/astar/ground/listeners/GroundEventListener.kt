package kr.astar.ground.listeners

import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import io.papermc.paper.event.player.PlayerPickItemEvent
import io.papermc.paper.event.player.PlayerPurchaseEvent
import kr.astar.ground.Ground
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*

class GroundEventListener : Listener {

    private val groundManager = Ground.groundManager

    private fun hasAccess(player: Player): Boolean {
        return try {
            val regionId = player.getRegion()
            val ownerUUID = groundManager.getOwner(regionId)
            val crewList = groundManager.getCrewList(ownerUUID)
            player.uniqueId in crewList
        } catch (_: Exception) {
            true
        }
    }

    private fun cancelIfNoAccess(event: Event, player: Player) {
        if (!hasAccess(player)) {
            when (event) {
                is Cancellable -> event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onDrop(e: PlayerDropItemEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onPickup(e: PlayerPickItemEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onBed(e: PlayerBedEnterEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onBucketFill(e: PlayerBucketFillEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onBucketEmpty(e: PlayerBucketEmptyEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onItemFrame(e: PlayerItemFrameChangeEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onBeacon(e: PlayerChangeBeaconEffectEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onArmorStand(e: PlayerArmorStandManipulateEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onPortal(e: PlayerPortalEvent) =
        cancelIfNoAccess(e, e.player)

    @EventHandler
    fun onPurchase(e: PlayerPurchaseEvent) =
        cancelIfNoAccess(e, e.player)
}
