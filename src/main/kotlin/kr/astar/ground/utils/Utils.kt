package kr.astar.ground.utils

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

        val info = "Version: $version     author: $author"
            .padEnd(innerWidth, ' ')

        val result = mutableListOf<String>()
        result += topBottom
        result += artLines.map { boxed(it) }
        result += separator
        result += boxed(info)
        result += topBottom
        return result.toTypedArray()
    }
}