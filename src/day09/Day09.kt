package day09

import readInputForDay

fun main() {
    fun part1(input: List<String>): Int {
        val map = HeightMap.parse(input)
        return map.lowPoints().sumOf { it + 1 }
    }

    fun part2(input: List<String>): Int {
        val map = HeightMap.parse(input)
        return map.lowPointCoords()
            .map { map.findBasinSize(it) }
            .sortedDescending()
            .take(3)
            .reduce { acc, e -> acc * e }
    }

    val testInput = readInputForDay(9, true)
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInputForDay(9)
    println(part1(input))
    println(part2(input))
}

class HeightMap(
    private val points: List<List<Int>>,
) {
    companion object {
        fun parse(input: List<String>): HeightMap {
            val points = input.map { row -> row.toList().map { it.digitToInt() } }
            return HeightMap(points)
        }
    }

    private inline fun forEachIndexed(action: (x: Int, y: Int, p: Int) -> Unit) {
        for (y in points.indices) {
            for (x in points.first().indices) {
                action(x, y, points[y][x])
            }
        }
    }

    fun lowPoints(): List<Int> {
        val lows = mutableListOf<Int>()
        forEachIndexed { x, y, p ->
            val adjacentPoints = adjacentPoints(x, y)
            if (adjacentPoints.all { it > p }) {
                lows.add(p)
            }
        }

        return lows
    }

    fun lowPointCoords(): List<Pair<Int, Int>> {
        val lows = mutableListOf<Pair<Int, Int>>()
        forEachIndexed { x, y, p ->
            val adjacentPoints = adjacentPoints(x, y)
            if (adjacentPoints.all { it > p }) {
                lows.add(x to y)
            }
        }

        return lows
    }

    private fun adjacentPoints(x: Int, y: Int): List<Int> {
        return listOf(
            leftOf(x) to y,
            x to bottomOf(y),
            rightOf(x) to y,
            x to topOf(y),
        ).filterNot { it.first == -1 || it.second == -1 }
            .map { points[it.second][it.first] }
    }

    private fun adjacentCoords(x: Int, y: Int): List<Pair<Int, Int>> {
        return listOf(
            leftOf(x) to y,
            x to bottomOf(y),
            rightOf(x) to y,
            x to topOf(y),
        ).filterNot { it.first == -1 || it.second == -1 }
    }

    private fun leftOf(x: Int): Int {
        return if (x < 1) {
            -1
        } else {
            x - 1
        }
    }

    private fun rightOf(x: Int): Int {
        return if (points.first().size - 2 < x) {
            -1
        } else {
            x + 1
        }
    }

    private fun bottomOf(y: Int): Int {
        return if (y < 1) {
            -1
        } else {
            y - 1
        }
    }

    private fun topOf(y: Int): Int {
        return if (points.size - 2 < y) {
            -1
        } else {
            y + 1
        }
    }

    fun findBasinSize(lowest: Pair<Int, Int>): Int {
        val basin = points.map { row -> row.map { 0 }.toMutableList() }.toMutableList()
        basin[lowest.second][lowest.first] = 1

        seekBasin(lowest, basin)

        return basin.flatMap { row -> row.map { it } }.count { it == 1 }
    }



    private fun seekBasin(from: Pair<Int, Int>, basin: MutableList<MutableList<Int>>) {
        val lowPoint = points[from.second][from.first]
        adjacentCoords(from.first, from.second).forEach { coords ->
            val p = points[coords.second][coords.first]
            if (p in (lowPoint + 1)..8) {
                basin[coords.second][coords.first] = 1
                seekBasin(coords, basin)
            }
        }
    }
}
