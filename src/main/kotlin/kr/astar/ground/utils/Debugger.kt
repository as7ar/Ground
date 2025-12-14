package kr.astar.ground.utils

import kr.astar.ground.Ground

object Debugger {
    fun debug(string: String) {
        Ground.plugin.logger.warning(string)
    }
}