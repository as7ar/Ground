package kr.astar.ground.data

import java.util.*

data class UserData(
    val owned: Set<UUID>,
    val members: Set<UUID>
)
