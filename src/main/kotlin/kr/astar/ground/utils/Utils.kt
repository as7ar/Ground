package kr.astar.ground.utils

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object Utils {
    const val prefix= "<gradient:#B8DB80:#C9B59C> GROUND </gradient>"

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
    fun Player.getRegion(): String? {
        val world= BukkitAdapter.adapt(world)
        val regionManager= container[world] ?: return null
        val blockVector= BukkitAdapter.asBlockVector(location)
        val regions= regionManager.getApplicableRegions(blockVector)
        val currentRegion= regions.regions.maxByOrNull { it.priority }?.id
        if (currentRegion!=null) return currentRegion
        return ""
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