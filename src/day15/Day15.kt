package day15

import readInputForDay
import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        val risks = parse(input)
        risks[0][0] = 0
        return dijkstra(risks)
    }

    fun part2(input: List<String>): Int {
        val risks = parse(input)
        val allRisks = enlargeMap(risks)
        allRisks[0][0] = 0
        return dijkstra(allRisks)
    }

    val testInput = readInputForDay(15, true)
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInputForDay(15)
    println(part1(input))
    println(part2(input))
}

fun parse(input: List<String>): Array<Array<Int>> {
    val xSize = input.first().length
    val ySize = input.size
    val grid = Array(ySize) { Array(xSize) { 0 } }

    input.forEachIndexed { y, row -> row.toList().forEachIndexed { x, c -> grid[y][x] = c.digitToInt() } }
    return grid
}

val dx = listOf(-1, 0, 1, 0)
val dy = listOf(0, 1, 0, -1)

data class Cell(
    val x: Int,
    val y: Int,
    val d: Int,
)

fun dijkstra(risks: Array<Array<Int>>): Int {
    val xSize = risks.first().size
    val ySize = risks.size

    val dist = Array(ySize) { Array(xSize) { Int.MAX_VALUE } }
    dist[0][0] = risks[0][0]
    val cells = PriorityQueue(ySize * xSize) { one: Cell, other: Cell ->
        if (one.d < other.d) -1 else if (one.d > other.d) 1 else 0
    }

    cells.add(Cell(0, 0, dist[0][0]))
    while (cells.isNotEmpty()) {
        val currentCell = cells.poll()
        (0 until 4).forEach { i ->
            val rows = currentCell.x + dx[i]
            val cols = currentCell.y + dy[i]

            if (rows in 0 until xSize && cols in 0 until ySize) {
               if (dist[rows][cols] > dist[currentCell.x][currentCell.y] + risks[rows][cols]) {
                   if (dist[rows][cols] != Int.MAX_VALUE) {
                       val reachedCell = Cell(rows, cols, dist[rows][cols])
                       cells.remove(reachedCell)
                   }

                   dist[rows][cols] = dist[currentCell.x][currentCell.y] + risks[rows][cols]
                   cells.add(Cell(rows, cols, dist[rows][cols]))
               }
            }
        }
    }

    return dist[ySize - 1][xSize - 1]
}

fun enlargeMap(risks: Array<Array<Int>>): Array<Array<Int>> {
    val xSize = risks.first().size
    val ySize = risks.size
    val allRisks = Array(5 * ySize) { Array(5 * xSize) { 0 } }

    (0 until 5).forEach { yStep ->
        (0 until 5).forEach { xStep ->
            (0 until ySize).forEach { y ->
                (0 until xSize).forEach { x ->
                    val r = risks[y][x] + yStep + xStep
                    allRisks[ySize * yStep + y][xSize * xStep + x] = if (9 < r) r % 9  else r
                }
            }
        }
    }

    return allRisks
}