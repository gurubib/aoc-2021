package day05

import readInputForDay

fun main() {
    fun part1(input: List<String>): Int {
        val sections = parseSections(input).filter { it.first.x == it.second.x || it.first.y == it.second.y }
        return countIntersections(sections)
    }

    fun part2(input: List<String>): Int {
        val sections = parseSections(input)
        return countIntersections(sections)
    }

    val testInput = readInputForDay(5, true)
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInputForDay(5)
    println(part1(input))
    println(part2(input))
}

private fun parseSections(input: List<String>): List<Pair<Point, Point>> {
    return input.map {
        val parts = it.split(" ")
        parsePoint(parts.first()) to parsePoint(parts.last())
    }
}

private data class Point(
    val x: Int = 0,
    val y: Int = 0,
)

private fun parsePoint(coords: String): Point {
    val parts = coords.split(",")
    return Point(parts.first().toInt(), -parts.last().toInt())
}


private class Line(
    private val start: Point,
    private val end: Point,
) {
    private val a = end.y - start.y
    private val b = start.x - end.x
    private val c = end.x * start.y - end.y * start.x

    fun points(): List<Point> {
        return if (start.x != end.x) {
            if (start.x <= end.x) {
                (start.x..end.x).map { x -> Point(x, y(x)) }
            } else {
                (end.x..start.x).map { x -> Point(x, y(x)) }
            }
        } else {
            if (start.y <= end.y) {
                (start.y..end.y).map { y -> Point(x(y), y) }
            } else {
                (end.y..start.y).map { y -> Point(x(y), y)}
            }
        }
    }

    private fun y(x: Int): Int {
        val num = -(a * x + c)
        val den = b.toDouble()
        return (num/den).toInt()
    }

    private fun x(y: Int): Int {
        val num = -(b * y + c)
        val den = a.toDouble()
        return (num/den).toInt()
    }
}

private fun countIntersections(sections: List<Pair<Point, Point>>): Int {
    val lines = sections.map { Line(it.first, it.second) }
    val points = mutableMapOf<Point, Int>()

    lines.flatMap { it.points() }.forEach { p ->
        val counter = points[p]
        if (counter != null) {
            points[p] = counter + 1
        } else {
            points[p] = 1
        }
    }

    return points.values.count { it > 1 }
}
