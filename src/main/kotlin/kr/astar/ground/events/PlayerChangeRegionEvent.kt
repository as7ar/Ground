package kr.astar.ground.events

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

class PlayerChangeRegionEvent(
    val pastRegion: String?,
    val newRegion: String?,
    val player: Player?
) : Event(), Cancellable, Listener {

    private var cancelled = false
    override fun isCancelled() = cancelled
    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

    override fun getHandlers(): HandlerList = getHandlerList();

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList

        private val wg = WorldGuard.getInstance()
        private val container = wg.platform.regionContainer
        private val playerRegions = mutableMapOf<UUID, String?>()

    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        if (event.from.blockX == event.to.blockX &&
            event.from.blockY == event.to.blockY &&
            event.from.blockZ == event.to.blockZ) return

        val world = BukkitAdapter.adapt(player.world)
        val regionManager = container[world] ?: return
        val blockVector = BukkitAdapter.asBlockVector(player.location)
        val regions = regionManager.getApplicableRegions(blockVector)
        val currentRegion = regions.regions.maxByOrNull { it.priority }?.id
        val lastRegion = playerRegions[player.uniqueId]

        if (currentRegion != lastRegion) {
            val changeEvent = PlayerChangeRegionEvent(lastRegion, currentRegion, player)
            Bukkit.getPluginManager().callEvent(changeEvent)
            playerRegions[player.uniqueId] = currentRegion
            if (changeEvent.isCancelled) {
                event.isCancelled = true
            }
        }
    }
}