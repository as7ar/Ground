package kr.astar.ground.manager

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.astar.ground.Ground
import kr.astar.ground.data.GNData
import org.bukkit.entity.Player
import java.io.File

class GroundManager {
    init { load() }

    private val plugin= Ground.plugin
    private val gndPrefix= plugin.config.getString("region.prefix") ?: "GND"

    private val folder= File(plugin.dataFolder, "ground")
    private val file= File(folder, "data.json")

    private var data= JsonObject()

    private val gson =  GsonBuilder().setPrettyPrinting().create()

    private val MAX_OWN=plugin.config.getInt("region.max-own")
    private val MAX_MEMBER=plugin.config.getInt("region.max-members")

    fun addGround(gnddata: GNData) {
        val membersArray = JsonArray().apply {
            gnddata.members.forEach { add(it.toString()) }
        }

        val json = JsonObject().apply {
            addProperty("world", gnddata.world.toString())
            addProperty("owner", gnddata.owner.toString())
            add("members", membersArray)
        }

        data.add(gnddata.id, json)
        save()
    }

    fun getGround(id: String): GNData {
        return gson.fromJson(data[id], GNData::class.java)
    }

    private fun save() {
        file.writeText(gson.toJson(data), Charsets.UTF_8)
    }

    private fun load() {
        if (!folder.exists()) folder.mkdirs()
        if (!file.exists()) {
            file.writeText("{}", Charsets.UTF_8)
            data = JsonObject()
            return
        }

        runCatching {
            gson.fromJson(file.readText(Charsets.UTF_8), JsonObject::class.java)
        }.onSuccess {
            data = it ?: JsonObject()
        }.onFailure {
            it.printStackTrace()
            data = JsonObject()
        }
    }

}