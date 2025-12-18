package kr.astar.ground.manager

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.astar.ground.Ground
import kr.astar.ground.data.GNData
import kr.astar.ground.exception.GroundNotFound
import kr.astar.ground.utils.Utils.decodeItem
import kr.astar.ground.utils.Utils.encodeItem
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.UUID

class GroundManager {
    private val plugin = Ground.plugin

    // 데이터 파일 경로
    private val folder = File(plugin.dataFolder, "data")
    private val groundFile = File(folder, "ground.json")
    private val usersFile = File(folder, "user.json")
    private var itemFile = File(folder, "item")
    private var cwFile = File(folder, "custom_welcome.yml")

    // 데이터 객체 생성
    private var groundData = JsonObject()
    private var usersData = JsonObject()
    private var itemData = YamlConfiguration.loadConfiguration(itemFile)
    private var cwData = YamlConfiguration.loadConfiguration(cwFile)

    private val gson = GsonBuilder().setPrettyPrinting().create()

    val gndPrefix = plugin.config.getString("region.prefix") ?: "GND"
    val MAX_OWN = plugin.config.getInt("region.max-own")
    val MAX_MEMBER = plugin.config.getInt("region.max-members")

    init { load() }

    // 땅 등록
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

    // 땅 정보 가져오기
    fun getGround(id: String): GNData {
        val obj = groundData[id]?.asJsonObject ?: throw GroundNotFound(id)

        return GNData(
            id = id,
            world = UUID.fromString(obj["world"].asString),
            owner = UUID.fromString(obj["owner"].asString)
        )
    }

    // 모든 땅 목록 가져오기
    fun getGroundList(): List<GNData> {
        return groundData.entrySet().map { (key, value) ->
            val obj = value.asJsonObject
            GNData(
                id = key,
                world = UUID.fromString(obj["world"].asString),
                owner = UUID.fromString(obj["owner"].asString)
            )
        }
    }

    // 땅 정보 제거
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

    // 플레이어 소유 땅 목록
    fun getOwned(player: UUID): Set<String> {
        val user = usersData[player.toString()]?.asJsonObject ?: return emptySet()
        val arr = user["owned"]?.asJsonArray ?: return emptySet()
        return arr.map { it.asString }.toSet()
    }

    // 소유권 공유 멤버 목록
    fun getCrewList(player: UUID): Set<UUID> {
        val user = usersData[player.toString()]?.asJsonObject ?: return emptySet()
        val arr = user["members"]?.asJsonArray ?: return emptySet()
        return arr.map { UUID.fromString(it.asString) }.toSet()
    }

    // 소유권 공유 멤버 추가
    fun addCrew(player: UUID, member: UUID): Boolean {
        try {
            val members=getCrewList(player)
            if (members.contains(member)) return false
            members.toMutableList().add(member)
            setCrewList(player, members)
            return true
        } catch (_: Exception) {return false}
    }

    // 소유권 공유 멤버 제거
    fun removeCrew(player: UUID, member: UUID): Boolean {
        try {
            val members=getCrewList(player)
            if (!members.contains(member)) return false
            members.toMutableList().remove(member)
            setCrewList(player, members)
            return true
        } catch (_: Exception) {return false}
    }

    // 소유권 공유 멤버 목록 설정
    fun setCrewList(player: UUID, members: Set<UUID>) {
        if (members.size > MAX_MEMBER) error("too many members")

        val user = usersData[player.toString()]?.asJsonObject
            ?: JsonObject().also { usersData.add(player.toString(), it) }

        val arr = JsonArray().apply {
            members.forEach { add(it.toString()) }
        }

        user.add("members", arr)
        save()
    }

    // 아이템 불러오기
    fun getItem(string: String): ItemStack {
        itemFile = File(folder, "item")
        return decodeItem(itemData.getString(string))
            ?: error("Item not found: $string")
    }

    // 아이템 경로 설정
    fun setItem(string: String, item: ItemStack) {
        itemData.set(string, encodeItem(item))
        itemData.save(itemFile)
    }

    // 사용자 지정 환영 메세지
    fun getWelcomeContent(player: UUID): String {
        return cwData.getString(player.toString()) ?: ""
    }

    fun setWelcomeContent(player: UUID, content: String) {
        cwData.set(player.toString(), content)
        cwData.save(cwFile)
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