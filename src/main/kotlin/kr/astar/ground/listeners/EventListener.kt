package kr.astar.ground.listeners

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import kr.astar.ground.events.PlayerChangeRegionEvent
import kr.astar.ground.events.PlayerRegionEnterEvent
import kr.astar.ground.events.PlayerRegionLeaveEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

class EventListener: Listener {
    private val wg = WorldGuard.getInstance()
    private val container = wg.platform.regionContainer
    private val playerRegions = mutableMapOf<UUID, String?>()

    @EventHandler(
        ignoreCancelled = true,
        priority = org.bukkit.event.EventPriority.HIGH
    )
    fun PlayerMoveEvent.onMove() {
        if (
            from.blockX == to.blockX
            && from.blockY == to.blockY
            && from.blockZ == to.blockZ
        ) return

        val world = BukkitAdapter.adapt(player.world)
        val regionManager = container[world] ?: return
        val blockVector = BukkitAdapter.asBlockVector(player.location)
        val regions = regionManager.getApplicableRegions(blockVector)
        val currentRegion = regions.regions.maxByOrNull { it.priority }?.id
        val lastRegion = playerRegions[player.uniqueId]

        if (currentRegion==lastRegion) return

        val changeEvent = PlayerChangeRegionEvent(lastRegion, currentRegion, player)
        changeEvent.callEvent()
        playerRegions[player.uniqueId] = currentRegion
        isCancelled = changeEvent.isCancelled
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = org.bukkit.event.EventPriority.HIGH
    )
    fun PlayerChangeRegionEvent.onChange() {

        if (pastRegion!=null) {
            val event= PlayerRegionLeaveEvent(player, pastRegion)
            event.callEvent()
            isCancelled= event.isCancelled
        }

        if (newRegion!=null) {
            val event= PlayerRegionEnterEvent(player, newRegion)
            event.callEvent()
            isCancelled= event.isCancelled
        }
    }
}