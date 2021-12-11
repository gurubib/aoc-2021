package day11

import readInputForDay

fun main() {
    fun part1(input: List<String>): Int {
        val octopusGrid = OctopusGrid.parse(input)
        repeat(100) {
            octopusGrid.step()
        }

        return octopusGrid.flashNum
    }

    fun part2(input: List<String>): Int {
        val octopusGrid = OctopusGrid.parse(input)
        return octopusGrid.calculateFirstSimultaneousFlash()
    }

    val testInput = readInputForDay(11, true)
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInputForDay(11)
    println(part1(input))
    println(part2(input))
}

class OctopusGrid(
    private val octopuses: List<MutableList<Int>>,
    private var stepNum: Int = 0,
    var flashNum: Int = 0,
) {
    companion object {
        fun parse(input: List<String>): OctopusGrid {
            val octopuses = input.map { row -> row.toList().map { it.digitToInt() }.toMutableList() }
            return OctopusGrid(octopuses)
        }
    }

    fun step() {
        stepNum++
        increaseByStep()
        allFlash()
    }

    private fun increaseByStep() {
        (0..9).forEach { y ->
            (0..9).forEach { x ->
                octopuses[y][x]++
            }
        }
    }

    private fun allFlash() {
        while (anyAboutToFlash()) {
            (0..9).forEach { y ->
                (0..9).forEach { x ->
                    if (9 < octopuses[y][x]) {
                        flashNum++
                        octopuses[y][x] = 0
                        adjacentCoords(x, y).forEach {
                            if (0 < octopuses[it.second][it.first]) {
                                octopuses[it.second][it.first]++
                            }
                        }
                    }
                }
            }
        }
    }

    private fun anyAboutToFlash(): Boolean = octopuses.any { row -> row.any { 9 < it } }

    private fun adjacentCoords(x: Int, y: Int): List<Pair<Int, Int>> {
        return listOf(
            x to y - 1,
            x - 1 to y - 1,
            x - 1 to y,
            x - 1 to y + 1,
            x to y + 1,
            x + 1 to y + 1,
            x + 1 to y,
            x + 1 to y - 1,
        ).filterNot { it.first < 0 || 9 < it.first || it.second < 0 || 9 < it.second }
    }

    fun calculateFirstSimultaneousFlash(): Int {
        while(!isSimultaneous()) {
            step()
        }

        return stepNum
    }

    private fun isSimultaneous(): Boolean = octopuses.all { row -> row.all { it == 0 } }
}
