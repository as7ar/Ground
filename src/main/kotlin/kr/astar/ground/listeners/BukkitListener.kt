package kr.astar.ground.listeners

import kr.astar.ground.Ground
import kr.astar.ground.data.GNData
import kr.astar.ground.events.PlayerRegionEnterEvent
import kr.astar.ground.events.PlayerRegionLeaveEvent
import kr.astar.ground.exception.GroundNotFound
import kr.astar.ground.utils.Utils.getProtectedRegion
import kr.astar.ground.utils.Utils.getRegion
import kr.astar.ground.utils.sendMessage
import kr.astar.ground.utils.toComponent
import kr.astar.ground.utils.translatable
import net.kyori.adventure.title.Title
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

class BukkitListener: Listener {
    private val plugin = Ground.plugin

    @Deprecated("Function for TEST")
//    @EventHandler
    fun PlayerMoveEvent.테스트() {
        player.sendActionBar(player.getRegion().toComponent())
    }

    @EventHandler
    fun PlayerInteractEvent.onInteraction() {
        if (!action.isRightClick) return
        if (!player.hasPermission("astar.ground.purchase")) return
        val groundManager= Ground.groundManager

        val purchaseItem= groundManager.getItem("purchase-item")
        if (!player.inventory.itemInMainHand.isSimilar(purchaseItem)) return

        val regionId=player.getRegion()

        if (regionId=="") {
            player.sendMessage("error.not.regions".translatable(), true)
            return
        }

        if (!regionId.startsWith(groundManager.gndPrefix, true)) return

        isCancelled = true

        val existing = runCatching {
            groundManager.getGround(regionId)
        }.getOrNull()

        if (existing != null && existing.world == player.world.uid) {
            player.sendMessage("content.owner.exist".translatable(), true)
            return
        }

        val owned= groundManager.getOwned(player.uniqueId)
        if (owned.size>= groundManager.MAX_OWNED) {
            player.sendMessage("content.crew.maximum.ground.3".translatable(
                "&c${groundManager.MAX_OWNED}".toComponent(), "&a${owned.size}".toComponent()
            ), true)
            return
        }

        val gndData= GNData(
            id= regionId,
            world= player.world.uid,
            owner= player.uniqueId
        )
        groundManager.addGround(gndData)

        player.getProtectedRegion()?.owners?.addPlayer(player.uniqueId)

        player.inventory.itemInMainHand.amount-=1
        player.sendMessage("content.purchase.success".translatable(
            "&a${regionId}".toComponent()
        ), true)
    }

    @EventHandler
    fun PlayerRegionEnterEvent.onEnter() {
        try {
            val groundManager = Ground.groundManager
            val ground = groundManager.getGround(region)
            val welcomeMsg= groundManager.getWelcomeContent(ground.owner)
            player.showTitle(Title.title("".toComponent(), "content.ground.join".translatable(
                "&a${plugin.server.getOfflinePlayer(ground.owner).name}".toComponent()
            ))
            )
            player.sendActionBar(welcomeMsg.toComponent())
        } catch (_: GroundNotFound) {}
    }

    @EventHandler
    fun PlayerRegionLeaveEvent.onLeave() {
        try {
            val groundManager = Ground.groundManager
            val ground = groundManager.getGround(region)
            player.showTitle(Title.title("".toComponent(), "content.ground.leave".translatable(
                "&a${plugin.server.getOfflinePlayer(ground.owner).name}".toComponent()
            ))
            )
        } catch (_: GroundNotFound) {}
    }
}