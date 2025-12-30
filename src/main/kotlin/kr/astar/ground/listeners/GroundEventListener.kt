package kr.astar.ground.listeners

import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import io.papermc.paper.event.player.PlayerPickItemEvent
import io.papermc.paper.event.player.PlayerPurchaseEvent
import kr.astar.ground.Ground
import kr.astar.ground.utils.Utils.getRegion
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*

class GroundEventListener: Listener {
    private val plugin = Ground.plugin
    private val groundManager = Ground.groundManager

    fun hasAccess(player: Player): Boolean {
        try {
            val regionId= player.getRegion()
//        val protectedRegion= player.getProtectedRegion() ?: return true
//        if (!player.isInside()) return
            val ownerUUID=groundManager.getOwner(regionId)
//        val owner= Bukkit.getOfflinePlayer(ownerUUID)
            val crewList= groundManager.getCrewList(ownerUUID)
            return player.uniqueId in crewList
        } catch (_: Exception) {
            return true
        }
    }

    @EventHandler
    fun PlayerInteractEvent.onInteraction() {
        isCancelled = !hasAccess(player)
        // other functions?
    }

    @EventHandler
    fun PlayerDropItemEvent.onDrop() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerPickItemEvent.onPickup() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerBedEnterEvent.onEnter() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerBucketFillEvent.onFill() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerItemFrameChangeEvent.onChange() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerBucketEvent.onEvent() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerChangeBeaconEffectEvent.onChange() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerArmorStandManipulateEvent.onManipulate() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerPortalEvent.onRide() {
        isCancelled= !hasAccess(player)
    }

    @EventHandler
    fun PlayerPurchaseEvent.onPurchase() {
        isCancelled= !hasAccess(player)
    }
}