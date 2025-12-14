package kr.astar.ground.data

import java.util.UUID

data class UerData(
    val owned: Set<UUID>,
    val members: Set<UUID>
)
