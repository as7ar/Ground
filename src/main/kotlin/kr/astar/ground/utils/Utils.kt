package kr.astar.ground.utils

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import kr.astar.ground.Ground
import kr.astar.ground.utils.Utils.prefix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object Utils {
    const val prefix= "<gradient:#B8DB80:#C9B59C> GROUND | </gradient>"
    private val groundManager= Ground.groundManager

    fun bannerGenerator(
        artLines: List<String>,
        version: String,
        author: String
    ): Array<String> {
        val innerWidth = artLines.maxOf { it.length }

        fun boxed(line: String): String =
            "|" + line.padEnd(innerWidth, ' ') + "|"

        val topBottom = "+" + "=".repeat(innerWidth) + "+"
        val separator = "|" + "=".repeat(innerWidth) + "|"

        val left = "Version: $version"
        val right = "author: $author"

        val spaceCount = (innerWidth - left.length - right.length)
            .coerceAtLeast(1)

        val info = left + " ".repeat(spaceCount) + right

        val result = mutableListOf<String>()
        result += topBottom
        result += artLines.map { boxed(it) }
        result += separator
        result += boxed(info)
        result += topBottom
        return result.toTypedArray()
    }


    private val wg = WorldGuard.getInstance()
    private val container = wg.platform.regionContainer
    fun Player.getRegion(): String {
        val region= this.getProtectedRegion() ?: return ""
        val currentRegion= region.id
        return currentRegion
    }

    fun Player.getProtectedRegion(): ProtectedRegion? {
        return this.location.getProtectedRegion()
    }

    fun Location.getRegion(): String {
        return this.getProtectedRegion()?.id ?: ""
    }

    fun Location.getProtectedRegion():  ProtectedRegion? {
        val world= BukkitAdapter.adapt(world)
        val regionManager= container[world] ?: return null
        val blockVector= BukkitAdapter.asBlockVector(this)
        val regions= regionManager.getApplicableRegions(blockVector)
        val region= regions.regions.maxByOrNull { it.priority }
        return region
    }

    fun Player.isInside(region: ProtectedRegion): Boolean {
        val loc= this.location
        val min = region.minimumPoint
        val max = region.maximumPoint

        return loc.blockX in min.x()..max.x() &&
                loc.blockY in min.y()..max.y() &&
                loc.blockZ in min.z()..max.z()
    }

    fun getRegionById(world: World, regionId: String): ProtectedRegion? {
        val wgWorld = BukkitAdapter.adapt(world)
        val regionManager = container[wgWorld] ?: return null
        return regionManager.getRegion(regionId)
    }

    fun OfflinePlayer.addCrew(crew: OfflinePlayer) {
        groundManager.getOwned(this.uniqueId).forEach {
            val gnd=groundManager.getGround(it)
            val world= Bukkit.getWorld(gnd.world) ?: return@forEach
            val region= getRegionById(world, gnd.id) ?: return@forEach
            region.members.addPlayer(crew.uniqueId)
        }
    }

    fun OfflinePlayer.removeCrew(crew: OfflinePlayer) {
        groundManager.getOwned(this.uniqueId).forEach {
            val gnd=groundManager.getGround(it)
            val world= Bukkit.getWorld(gnd.world) ?: return@forEach
            val region= getRegionById(world, gnd.id) ?: return@forEach
            region.members.removePlayer(crew.uniqueId)
        }
    }

    fun encodeItem(item: ItemStack): String {
        val output = ByteArrayOutputStream()
        BukkitObjectOutputStream(output).use { it.writeObject(item) }
        return Base64.getEncoder().encodeToString(output.toByteArray())
    }

    fun decodeItem(base64: String?): ItemStack? {
        if (base64==null) return null
        val input = ByteArrayInputStream(Base64.getDecoder().decode(base64))
        return BukkitObjectInputStream(input).use { it.readObject() as ItemStack }
    }
}

fun Player.sendMessage(component: Component, bool: Boolean=true) {
    if (bool) this.sendMessage(MiniMessage.miniMessage().deserialize(prefix).append(component))
    else this.sendMessage(component)
}