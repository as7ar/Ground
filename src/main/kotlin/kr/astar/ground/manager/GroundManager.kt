package kr.astar.ground.manager

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.astar.ground.Ground
import kr.astar.ground.data.GNData
import java.io.File
import java.util.UUID

class GroundManager {
    private val plugin = Ground.plugin

    private val folder = File(plugin.dataFolder, "data")
    private val groundFile = File(folder, "ground.json")
    private val usersFile = File(folder, "user.json")

    private var groundData = JsonObject()
    private var usersData = JsonObject()

    private val gson = GsonBuilder().setPrettyPrinting().create()

    val gndPrefix = plugin.config.getString("region.prefix") ?: "GND"
    private val MAX_OWN = plugin.config.getInt("region.max-own")
    private val MAX_MEMBER = plugin.config.getInt("region.max-members")

    init { load() }

    fun addGround(gnddata: GNData) {
        val userKey = gnddata.owner.toString()

        val user = usersData[userKey]?.asJsonObject
            ?: JsonObject().also { usersData.add(userKey, it) }

        val owned = user["owned"]?.asJsonArray ?: JsonArray()

        if (owned.size() >= MAX_OWN) error("too many grounds")

        val groundJson = JsonObject().apply {
            addProperty("world", gnddata.world.toString())
            addProperty("owner", gnddata.owner.toString())
        }

        groundData.add(gnddata.id, groundJson)

        owned.add(gnddata.id)
        user.add("owned", owned)

        save()
    }

    fun getGround(id: String): GNData {
        val obj = groundData[id]?.asJsonObject ?: error("Ground not found: $id")

        return GNData(
            id = id,
            world = UUID.fromString(obj["world"].asString),
            owner = UUID.fromString(obj["owner"].asString)
        )
    }

    fun removeGround(regionId: String) {
        val ground = groundData[regionId]?.asJsonObject
            ?: error("Ground not found: $regionId")
        val owner = ground["owner"].asString

        groundData.remove(regionId)

        val user = usersData[owner]?.asJsonObject
            ?: error("Owner data not found: $owner")
        val owned = user["owned"]?.asJsonArray ?: JsonArray()
        val newOwned = JsonArray()

        owned.forEach {
            if (it.asString != regionId) newOwned.add(it)
        }
        user.add("owned", newOwned)
        save()
    }


    fun getOwned(player: UUID): Set<String> {
        val user = usersData[player.toString()]?.asJsonObject ?: return emptySet()
        val arr = user["owned"]?.asJsonArray ?: return emptySet()
        return arr.map { it.asString }.toSet()
    }

    fun getMembers(player: UUID): Set<UUID> {
        val user = usersData[player.toString()]?.asJsonObject ?: return emptySet()
        val arr = user["members"]?.asJsonArray ?: return emptySet()
        return arr.map { UUID.fromString(it.asString) }.toSet()
    }

    fun addMember(player: UUID, member: UUID) {
        val members=getMembers(player)
        if (members.contains(member)) return
        members.toMutableList().add(member)
        setMembers(player, members)
    }

    fun removeMember(player: UUID, member: UUID) {
        val members=getMembers(player)
        if (!members.contains(member)) return
        members.toMutableList().remove(member)
        setMembers(player, members)
    }

    fun setMembers(player: UUID, members: Set<UUID>) {
        if (members.size > MAX_MEMBER) error("too many members")

        val user = usersData[player.toString()]?.asJsonObject
            ?: JsonObject().also { usersData.add(player.toString(), it) }

        val arr = JsonArray().apply {
            members.forEach { add(it.toString()) }
        }

        user.add("members", arr)
        save()
    }

    private fun save() {
        groundFile.writeText(gson.toJson(groundData), Charsets.UTF_8)
        usersFile.writeText(gson.toJson(usersData), Charsets.UTF_8)
    }

    private fun load() {
        if (!folder.exists()) folder.mkdirs()
        groundData = loadJson(groundFile)
        usersData = loadJson(usersFile)
    }

    private fun loadJson(file: File): JsonObject {
        if (!file.exists()) {
            file.writeText("{}", Charsets.UTF_8)
            return JsonObject()
        }

        return runCatching {
            gson.fromJson(file.readText(Charsets.UTF_8), JsonObject::class.java)
        }.getOrElse {
            it.printStackTrace()
            JsonObject()
        }
    }
}